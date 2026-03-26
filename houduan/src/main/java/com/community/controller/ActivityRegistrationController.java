package com.community.controller;

import com.community.annotation.Auth;
import com.community.annotation.Log;
import com.community.common.Constants;
import com.community.common.Result;
import com.community.dto.ActivityRegistrationDTO;
import com.community.entity.ActivityRegistration;
import com.community.service.ActivityRegistrationService;
import com.community.service.CommunityActivityService;
import com.community.service.MessageNoticeService;
import com.community.service.PointsAccountService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/registration")
public class ActivityRegistrationController {

    @Autowired
    private ActivityRegistrationService registrationService;
    @Autowired
    private CommunityActivityService activityService;
    @Autowired
    private PointsAccountService pointsAccountService;
    @Autowired
    private MessageNoticeService messageNoticeService;

    @Log("报名活动")
    @PostMapping
    @Transactional
    public Result<String> registerActivity(@RequestBody ActivityRegistrationDTO dto, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");

        if (activityService.getById(dto.getActivityId()) == null) {
            return Result.error("活动不存在");
        }

        if (registrationService.isUserRegistered(dto.getActivityId(), userId)) {
            return Result.error("您已报名该活动，请勿重复报名");
        }

        ActivityRegistration registration = new ActivityRegistration();
        registration.setActivityId(dto.getActivityId());
        registration.setUserId(userId);
        registration.setStatus(Constants.RegistrationStatus.REGISTERED);
        registration.setRegisterTime(LocalDateTime.now());

        registrationService.save(registration);
        messageNoticeService.sendMessage(
                userId,
                "活动报名成功",
                "您已成功报名活动，活动ID：" + dto.getActivityId(),
                Constants.MessageType.ACTIVITY,
                Constants.MessageBusinessType.ACTIVITY_REGISTRATION,
                registration.getId()
        );
        return Result.success("报名成功");
    }

    @Log("取消报名")
    @DeleteMapping("/{id}")
    @Transactional
    public Result<String> cancelRegistration(@PathVariable Long id, HttpServletRequest request) {
        ActivityRegistration registration = registrationService.getById(id);
        if (registration == null) {
            return Result.error("报名记录不存在");
        }

        Long userId = (Long) request.getAttribute("userId");
        if (!registration.getUserId().equals(userId)) {
            return Result.error("无权限取消他人的报名记录");
        }

        registration.setStatus(Constants.RegistrationStatus.CANCELLED);
        registrationService.updateById(registration);
        messageNoticeService.sendMessage(
                userId,
                "活动报名已取消",
                "您已取消报名，报名记录ID：" + registration.getId(),
                Constants.MessageType.ACTIVITY,
                Constants.MessageBusinessType.ACTIVITY_REGISTRATION,
                registration.getId()
        );
        return Result.success("取消报名成功");
    }

    @Log("活动签到")
    @PutMapping("/checkin/{id}")
    @Transactional
    public Result<String> checkIn(@PathVariable Long id, HttpServletRequest request) {
        ActivityRegistration registration = registrationService.getById(id);
        if (registration == null) {
            return Result.error("报名记录不存在");
        }

        Long userId = (Long) request.getAttribute("userId");
        if (!registration.getUserId().equals(userId)) {
            return Result.error("无权限为他人签到");
        }

        if (!Constants.RegistrationStatus.REGISTERED.equals(registration.getStatus())) {
            return Result.error("当前状态不允许签到");
        }

        registration.setStatus(Constants.RegistrationStatus.CHECKED_IN);
        registration.setCheckInTime(LocalDateTime.now());
        registrationService.updateById(registration);

        boolean granted = pointsAccountService.addPoints(
                userId,
                Constants.PointsRule.ACTIVITY_CHECKIN_POINTS,
                Constants.PointsChangeType.INCOME,
                Constants.PointsBusinessType.ACTIVITY_CHECKIN,
                registration.getId(),
                "活动签到奖励积分",
                userId
        );

        if (granted) {
            return Result.success("签到成功，积分+" + Constants.PointsRule.ACTIVITY_CHECKIN_POINTS);
        }
        return Result.success("签到成功");
    }

    @GetMapping("/my")
    public Result<?> getMyRegistrations(@RequestParam(defaultValue = "1") Integer pageNum,
                                        @RequestParam(defaultValue = "10") Integer pageSize,
                                        HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(registrationService.pageByUserId(userId, pageNum, pageSize));
    }

    @Auth(value = "ADMIN")
    @GetMapping("/activity/{activityId}")
    public Result<?> getActivityRegistrations(@PathVariable Long activityId,
                                              @RequestParam(defaultValue = "1") Integer pageNum,
                                              @RequestParam(defaultValue = "10") Integer pageSize) {
        if (activityService.getById(activityId) == null) {
            return Result.error("活动不存在");
        }

        return Result.success(registrationService.pageByActivityId(activityId, pageNum, pageSize));
    }
}
