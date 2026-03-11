package com.community.controller;

import com.community.annotation.Auth;
import com.community.common.Constants;
import com.community.common.Result;
import com.community.entity.ServiceEvaluation;
import com.community.entity.WorkOrder;
import com.community.service.ServiceEvaluationService;
import com.community.service.WorkOrderService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/evaluation")
public class ServiceEvaluationController {

    private final ServiceEvaluationService serviceEvaluationService;
    private final WorkOrderService workOrderService;

    public ServiceEvaluationController(ServiceEvaluationService serviceEvaluationService, WorkOrderService workOrderService) {
        this.serviceEvaluationService = serviceEvaluationService;
        this.workOrderService = workOrderService;
    }

    @Auth(value = "", permissions = {"btn.evaluation.submit"})
    @PostMapping("/workorder/{workOrderId}")
    public Result<?> submit(@PathVariable Long workOrderId,
                            @RequestBody ServiceEvaluation payload,
                            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        String error = serviceEvaluationService.validateForSubmit(userId, workOrderId, payload.getScore());
        if (error != null) {
            return Result.error(error);
        }

        boolean ok = serviceEvaluationService.submitOrUpdate(userId, workOrderId, payload.getScore(), payload.getContent());
        return ok ? Result.success() : Result.error("评价提交失败");
    }

    @GetMapping("/workorder/{workOrderId}")
    public Result<?> getMyEvaluation(@PathVariable Long workOrderId, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(serviceEvaluationService.getByUserAndWorkOrder(userId, workOrderId));
    }

    @GetMapping("/my")
    public Result<?> myEvaluations(@RequestParam(defaultValue = "1") Integer pageNum,
                                   @RequestParam(defaultValue = "10") Integer pageSize,
                                   HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(serviceEvaluationService.pageByUser(userId, pageNum, pageSize));
    }

    @GetMapping("/my/workorders")
    public Result<?> myWorkOrders(@RequestParam(defaultValue = "1") Integer pageNum,
                                  @RequestParam(defaultValue = "10") Integer pageSize,
                                  @RequestParam(required = false) String title,
                                  @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate deadlineStart,
                                  @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate deadlineEnd,
                                  HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        var page = workOrderService.pageQuery(pageNum, pageSize, title, null, Constants.WorkOrderStatus.COMPLETED,
                userId, null, null, deadlineStart, deadlineEnd);
        serviceEvaluationService.markEvaluatedFlag(page.getRecords(), userId);
        return Result.success(page);
    }

    @Auth(permissions = {"btn.evaluation.admin_list", "scope.evaluation.all"})
    @GetMapping("/admin/list")
    public Result<?> adminList(@RequestParam(defaultValue = "1") Integer pageNum,
                               @RequestParam(defaultValue = "10") Integer pageSize,
                               @RequestParam(required = false) Integer score,
                               @RequestParam(required = false) Long userId) {
        return Result.success(serviceEvaluationService.pageAll(pageNum, pageSize, score, userId));
    }

    @Auth(permissions = {"btn.evaluation.stats", "scope.evaluation.all"})
    @GetMapping("/stats")
    public Result<?> stats() {
        return Result.success(serviceEvaluationService.stats());
    }
}
