package com.community.controller;

import cn.hutool.core.util.StrUtil;
import com.community.annotation.Auth;
import com.community.annotation.Log;
import com.community.ai.dto.ServiceAppointmentEnhanceRequest;
import com.community.ai.service.AIService;
import com.community.common.Result;
import com.community.entity.ServiceAppointment;
import com.community.service.ServiceAppointmentService;
import com.community.service.SysPermissionService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/appointment")
public class ServiceAppointmentController {
    private static final Logger logger = LoggerFactory.getLogger(ServiceAppointmentController.class);
    private static final String AI_TRACE_ID_HEADER = "X-AI-Trace-Id";

    @Autowired
    private ServiceAppointmentService appointmentService;
    @Autowired
    private SysPermissionService sysPermissionService;
    @Autowired
    private AIService aiService;

    @GetMapping("/list")
    public Result<?> list(@RequestParam(defaultValue = "1") Integer pageNum,
                          @RequestParam(defaultValue = "10") Integer pageSize,
                          String serviceType, Integer status,
                          HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        String role = (String) request.getAttribute("role");
        boolean canViewAll = sysPermissionService.hasPermission(role, "scope.appointment.all", "ADMIN".equals(role));
        return Result.success(appointmentService.pageQuery(pageNum, pageSize, serviceType, status, canViewAll ? null : userId));
    }

    @GetMapping("/my")
    public Result<?> myList(@RequestParam(defaultValue = "1") Integer pageNum,
                            @RequestParam(defaultValue = "10") Integer pageSize,
                            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(appointmentService.pageQuery(pageNum, pageSize, null, null, userId));
    }

    @GetMapping("/{id}")
    public Result<?> getById(@PathVariable Long id, HttpServletRequest request) {
        ServiceAppointment appointment = appointmentService.getById(id);
        if (appointment == null) {
            return Result.error("\u9884\u7ea6\u4e0d\u5b58\u5728");
        }
        Long userId = (Long) request.getAttribute("userId");
        String role = (String) request.getAttribute("role");
        boolean canViewAll = sysPermissionService.hasPermission(role, "scope.appointment.all", "ADMIN".equals(role));
        if (!canViewAll && !appointment.getUserId().equals(userId)) {
            return Result.error("\u65e0\u6743\u9650\u67e5\u770b\u8be5\u9884\u7ea6");
        }
        return Result.success(appointment);
    }

    @Auth(value = "", permissions = {"btn.appointment.add", "btn.appointment.edit"}, requireAllPermissions = false)
    @PostMapping("/ai/complete")
    public Result<?> enhance(@RequestBody ServiceAppointmentEnhanceRequest enhanceRequest, HttpServletRequest httpRequest) {
        String traceId = StrUtil.blankToDefault(httpRequest.getHeader(AI_TRACE_ID_HEADER), "unknown");
        long startTime = System.currentTimeMillis();
        if (enhanceRequest == null || StrUtil.isAllBlank(enhanceRequest.getTitle(), enhanceRequest.getContent())) {
            logger.warn("[ai-complete][backend] traceId={} stage=controller-exit scene=appointment costMs={} success=false reason=empty_input",
                    traceId,
                    System.currentTimeMillis() - startTime);
            return Result.error(400, "\u8bf7\u5148\u8f93\u5165\u9884\u7ea6\u6807\u9898\u6216\u9884\u7ea6\u5185\u5bb9");
        }
        logger.info("[ai-complete][backend] traceId={} stage=controller-enter scene=appointment uri={} titleChars={} contentChars={}",
                traceId,
                httpRequest.getRequestURI(),
                enhanceRequest.getTitle() == null ? 0 : enhanceRequest.getTitle().length(),
                enhanceRequest.getContent() == null ? 0 : enhanceRequest.getContent().length());
        try {
            Result<?> result = Result.success(aiService.enhanceServiceAppointment(enhanceRequest));
            logger.info("[ai-complete][backend] traceId={} stage=controller-exit scene=appointment costMs={} success=true",
                    traceId,
                    System.currentTimeMillis() - startTime);
            return result;
        } catch (IllegalStateException e) {
            logger.warn("[ai-complete][backend] traceId={} stage=controller-exit scene=appointment costMs={} success=false message={}",
                    traceId,
                    System.currentTimeMillis() - startTime,
                    e.getMessage());
            return Result.error(e.getMessage());
        } catch (Exception e) {
            logger.error("[ai-complete][backend] traceId={} stage=controller-exit scene=appointment costMs={} success=false",
                    traceId,
                    System.currentTimeMillis() - startTime,
                    e);
            return Result.error("AI \u5b8c\u5584\u9884\u7ea6\u4fe1\u606f\u5931\u8d25\uff0c\u8bf7\u7a0d\u540e\u91cd\u8bd5");
        }
    }

    @Log("appointment_add")
    @Auth(value = "", permissions = {"btn.appointment.add"})
    @PostMapping
    public Result<?> add(@RequestBody ServiceAppointment appointment, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        appointment.setUserId(userId);
        appointment.setStatus(0);
        appointmentService.save(appointment);
        return Result.success();
    }

    @Log("appointment_update")
    @Auth(value = "", permissions = {"btn.appointment.edit"})
    @PutMapping("/{id}")
    public Result<?> update(@PathVariable Long id, @RequestBody ServiceAppointment appointment, HttpServletRequest request) {
        ServiceAppointment existing = appointmentService.getById(id);
        if (existing == null) {
            return Result.error("\u9884\u7ea6\u4e0d\u5b58\u5728");
        }
        Long userId = (Long) request.getAttribute("userId");
        String role = (String) request.getAttribute("role");
        boolean canViewAll = sysPermissionService.hasPermission(role, "scope.appointment.all", "ADMIN".equals(role));
        if (!canViewAll && !existing.getUserId().equals(userId)) {
            return Result.error("\u65e0\u6743\u9650\u7f16\u8f91\u8be5\u9884\u7ea6");
        }
        appointment.setId(id);
        appointment.setUserId(existing.getUserId());
        appointmentService.updateById(appointment);
        return Result.success();
    }

    @Log("appointment_status")
    @Auth(value = "", permissions = {"btn.appointment.status.confirm", "btn.appointment.status.complete", "btn.appointment.status.cancel"}, requireAllPermissions = false)
    @PutMapping("/{id}/status")
    public Result<?> updateStatus(@PathVariable Long id, @RequestParam Integer status, HttpServletRequest request) {
        ServiceAppointment appointment = appointmentService.getById(id);
        if (appointment == null) {
            return Result.error("\u9884\u7ea6\u4e0d\u5b58\u5728");
        }
        Long userId = (Long) request.getAttribute("userId");
        String role = (String) request.getAttribute("role");
        boolean canViewAll = sysPermissionService.hasPermission(role, "scope.appointment.all", "ADMIN".equals(role));
        if (!canViewAll && !appointment.getUserId().equals(userId)) {
            return Result.error("\u65e0\u6743\u9650\u66f4\u65b0\u8be5\u9884\u7ea6\u72b6\u6001");
        }
        appointment.setStatus(status);
        appointmentService.updateById(appointment);
        return Result.success();
    }

    @Log("appointment_delete")
    @Auth(value = "", permissions = {"btn.appointment.delete"})
    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id, HttpServletRequest request) {
        ServiceAppointment appointment = appointmentService.getById(id);
        if (appointment == null) {
            return Result.error("\u9884\u7ea6\u4e0d\u5b58\u5728");
        }
        Long userId = (Long) request.getAttribute("userId");
        String role = (String) request.getAttribute("role");
        boolean canViewAll = sysPermissionService.hasPermission(role, "scope.appointment.all", "ADMIN".equals(role));
        if (!canViewAll && !appointment.getUserId().equals(userId)) {
            return Result.error("\u65e0\u6743\u9650\u5220\u9664\u8be5\u9884\u7ea6");
        }
        appointmentService.removeById(appointment.getId());
        return Result.success();
    }
}
