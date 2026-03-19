package com.community.controller;

import com.community.common.Result;
import com.community.service.NoticeSubscriptionService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/notice/subscription")
public class NoticeSubscriptionController {

    private final NoticeSubscriptionService noticeSubscriptionService;

    public NoticeSubscriptionController(NoticeSubscriptionService noticeSubscriptionService) {
        this.noticeSubscriptionService = noticeSubscriptionService;
    }

    @GetMapping("/my")
    public Result<?> mySubscriptions(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(noticeSubscriptionService.getUserSubscriptions(userId));
    }

    @PutMapping("/my")
    public Result<?> saveSubscriptions(@RequestBody Map<String, Boolean> preferences,
                                       HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        noticeSubscriptionService.saveUserSubscriptions(userId, preferences);
        return Result.success();
    }
}
