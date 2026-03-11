package com.community.controller;

import com.community.annotation.Auth;
import com.community.annotation.Log;
import com.community.common.Result;
import com.community.entity.CommunityActivity;
import com.community.service.CommunityActivityService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/activity")
public class ActivityController {
    @Autowired
    private CommunityActivityService activityService;

    @GetMapping("/list")
    public Result<?> list(@RequestParam(defaultValue = "1") Integer pageNum,
                          @RequestParam(defaultValue = "10") Integer pageSize,
                          String title, String activityType, Integer isCancelled) {
        return Result.success(activityService.pageQuery(pageNum, pageSize, title, activityType, isCancelled));
    }

    @GetMapping("/{id}")
    public Result<?> getById(@PathVariable Long id) {
        return Result.success(activityService.getById(id));
    }

    @Log("发布活动")
    @Auth(permissions = {"btn.activity.add"})
    @PostMapping
    public Result<?> add(@RequestBody CommunityActivity activity, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        activity.setOrganizerId(userId);
        if (activity.getIsCancelled() == null) {
            activity.setIsCancelled(0);
        }
        activityService.save(activity);
        return Result.success();
    }

    @Log("编辑活动")
    @Auth(permissions = {"btn.activity.edit"})
    @PutMapping("/{id}")
    public Result<?> update(@PathVariable Long id, @RequestBody CommunityActivity activity) {
        activity.setId(id);
        activityService.updateById(activity);
        return Result.success();
    }

    @Log("删除活动")
    @Auth(permissions = {"btn.activity.delete"})
    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        activityService.removeById(id);
        return Result.success();
    }
}
