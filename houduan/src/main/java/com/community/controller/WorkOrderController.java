package com.community.controller;

import cn.hutool.core.util.StrUtil;
import com.community.annotation.Auth;
import com.community.annotation.Log;
import com.community.ai.dto.WorkOrderEnhanceRequest;
import com.community.ai.service.AIService;
import com.community.common.Constants;
import com.community.common.Result;
import com.community.entity.SysUser;
import com.community.entity.WorkOrder;
import com.community.service.GridDispatchRuleService;
import com.community.service.MessageNoticeService;
import com.community.service.ServiceEvaluationService;
import com.community.service.SysPermissionService;
import com.community.service.SysUserService;
import com.community.service.WorkOrderService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/workorder")
public class WorkOrderController {
    private static final Logger logger = LoggerFactory.getLogger(WorkOrderController.class);
    private static final String AI_TRACE_ID_HEADER = "X-AI-Trace-Id";

    @Autowired
    private WorkOrderService workOrderService;
    @Autowired
    private MessageNoticeService messageNoticeService;
    @Autowired
    private GridDispatchRuleService gridDispatchRuleService;
    @Autowired
    private ServiceEvaluationService serviceEvaluationService;
    @Autowired
    private SysPermissionService sysPermissionService;
    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private AIService aiService;

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
        boolean canViewAll = sysPermissionService.hasPermission(role, "scope.workorder.all", Constants.Role.ADMIN.equals(role));

        if (canViewAll) {
            var page = workOrderService.pageQueryForAdmin(
                    pageNum, pageSize, title, orderType, status, assigneeId, isOvertime, deadlineStart, deadlineEnd);
            fillAssigneeNames(page.getRecords());
            return Result.success(page);
        } else {
            var page = workOrderService.pageQuery(
                    pageNum, pageSize, title, orderType, status, userId, assigneeId, isOvertime, deadlineStart, deadlineEnd);
            serviceEvaluationService.markEvaluatedFlag(page.getRecords(), userId);
            fillAssigneeNames(page.getRecords());
            return Result.success(page);
        }
    }

    @GetMapping("/{id}")
    public Result<?> getById(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        String role = (String) request.getAttribute("role");
        boolean canViewAll = sysPermissionService.hasPermission(role, "scope.workorder.all", Constants.Role.ADMIN.equals(role));

        WorkOrder workOrder = workOrderService.getById(id);
        if (workOrder == null) {
            return Result.error("\u5de5\u5355\u4e0d\u5b58\u5728");
        }

        if (!canViewAll && !workOrder.getSubmitterId().equals(userId)) {
            return Result.error("\u65e0\u6743\u9650\u67e5\u770b\u8be5\u5de5\u5355");
        }

        fillAssigneeNames(List.of(workOrder));
        return Result.success(workOrder);
    }

    @Auth(value = "", permissions = {"btn.workorder.add", "btn.workorder.edit"}, requireAllPermissions = false)
    @PostMapping("/ai/complete")
    public Result<?> enhance(@RequestBody WorkOrderEnhanceRequest enhanceRequest, HttpServletRequest httpRequest) {
        String traceId = StrUtil.blankToDefault(httpRequest.getHeader(AI_TRACE_ID_HEADER), "unknown");
        long startTime = System.currentTimeMillis();
        if (enhanceRequest == null || StrUtil.isAllBlank(enhanceRequest.getTitle(), enhanceRequest.getContent())) {
            logger.warn("[ai-complete][backend] traceId={} stage=controller-exit scene=workorder costMs={} success=false reason=empty_input",
                    traceId,
                    System.currentTimeMillis() - startTime);
            return Result.error(400, "\u8bf7\u5148\u8f93\u5165\u5de5\u5355\u6807\u9898\u6216\u95ee\u9898\u63cf\u8ff0");
        }
        logger.info("[ai-complete][backend] traceId={} stage=controller-enter scene=workorder uri={} titleChars={} contentChars={}",
                traceId,
                httpRequest.getRequestURI(),
                enhanceRequest.getTitle() == null ? 0 : enhanceRequest.getTitle().length(),
                enhanceRequest.getContent() == null ? 0 : enhanceRequest.getContent().length());
        try {
            Result<?> result = Result.success(aiService.enhanceWorkOrder(enhanceRequest));
            logger.info("[ai-complete][backend] traceId={} stage=controller-exit scene=workorder costMs={} success=true",
                    traceId,
                    System.currentTimeMillis() - startTime);
            return result;
        } catch (IllegalStateException e) {
            logger.warn("[ai-complete][backend] traceId={} stage=controller-exit scene=workorder costMs={} success=false message={}",
                    traceId,
                    System.currentTimeMillis() - startTime,
                    e.getMessage());
            return Result.error(e.getMessage());
        } catch (Exception e) {
            logger.error("[ai-complete][backend] traceId={} stage=controller-exit scene=workorder costMs={} success=false",
                    traceId,
                    System.currentTimeMillis() - startTime,
                    e);
            return Result.error("AI \u5b8c\u5584\u5de5\u5355\u4fe1\u606f\u5931\u8d25\uff0c\u8bf7\u7a0d\u540e\u91cd\u8bd5");
        }
    }

    @Log("Submit Work Order")
    @Auth(value = "", permissions = {"btn.workorder.add"})
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
                    "\u6709\u65b0\u7684\u5de5\u5355\u5f85\u5904\u7406",
                    "\u7cfb\u7edf\u5df2\u4e3a\u60a8\u81ea\u52a8\u6d3e\u5355\uff1a[" + workOrder.getTitle() + "]\uff0c\u8bf7\u53ca\u65f6\u5904\u7406\u3002",
                    Constants.MessageType.SYSTEM,
                    Constants.MessageBusinessType.WORK_ORDER,
                    workOrder.getId()
            );
        }
        return Result.success();
    }

    @Log("Update Work Order")
    @Auth(value = "", permissions = {"btn.workorder.edit"})
    @PutMapping("/{id}")
    @Transactional
    public Result<?> update(@PathVariable Long id, @RequestBody WorkOrder workOrder, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        String role = (String) request.getAttribute("role");
        boolean canViewAll = sysPermissionService.hasPermission(role, "scope.workorder.all", Constants.Role.ADMIN.equals(role));

        WorkOrder existingOrder = workOrderService.getById(id);
        if (existingOrder == null) {
            return Result.error("\u5de5\u5355\u4e0d\u5b58\u5728");
        }

        if (!existingOrder.getSubmitterId().equals(userId) && !canViewAll) {
            return Result.error("\u65e0\u6743\u9650\u66f4\u65b0\u8be5\u5de5\u5355");
        }

        workOrder.setId(id);
        workOrder.setSubmitterId(existingOrder.getSubmitterId());

        if (!canViewAll) {
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
    @Auth(value = "", permissions = {"btn.workorder.handle"})
    @PutMapping("/{id}/handle")
    @Transactional
    public Result<?> handle(@PathVariable Long id, @RequestBody WorkOrder workOrder, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        String role = (String) request.getAttribute("role");
        boolean canViewAll = sysPermissionService.hasPermission(role, "scope.workorder.all", Constants.Role.ADMIN.equals(role));

        if (!Constants.Role.ADMIN.equals(role)) {
            return Result.error("\u65e0\u6743\u9650\u5904\u7406\u5de5\u5355");
        }

        WorkOrder order = workOrderService.getById(id);
        if (order == null) {
            return Result.error("\u5de5\u5355\u4e0d\u5b58\u5728");
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
                    "\u5de5\u5355\u5df2\u5b8c\u6210",
                    "\u60a8\u63d0\u4ea4\u7684\u5de5\u5355[" + order.getTitle() + "]\u5df2\u5904\u7406\u5b8c\u6210\u3002",
                    Constants.MessageType.WORK_ORDER,
                    Constants.MessageBusinessType.WORK_ORDER,
                    order.getId()
            );
        }

        return Result.success();
    }

    @Log("Delete Work Order")
    @Auth(value = "", permissions = {"btn.workorder.delete"})
    @DeleteMapping("/{id}")
    @Transactional
    public Result<?> delete(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        String role = (String) request.getAttribute("role");
        boolean canViewAll = sysPermissionService.hasPermission(role, "scope.workorder.all", Constants.Role.ADMIN.equals(role));

        WorkOrder workOrder = workOrderService.getById(id);
        if (workOrder == null) {
            return Result.error("\u5de5\u5355\u4e0d\u5b58\u5728");
        }

        if (!workOrder.getSubmitterId().equals(userId) && !canViewAll) {
            return Result.error("\u65e0\u6743\u9650\u5220\u9664\u8be5\u5de5\u5355");
        }

        if (!Constants.WorkOrderStatus.PENDING.equals(workOrder.getStatus())) {
            return Result.error("\u5df2\u5904\u7406\u5de5\u5355\u4e0d\u53ef\u5220\u9664");
        }

        workOrderService.removeById(id);
        return Result.success();
    }

    private void fillAssigneeNames(List<WorkOrder> workOrders) {
        if (workOrders == null || workOrders.isEmpty()) {
            return;
        }

        List<Long> assigneeIds = workOrders.stream()
                .map(WorkOrder::getAssigneeId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();
        if (assigneeIds.isEmpty()) {
            return;
        }

        Map<Long, String> assigneeMap = sysUserService.listByIds(assigneeIds).stream()
                .collect(Collectors.toMap(
                        SysUser::getId,
                        item -> item.getRealName() != null && !item.getRealName().isBlank() ? item.getRealName() : item.getUsername(),
                        (left, right) -> left
                ));

        workOrders.forEach(item -> item.setAssigneeName(assigneeMap.get(item.getAssigneeId())));
    }
}
