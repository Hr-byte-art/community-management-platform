package com.community.controller;

import com.community.annotation.Auth;
import com.community.annotation.Log;
import com.community.common.Constants;
import com.community.common.Result;
import com.community.entity.Volunteer;
import com.community.service.MessageNoticeService;
import com.community.service.SysPermissionService;
import com.community.service.VolunteerService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/volunteer")
public class VolunteerController {
    @Autowired
    private VolunteerService volunteerService;
    @Autowired
    private MessageNoticeService messageNoticeService;
    @Autowired
    private SysPermissionService sysPermissionService;

    @GetMapping("/list")
    public Result<?> list(@RequestParam(defaultValue = "1") Integer pageNum,
                          @RequestParam(defaultValue = "10") Integer pageSize,
                          String name, Integer status, HttpServletRequest request) {
        String role = (String) request.getAttribute("role");
        boolean canViewAll = sysPermissionService.hasPermission(role, "scope.volunteer.all", Constants.Role.ADMIN.equals(role));

        if (canViewAll) {
            return Result.success(volunteerService.pageQuery(pageNum, pageSize, name, status));
        } else {
            return Result.success(volunteerService.pageQuery(pageNum, pageSize, name, Constants.VolunteerStatus.APPROVED));
        }
    }

    @GetMapping("/{id}")
    public Result<?> getById(@PathVariable Long id, HttpServletRequest request) {
        String role = (String) request.getAttribute("role");
        Long userId = (Long) request.getAttribute("userId");
        boolean canViewAll = sysPermissionService.hasPermission(role, "scope.volunteer.all", Constants.Role.ADMIN.equals(role));

        Volunteer volunteer = volunteerService.getById(id);
        if (volunteer == null) {
            return Result.error("志愿者记录不存在");
        }

        if (canViewAll
                || volunteer.getUserId().equals(userId)
                || Constants.VolunteerStatus.APPROVED.equals(volunteer.getStatus())) {
            return Result.success(volunteer);
        } else {
            return Result.error("无权限查看该记录");
        }
    }

    @Log("申请成为志愿者")
    @Auth(value = "", permissions = {"btn.volunteer.apply"})
    @PostMapping("/apply")
    @Transactional
    public Result<?> apply(@RequestBody Volunteer volunteer, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");

        Volunteer existingApplication = volunteerService.getByUserId(userId);
        if (existingApplication != null) {
            return Result.error("您已提交过志愿者申请，请勿重复提交");
        }

        volunteer.setUserId(userId);
        volunteer.setStatus(Constants.VolunteerStatus.PENDING);
        volunteerService.save(volunteer);
        return Result.success();
    }

    @Log("审核志愿者申请")
    @Auth(permissions = {"btn.volunteer.audit"})
    @PutMapping("/{id}/audit")
    @Transactional
    public Result<?> audit(@PathVariable Long id, @RequestParam Integer status, HttpServletRequest request) {
        String role = (String) request.getAttribute("role");

        if (!Constants.Role.ADMIN.equals(role)) {
            return Result.error("无权限审核志愿者申请");
        }

        if (!Constants.VolunteerStatus.PENDING.equals(status)
                && !Constants.VolunteerStatus.APPROVED.equals(status)
                && !Constants.VolunteerStatus.REJECTED.equals(status)) {
            return Result.error("审核状态无效");
        }

        Volunteer volunteer = volunteerService.getById(id);
        if (volunteer == null) {
            return Result.error("志愿者记录不存在");
        }

        volunteer.setStatus(status);
        if (Constants.VolunteerStatus.APPROVED.equals(status)) {
            volunteer.setJoinDate(LocalDate.now());
        }
        volunteerService.updateById(volunteer);

        String statusText = "待审核";
        if (Constants.VolunteerStatus.APPROVED.equals(status)) {
            statusText = "已通过";
        } else if (Constants.VolunteerStatus.REJECTED.equals(status)) {
            statusText = "已拒绝";
        }

        messageNoticeService.sendMessage(
                volunteer.getUserId(),
                "志愿者申请审核结果",
                "您的志愿者申请当前状态：" + statusText,
                Constants.MessageType.VOLUNTEER,
                Constants.MessageBusinessType.VOLUNTEER,
                volunteer.getId()
        );

        return Result.success();
    }

    @Log("编辑志愿者信息")
    @Auth(value = "", permissions = {"btn.volunteer.edit"})
    @PutMapping("/{id}")
    @Transactional
    public Result<?> update(@PathVariable Long id, @RequestBody Volunteer volunteer, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        String role = (String) request.getAttribute("role");

        Volunteer existingVolunteer = volunteerService.getById(id);
        if (existingVolunteer == null) {
            return Result.error("志愿者记录不存在");
        }

        if (!existingVolunteer.getUserId().equals(userId) && !Constants.Role.ADMIN.equals(role)) {
            return Result.error("无权限修改该记录");
        }

        if (!Constants.Role.ADMIN.equals(role) && Constants.VolunteerStatus.APPROVED.equals(existingVolunteer.getStatus())) {
            volunteer.setStatus(existingVolunteer.getStatus());
        }

        volunteer.setId(id);
        volunteerService.updateById(volunteer);
        return Result.success();
    }

    @Log("删除志愿者申请")
    @Auth(value = "", permissions = {"btn.volunteer.delete"})
    @DeleteMapping("/{id}")
    @Transactional
    public Result<?> delete(@PathVariable Long id, HttpServletRequest request) {
        String role = (String) request.getAttribute("role");
        Long userId = (Long) request.getAttribute("userId");

        Volunteer volunteer = volunteerService.getById(id);
        if (volunteer == null) {
            return Result.error("志愿者记录不存在");
        }

        if (!Constants.Role.ADMIN.equals(role) && !volunteer.getUserId().equals(userId)) {
            return Result.error("无权限删除该记录");
        }

        if (!Constants.VolunteerStatus.PENDING.equals(volunteer.getStatus()) && !Constants.Role.ADMIN.equals(role)) {
            return Result.error("已审核申请不可删除");
        }

        volunteerService.removeById(id);
        return Result.success();
    }
}
