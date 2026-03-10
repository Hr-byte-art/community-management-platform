package com.community.controller;

import com.community.annotation.Log;
import com.community.common.Result;
import com.community.entity.ServiceAppointment;
import com.community.service.ServiceAppointmentService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/appointment")
public class ServiceAppointmentController {
    @Autowired
    private ServiceAppointmentService appointmentService;

    @GetMapping("/list")
    public Result<?> list(@RequestParam(defaultValue = "1") Integer pageNum,
                          @RequestParam(defaultValue = "10") Integer pageSize,
                          String serviceType, Integer status) {
        return Result.success(appointmentService.pageQuery(pageNum, pageSize, serviceType, status, null));
    }

    @GetMapping("/my")
    public Result<?> myList(@RequestParam(defaultValue = "1") Integer pageNum,
                            @RequestParam(defaultValue = "10") Integer pageSize,
                            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(appointmentService.pageQuery(pageNum, pageSize, null, null, userId));
    }

    @GetMapping("/{id}")
    public Result<?> getById(@PathVariable Long id) {
        return Result.success(appointmentService.getById(id));
    }

    @Log("预约生活服务")
    @PostMapping
    public Result<?> add(@RequestBody ServiceAppointment appointment, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        appointment.setUserId(userId);
        appointment.setStatus(0);
        appointmentService.save(appointment);
        return Result.success();
    }

    @Log("编辑预约")
    @PutMapping("/{id}")
    public Result<?> update(@PathVariable Long id, @RequestBody ServiceAppointment appointment) {
        appointment.setId(id);
        appointmentService.updateById(appointment);
        return Result.success();
    }

    @Log("更新预约状态")
    @PutMapping("/{id}/status")
    public Result<?> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        ServiceAppointment appointment = appointmentService.getById(id);
        appointment.setStatus(status);
        appointmentService.updateById(appointment);
        return Result.success();
    }

    @Log("删除预约")
    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        appointmentService.removeById(id);
        return Result.success();
    }
}
