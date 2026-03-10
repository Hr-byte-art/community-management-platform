package com.community.controller;

import com.community.annotation.Log;
import com.community.common.Constants;
import com.community.common.Result;
import com.community.entity.WorkOrder;
import com.community.service.GridDispatchRuleService;
import com.community.service.MessageNoticeService;
import com.community.service.ServiceEvaluationService;
import com.community.service.WorkOrderService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/workorder")
public class WorkOrderController {
    @Autowired
    private WorkOrderService workOrderService;
    @Autowired
    private MessageNoticeService messageNoticeService;
    @Autowired
    private GridDispatchRuleService gridDispatchRuleService;
    @Autowired
    private ServiceEvaluationService serviceEvaluationService;

    @GetMapping("/list")
    public Result<?> list(@RequestParam(defaultValue = "1") Integer pageNum,
                          @RequestParam(defaultValue = "10") Integer pageSize,
                          String title, String orderType, Integer status,
                          @RequestParam(required = false) Long assigneeId,
                          @RequestParam(required = false) Integer isOvertime,
                          @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate deadlineStart,
                          @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate deadlineEnd,
                          HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        String role = (String) request.getAttribute("role");

        if (Constants.Role.ADMIN.equals(role)) {
            return Result.success(workOrderService.pageQueryForAdmin(
                    pageNum, pageSize, title, orderType, status, assigneeId, isOvertime, deadlineStart, deadlineEnd));
        } else {
            var page = workOrderService.pageQuery(
                    pageNum, pageSize, title, orderType, status, userId, assigneeId, isOvertime, deadlineStart, deadlineEnd);
            serviceEvaluationService.markEvaluatedFlag(page.getRecords(), userId);
            return Result.success(page);
        }
    }

    @GetMapping("/{id}")
    public Result<?> getById(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        String role = (String) request.getAttribute("role");

        WorkOrder workOrder = workOrderService.getById(id);
        if (workOrder == null) {
            return Result.error("工单不存在");
        }

        if (!Constants.Role.ADMIN.equals(role) && !workOrder.getSubmitterId().equals(userId)) {
            return Result.error("无权限查看该工单");
        }

        return Result.success(workOrder);
    }

    @Log("Submit Work Order")
    @PostMapping
    @Transactional
    public Result<?> add(@RequestBody WorkOrder workOrder, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        workOrder.setSubmitterId(userId);
        workOrder.setStatus(Constants.WorkOrderStatus.PENDING);
        if (workOrder.getIsOvertime() == null) {
            workOrder.setIsOvertime(0);
        }
        if (workOrder.getAssigneeId() == null) {
            Long assigneeId = gridDispatchRuleService.matchAssigneeId(workOrder.getOrderType(), workOrder.getPriority());
            if (assigneeId != null) {
                workOrder.setAssigneeId(assigneeId);
            }
        }
        workOrderService.refreshOvertimeStatus(workOrder);
        workOrderService.save(workOrder);

        if (workOrder.getAssigneeId() != null) {
            messageNoticeService.sendMessage(
                    workOrder.getAssigneeId(),
                    "有新的工单待处理",
                    "系统已为您自动派单：[" + workOrder.getTitle() + "]，请及时处理。",
                    Constants.MessageType.SYSTEM,
                    Constants.MessageBusinessType.WORK_ORDER,
                    workOrder.getId()
            );
        }
        return Result.success();
    }

    @Log("Update Work Order")
    @PutMapping("/{id}")
    @Transactional
    public Result<?> update(@PathVariable Long id, @RequestBody WorkOrder workOrder, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        String role = (String) request.getAttribute("role");

        WorkOrder existingOrder = workOrderService.getById(id);
        if (existingOrder == null) {
            return Result.error("工单不存在");
        }

        if (!existingOrder.getSubmitterId().equals(userId) && !Constants.Role.ADMIN.equals(role)) {
            return Result.error("无权限更新该工单");
        }

        workOrder.setId(id);
        workOrder.setSubmitterId(existingOrder.getSubmitterId());

        if (!Constants.Role.ADMIN.equals(role)) {
            workOrder.setStatus(existingOrder.getStatus());
            workOrder.setHandlerId(existingOrder.getHandlerId());
            workOrder.setAssigneeId(existingOrder.getAssigneeId());
            workOrder.setHandleResult(existingOrder.getHandleResult());
            workOrder.setHandleTime(existingOrder.getHandleTime());
        }

        workOrderService.refreshOvertimeStatus(workOrder);
        workOrderService.updateById(workOrder);
        return Result.success();
    }

    @Log("Handle Work Order")
    @PutMapping("/{id}/handle")
    @Transactional
    public Result<?> handle(@PathVariable Long id, @RequestBody WorkOrder workOrder, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        String role = (String) request.getAttribute("role");

        if (!Constants.Role.ADMIN.equals(role)) {
            return Result.error("无权限处理工单");
        }

        WorkOrder order = workOrderService.getById(id);
        if (order == null) {
            return Result.error("工单不存在");
        }

        Integer oldStatus = order.getStatus();

        order.setHandlerId(userId);
        if (order.getAssigneeId() == null) {
            order.setAssigneeId(userId);
        }
        if (workOrder.getAssigneeId() != null) {
            order.setAssigneeId(workOrder.getAssigneeId());
        }
        if (workOrder.getDeadline() != null) {
            order.setDeadline(workOrder.getDeadline());
        }
        order.setStatus(workOrder.getStatus());
        order.setHandleResult(workOrder.getHandleResult());
        order.setHandleTime(LocalDateTime.now());
        workOrderService.refreshOvertimeStatus(order);
        workOrderService.updateById(order);

        if (!Constants.WorkOrderStatus.COMPLETED.equals(oldStatus)
                && Constants.WorkOrderStatus.COMPLETED.equals(order.getStatus())) {
            messageNoticeService.sendMessage(
                    order.getSubmitterId(),
                    "工单已完成",
                    "您提交的工单[" + order.getTitle() + "]已处理完成。",
                    Constants.MessageType.WORK_ORDER,
                    Constants.MessageBusinessType.WORK_ORDER,
                    order.getId()
            );
        }

        return Result.success();
    }

    @Log("Delete Work Order")
    @DeleteMapping("/{id}")
    @Transactional
    public Result<?> delete(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        String role = (String) request.getAttribute("role");

        WorkOrder workOrder = workOrderService.getById(id);
        if (workOrder == null) {
            return Result.error("工单不存在");
        }

        if (!workOrder.getSubmitterId().equals(userId) && !Constants.Role.ADMIN.equals(role)) {
            return Result.error("无权限删除该工单");
        }

        if (!Constants.WorkOrderStatus.PENDING.equals(workOrder.getStatus())) {
            return Result.error("已处理工单不可删除");
        }

        workOrderService.removeById(id);
        return Result.success();
    }
}
