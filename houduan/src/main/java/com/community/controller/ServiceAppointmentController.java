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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/appointment")
public class ServiceAppointmentController {
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
            return Result.error("预约不存在");
        }
        Long userId = (Long) request.getAttribute("userId");
        String role = (String) request.getAttribute("role");
        boolean canViewAll = sysPermissionService.hasPermission(role, "scope.appointment.all", "ADMIN".equals(role));
        if (!canViewAll && !appointment.getUserId().equals(userId)) {
            return Result.error("无权限查看该预约");
        }
        return Result.success(appointment);
    }

    @Auth(value = "", permissions = {"btn.appointment.add", "btn.appointment.edit"}, requireAllPermissions = false)
    @PostMapping("/ai/complete")
    public Result<?> enhance(@RequestBody ServiceAppointmentEnhanceRequest request) {
        if (request == null || StrUtil.isAllBlank(request.getTitle(), request.getContent())) {
            return Result.error(400, "请先输入预约标题或预约内容");
        }
        try {
            return Result.success(aiService.enhanceServiceAppointment(request));
        } catch (IllegalStateException e) {
            return Result.error(e.getMessage());
        } catch (Exception e) {
            return Result.error("AI 完善预约信息失败，请稍后重试");
        }
    }

    @Log("预约生活服务")
    @Auth(value = "", permissions = {"btn.appointment.add"})
    @PostMapping
    public Result<?> add(@RequestBody ServiceAppointment appointment, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        appointment.setUserId(userId);
        appointment.setStatus(0);
        appointmentService.save(appointment);
        return Result.success();
    }

    @Log("编辑预约")
    @Auth(value = "", permissions = {"btn.appointment.edit"})
    @PutMapping("/{id}")
    public Result<?> update(@PathVariable Long id, @RequestBody ServiceAppointment appointment, HttpServletRequest request) {
        ServiceAppointment existing = appointmentService.getById(id);
        if (existing == null) {
            return Result.error("预约不存在");
        }
        Long userId = (Long) request.getAttribute("userId");
        String role = (String) request.getAttribute("role");
        boolean canViewAll = sysPermissionService.hasPermission(role, "scope.appointment.all", "ADMIN".equals(role));
        if (!canViewAll && !existing.getUserId().equals(userId)) {
            return Result.error("无权限编辑该预约");
        }
        appointment.setId(id);
        appointment.setUserId(existing.getUserId());
        appointmentService.updateById(appointment);
        return Result.success();
    }

    @Log("更新预约状态")
    @Auth(value = "", permissions = {"btn.appointment.status.confirm", "btn.appointment.status.complete", "btn.appointment.status.cancel"}, requireAllPermissions = false)
    @PutMapping("/{id}/status")
    public Result<?> updateStatus(@PathVariable Long id, @RequestParam Integer status, HttpServletRequest request) {
        ServiceAppointment appointment = appointmentService.getById(id);
        if (appointment == null) {
            return Result.error("预约不存在");
        }
        Long userId = (Long) request.getAttribute("userId");
        String role = (String) request.getAttribute("role");
        boolean canViewAll = sysPermissionService.hasPermission(role, "scope.appointment.all", "ADMIN".equals(role));
        if (!canViewAll && !appointment.getUserId().equals(userId)) {
            return Result.error("无权限更新该预约状态");
        }
        appointment.setStatus(status);
        appointmentService.updateById(appointment);
        return Result.success();
    }

    @Log("删除预约")
    @Auth(value = "", permissions = {"btn.appointment.delete"})
    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id, HttpServletRequest request) {
        ServiceAppointment appointment = appointmentService.getById(id);
        if (appointment == null) {
            return Result.error("预约不存在");
        }
        Long userId = (Long) request.getAttribute("userId");
        String role = (String) request.getAttribute("role");
        boolean canViewAll = sysPermissionService.hasPermission(role, "scope.appointment.all", "ADMIN".equals(role));
        if (!canViewAll && !appointment.getUserId().equals(userId)) {
            return Result.error("无权限删除该预约");
        }
        appointmentService.removeById(id);
        return Result.success();
    }
}

