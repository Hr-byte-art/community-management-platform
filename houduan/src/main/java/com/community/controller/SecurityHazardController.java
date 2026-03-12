package com.community.controller;

import cn.hutool.core.util.StrUtil;
import com.community.annotation.Auth;
import com.community.annotation.Log;
import com.community.ai.dto.SecurityHazardEnhanceRequest;
import com.community.ai.service.AIService;
import com.community.common.Result;
import com.community.entity.SecurityHazard;
import com.community.service.SecurityHazardService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/hazard")
public class SecurityHazardController {
    @Autowired
    private SecurityHazardService hazardService;
    @Autowired
    private AIService aiService;

    @GetMapping("/list")
    public Result<?> list(@RequestParam(defaultValue = "1") Integer pageNum,
                          @RequestParam(defaultValue = "10") Integer pageSize,
                          String title, String hazardType, Integer status) {
        return Result.success(hazardService.pageQuery(pageNum, pageSize, title, hazardType, status));
    }

    @GetMapping("/{id}")
    public Result<?> getById(@PathVariable Long id) {
        return Result.success(hazardService.getById(id));
    }

    @Auth(value = "", permissions = {"btn.hazard.add", "btn.hazard.edit"}, requireAllPermissions = false)
    @PostMapping("/ai/complete")
    public Result<?> enhance(@RequestBody SecurityHazardEnhanceRequest request) {
        if (request == null || StrUtil.isAllBlank(request.getTitle(), request.getContent())) {
            return Result.error(400, "请先输入隐患标题或隐患描述");
        }
        try {
            return Result.success(aiService.enhanceSecurityHazard(request));
        } catch (Exception e) {
            return Result.error("AI 完善隐患信息失败，请稍后重试");
        }
    }

    @Log("上报安全隐患")
    @Auth(value = "", permissions = {"btn.hazard.add"})
    @PostMapping
    public Result<?> add(@RequestBody SecurityHazard hazard, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        hazard.setReporterId(userId);
        hazard.setStatus(0);
        hazardService.save(hazard);
        return Result.success();
    }

    @Log("编辑隐患信息")
    @Auth(value = "", permissions = {"btn.hazard.edit"})
    @PutMapping("/{id}")
    public Result<?> update(@PathVariable Long id, @RequestBody SecurityHazard hazard) {
        hazard.setId(id);
        hazardService.updateById(hazard);
        return Result.success();
    }

    @Log("处理隐患")
    @Auth(permissions = {"btn.hazard.handle"})
    @PutMapping("/{id}/handle")
    public Result<?> handle(@PathVariable Long id, @RequestBody SecurityHazard hazard, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        SecurityHazard h = hazardService.getById(id);
        h.setHandlerId(userId);
        h.setStatus(hazard.getStatus());
        h.setHandleResult(hazard.getHandleResult());
        h.setHandleTime(LocalDateTime.now());
        hazardService.updateById(h);
        return Result.success();
    }

    @Log("删除隐患")
    @Auth(value = "", permissions = {"btn.hazard.delete"})
    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        hazardService.removeById(id);
        return Result.success();
    }
}
