package com.community.service;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.community.entity.WorkOrder;
import com.community.mapper.WorkOrderMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class WorkOrderService extends ServiceImpl<WorkOrderMapper, WorkOrder> {

    public Page<WorkOrder> pageQuery(Integer pageNum, Integer pageSize, String title, String orderType, Integer status) {
        return pageQuery(pageNum, pageSize, title, orderType, status, null, null, null, null);
    }

    public Page<WorkOrder> pageQuery(Integer pageNum, Integer pageSize, String title, String orderType, Integer status,
                                     Long assigneeId, Integer isOvertime, LocalDate deadlineStart, LocalDate deadlineEnd) {
        LambdaQueryWrapper<WorkOrder> wrapper = buildCommonWrapper(title, orderType, status, assigneeId, isOvertime, deadlineStart, deadlineEnd);
        return page(new Page<>(pageNum, pageSize), wrapper);
    }

    public Page<WorkOrder> pageQuery(Integer pageNum, Integer pageSize, String title, String orderType, Integer status, Long submitterId) {
        return pageQuery(pageNum, pageSize, title, orderType, status, submitterId, null, null, null, null);
    }

    public Page<WorkOrder> pageQuery(Integer pageNum, Integer pageSize, String title, String orderType, Integer status,
                                     Long submitterId, Long assigneeId, Integer isOvertime,
                                     LocalDate deadlineStart, LocalDate deadlineEnd) {
        LambdaQueryWrapper<WorkOrder> wrapper = buildCommonWrapper(title, orderType, status, assigneeId, isOvertime, deadlineStart, deadlineEnd);
        wrapper.eq(WorkOrder::getSubmitterId, submitterId);
        return page(new Page<>(pageNum, pageSize), wrapper);
    }

    public Page<WorkOrder> pageQueryForAdmin(Integer pageNum, Integer pageSize, String title, String orderType, Integer status) {
        return pageQueryForAdmin(pageNum, pageSize, title, orderType, status, null, null, null, null);
    }

    public Page<WorkOrder> pageQueryForAdmin(Integer pageNum, Integer pageSize, String title, String orderType, Integer status,
                                             Long assigneeId, Integer isOvertime,
                                             LocalDate deadlineStart, LocalDate deadlineEnd) {
        LambdaQueryWrapper<WorkOrder> wrapper = buildCommonWrapper(title, orderType, status, assigneeId, isOvertime, deadlineStart, deadlineEnd);
        return page(new Page<>(pageNum, pageSize), wrapper);
    }

    public long countByStatus(Integer status) {
        return count(new LambdaQueryWrapper<WorkOrder>().eq(WorkOrder::getStatus, status));
    }

    public List<Map<String, Object>> countByType() {
        List<Map<String, Object>> result = new ArrayList<>();
        String[] types = {"REPAIR", "COMPLAINT", "SUGGESTION", "OTHER"};
        String[] names = {"报修", "投诉", "建议", "其他"};
        for (int i = 0; i < types.length; i++) {
            Map<String, Object> item = new HashMap<>();
            item.put("type", types[i]);
            item.put("name", names[i]);
            item.put("count", count(new LambdaQueryWrapper<WorkOrder>().eq(WorkOrder::getOrderType, types[i])));
            result.add(item);
        }
        return result;
    }

    public List<Map<String, Object>> countByMonth() {
        List<Map<String, Object>> dbResult = baseMapper.countByMonth();

        List<Map<String, Object>> result = new ArrayList<>();
        LocalDate now = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");

        Map<String, Long> dbMap = new HashMap<>();
        for (Map<String, Object> item : dbResult) {
            dbMap.put((String) item.get("month"), ((Number) item.get("count")).longValue());
        }

        for (int i = 11; i >= 0; i--) {
            LocalDate date = now.minusMonths(i);
            String month = date.format(formatter);
            Map<String, Object> item = new HashMap<>();
            item.put("month", month);
            item.put("count", dbMap.getOrDefault(month, 0L));
            result.add(item);
        }

        return result;
    }

    public long countOvertimeOpen() {
        return baseMapper.countOvertimeOpen();
    }

    public long countTodayTodo() {
        return baseMapper.countTodayTodo();
    }

    public int scanAndMarkOvertime() {
        return baseMapper.markOvertime();
    }

    public void refreshOvertimeStatus(WorkOrder workOrder) {
        if (workOrder == null) {
            return;
        }
        if (workOrder.getDeadline() == null) {
            workOrder.setIsOvertime(0);
            return;
        }
        boolean openStatus = Objects.equals(workOrder.getStatus(), 0) || Objects.equals(workOrder.getStatus(), 1);
        workOrder.setIsOvertime(openStatus && workOrder.getDeadline().isBefore(LocalDateTime.now()) ? 1 : 0);
    }

    private LambdaQueryWrapper<WorkOrder> buildCommonWrapper(String title, String orderType, Integer status,
                                                             Long assigneeId, Integer isOvertime,
                                                             LocalDate deadlineStart, LocalDate deadlineEnd) {
        LambdaQueryWrapper<WorkOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StrUtil.isNotBlank(title), WorkOrder::getTitle, title);
        wrapper.eq(StrUtil.isNotBlank(orderType), WorkOrder::getOrderType, orderType);
        wrapper.eq(status != null, WorkOrder::getStatus, status);
        wrapper.eq(assigneeId != null, WorkOrder::getAssigneeId, assigneeId);
        wrapper.eq(isOvertime != null, WorkOrder::getIsOvertime, isOvertime);
        wrapper.ge(deadlineStart != null, WorkOrder::getDeadline, deadlineStart == null ? null : deadlineStart.atStartOfDay());
        wrapper.le(deadlineEnd != null, WorkOrder::getDeadline, deadlineEnd == null ? null : deadlineEnd.atTime(23, 59, 59));
        wrapper.orderByDesc(WorkOrder::getCreateTime);
        return wrapper;
    }
}
