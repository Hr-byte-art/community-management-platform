package com.community.task;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.community.common.Constants;
import com.community.entity.FloatingPopulation;
import com.community.service.FloatingPopulationService;
import com.community.service.WorkOrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

/**
 * 定时任务
 */
@Component
public class ScheduledTasks {

    private static final Logger logger = LoggerFactory.getLogger(ScheduledTasks.class);

    @Autowired
    private FloatingPopulationService floatingPopulationService;

    @Autowired
    private WorkOrderService workOrderService;

    /**
     * 每天凌晨 2 点检查流动人口到期状态
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void checkFloatingPopulationExpiration() {
        logger.info("开始检查流动人口到期状态");

        try {
            LocalDate today = LocalDate.now();
            LocalDate sevenDaysLater = today.plusDays(7);

            List<FloatingPopulation> expiringPopulations = floatingPopulationService.list(
                    new LambdaQueryWrapper<FloatingPopulation>()
                            .between(FloatingPopulation::getExpectedLeaveDate, today, sevenDaysLater)
                            .eq(FloatingPopulation::getStatus, Constants.FloatingPopulationStatus.IN_RESIDENCE)
            );

            logger.info("7天内到期流动人口数量: {}", expiringPopulations.size());

            List<FloatingPopulation> overduePopulations = floatingPopulationService.list(
                    new LambdaQueryWrapper<FloatingPopulation>()
                            .lt(FloatingPopulation::getExpectedLeaveDate, today)
                            .eq(FloatingPopulation::getStatus, Constants.FloatingPopulationStatus.IN_RESIDENCE)
            );

            logger.info("已逾期流动人口数量: {}", overduePopulations.size());

            for (FloatingPopulation population : overduePopulations) {
                population.setStatus(Constants.FloatingPopulationStatus.LEFT);
                floatingPopulationService.updateById(population);
            }
        } catch (Exception e) {
            logger.error("检查流动人口到期状态失败", e);
        }

        logger.info("结束检查流动人口到期状态");
    }

    /**
     * 每小时扫描一次超时工单
     */
    @Scheduled(cron = "0 0 * * * ?")
    public void scanOvertimeWorkOrders() {
        try {
            int updated = workOrderService.scanAndMarkOvertime();
            if (updated > 0) {
                logger.info("超时工单扫描完成，新增超时标记数量: {}", updated);
            }
        } catch (Exception e) {
            logger.error("超时工单扫描失败", e);
        }
    }
}
