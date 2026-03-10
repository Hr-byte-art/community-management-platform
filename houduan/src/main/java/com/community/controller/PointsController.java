package com.community.controller;

import com.community.common.Result;
import com.community.entity.PointsAccount;
import com.community.service.PointsAccountService;
import com.community.service.PointsRecordService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/points")
public class PointsController {

    private final PointsAccountService pointsAccountService;
    private final PointsRecordService pointsRecordService;

    public PointsController(PointsAccountService pointsAccountService, PointsRecordService pointsRecordService) {
        this.pointsAccountService = pointsAccountService;
        this.pointsRecordService = pointsRecordService;
    }

    @GetMapping("/my")
    public Result<?> myAccount(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        PointsAccount account = pointsAccountService.getOrCreateByUserId(userId);
        return Result.success(account);
    }

    @GetMapping("/records")
    public Result<?> myRecords(@RequestParam(defaultValue = "1") Integer pageNum,
                               @RequestParam(defaultValue = "10") Integer pageSize,
                               @RequestParam(required = false) Integer changeType,
                               @RequestParam(required = false) String businessType,
                               HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(pointsRecordService.pageByUserId(userId, pageNum, pageSize, changeType, businessType));
    }

    @GetMapping("/rank")
    public Result<?> rank(@RequestParam(defaultValue = "10") Integer limit) {
        int safeLimit = Math.max(1, Math.min(100, limit));
        return Result.success(pointsRecordService.rankList(safeLimit));
    }
}

