package com.community.service;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.community.common.Constants;
import com.community.entity.ServiceEvaluation;
import com.community.entity.WorkOrder;
import com.community.mapper.ServiceEvaluationMapper;
import com.community.mapper.WorkOrderMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ServiceEvaluationService extends ServiceImpl<ServiceEvaluationMapper, ServiceEvaluation> {

    private final WorkOrderMapper workOrderMapper;

    public ServiceEvaluationService(WorkOrderMapper workOrderMapper) {
        this.workOrderMapper = workOrderMapper;
    }

    public String validateForSubmit(Long userId, Long workOrderId, Integer score) {
        if (userId == null || workOrderId == null) {
            return "参数不完整";
        }
        if (score == null || score < 1 || score > 5) {
            return "评分必须在1到5之间";
        }

        WorkOrder workOrder = workOrderMapper.selectById(workOrderId);
        if (workOrder == null) {
            return "工单不存在";
        }
        if (!Objects.equals(workOrder.getSubmitterId(), userId)) {
            return "仅工单提交人可评价";
        }
        if (!Objects.equals(workOrder.getStatus(), Constants.WorkOrderStatus.COMPLETED)
                && !Objects.equals(workOrder.getStatus(), Constants.WorkOrderStatus.CLOSED)) {
            return "仅已完成或已关闭工单可评价";
        }
        return null;
    }

    public boolean submitOrUpdate(Long userId, Long workOrderId, Integer score, String content) {
        LambdaQueryWrapper<ServiceEvaluation> wrapper = new LambdaQueryWrapper<ServiceEvaluation>()
                .eq(ServiceEvaluation::getUserId, userId)
                .eq(ServiceEvaluation::getWorkOrderId, workOrderId)
                .last("limit 1");

        ServiceEvaluation existing = getOne(wrapper, false);
        if (existing == null) {
            ServiceEvaluation entity = new ServiceEvaluation();
            entity.setUserId(userId);
            entity.setWorkOrderId(workOrderId);
            entity.setScore(score);
            entity.setContent(StrUtil.maxLength(content == null ? "" : content.trim(), 500));
            return save(entity);
        }

        existing.setScore(score);
        existing.setContent(StrUtil.maxLength(content == null ? "" : content.trim(), 500));
        return updateById(existing);
    }

    public ServiceEvaluation getByUserAndWorkOrder(Long userId, Long workOrderId) {
        return getOne(new LambdaQueryWrapper<ServiceEvaluation>()
                .eq(ServiceEvaluation::getUserId, userId)
                .eq(ServiceEvaluation::getWorkOrderId, workOrderId)
                .last("limit 1"), false);
    }

    public Page<ServiceEvaluation> pageByUser(Long userId, Integer pageNum, Integer pageSize) {
        Page<ServiceEvaluation> page = page(new Page<>(pageNum, pageSize),
                new LambdaQueryWrapper<ServiceEvaluation>()
                        .eq(ServiceEvaluation::getUserId, userId)
                        .orderByDesc(ServiceEvaluation::getCreateTime));
        fillWorkOrderTitle(page.getRecords());
        return page;
    }

    public Page<ServiceEvaluation> pageAll(Integer pageNum, Integer pageSize, Integer score, Long userId) {
        LambdaQueryWrapper<ServiceEvaluation> wrapper = new LambdaQueryWrapper<ServiceEvaluation>()
                .eq(score != null, ServiceEvaluation::getScore, score)
                .eq(userId != null, ServiceEvaluation::getUserId, userId)
                .orderByDesc(ServiceEvaluation::getCreateTime);
        Page<ServiceEvaluation> page = page(new Page<>(pageNum, pageSize), wrapper);
        fillWorkOrderTitle(page.getRecords());
        return page;
    }

    public Map<String, Object> stats() {
        long total = count();
        long score5 = count(new LambdaQueryWrapper<ServiceEvaluation>().eq(ServiceEvaluation::getScore, 5));
        long score4 = count(new LambdaQueryWrapper<ServiceEvaluation>().eq(ServiceEvaluation::getScore, 4));
        long score3 = count(new LambdaQueryWrapper<ServiceEvaluation>().eq(ServiceEvaluation::getScore, 3));
        long score2 = count(new LambdaQueryWrapper<ServiceEvaluation>().eq(ServiceEvaluation::getScore, 2));
        long score1 = count(new LambdaQueryWrapper<ServiceEvaluation>().eq(ServiceEvaluation::getScore, 1));

        Double avgScore = baseMapper.selectList(new LambdaQueryWrapper<ServiceEvaluation>().select(ServiceEvaluation::getScore))
                .stream()
                .map(ServiceEvaluation::getScore)
                .filter(Objects::nonNull)
                .mapToInt(Integer::intValue)
                .average()
                .orElse(0.0);

        return Map.of(
                "total", total,
                "avgScore", Math.round(avgScore * 100.0) / 100.0,
                "score5", score5,
                "score4", score4,
                "score3", score3,
                "score2", score2,
                "score1", score1,
                "satisfactionRate", total == 0 ? 0 : Math.round((double) (score4 + score5) * 10000 / total) / 100.0
        );
    }

    public void fillWorkOrderTitle(List<ServiceEvaluation> evaluations) {
        if (evaluations == null || evaluations.isEmpty()) {
            return;
        }
        List<Long> workOrderIds = evaluations.stream()
                .map(ServiceEvaluation::getWorkOrderId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();
        if (workOrderIds.isEmpty()) {
            return;
        }

        Map<Long, String> titleMap = workOrderMapper.selectBatchIds(workOrderIds).stream()
                .collect(Collectors.toMap(WorkOrder::getId, WorkOrder::getTitle, (a, b) -> a));

        evaluations.forEach(item -> item.setWorkOrderTitle(titleMap.get(item.getWorkOrderId())));
    }

    public void markEvaluatedFlag(List<WorkOrder> workOrders, Long userId) {
        if (workOrders == null || workOrders.isEmpty() || userId == null) {
            return;
        }
        List<Long> workOrderIds = workOrders.stream().map(WorkOrder::getId).filter(Objects::nonNull).toList();
        if (workOrderIds.isEmpty()) {
            return;
        }
        Map<Long, ServiceEvaluation> map = list(new LambdaQueryWrapper<ServiceEvaluation>()
                .eq(ServiceEvaluation::getUserId, userId)
                .in(ServiceEvaluation::getWorkOrderId, workOrderIds))
                .stream()
                .collect(Collectors.toMap(ServiceEvaluation::getWorkOrderId, Function.identity(), (a, b) -> a));

        workOrders.forEach(item -> item.setEvaluated(map.containsKey(item.getId()) ? 1 : 0));
    }
}
