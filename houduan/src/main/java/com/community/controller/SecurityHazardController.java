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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/hazard")
public class SecurityHazardController {
    private static final Logger logger = LoggerFactory.getLogger(SecurityHazardController.class);
    private static final String AI_TRACE_ID_HEADER = "X-AI-Trace-Id";

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
    public Result<?> enhance(@RequestBody SecurityHazardEnhanceRequest enhanceRequest, HttpServletRequest httpRequest) {
        String traceId = StrUtil.blankToDefault(httpRequest.getHeader(AI_TRACE_ID_HEADER), "unknown");
        long startTime = System.currentTimeMillis();
        if (enhanceRequest == null || StrUtil.isAllBlank(enhanceRequest.getTitle(), enhanceRequest.getContent())) {
            logger.warn("[ai-complete][backend] traceId={} stage=controller-exit scene=hazard costMs={} success=false reason=empty_input",
                    traceId,
                    System.currentTimeMillis() - startTime);
            return Result.error(400, "\u8bf7\u5148\u8f93\u5165\u9690\u60a3\u6807\u9898\u6216\u9690\u60a3\u63cf\u8ff0");
        }
        logger.info("[ai-complete][backend] traceId={} stage=controller-enter scene=hazard uri={} titleChars={} contentChars={}",
                traceId,
                httpRequest.getRequestURI(),
                enhanceRequest.getTitle() == null ? 0 : enhanceRequest.getTitle().length(),
                enhanceRequest.getContent() == null ? 0 : enhanceRequest.getContent().length());
        try {
            Result<?> result = Result.success(aiService.enhanceSecurityHazard(enhanceRequest));
            logger.info("[ai-complete][backend] traceId={} stage=controller-exit scene=hazard costMs={} success=true",
                    traceId,
                    System.currentTimeMillis() - startTime);
            return result;
        } catch (IllegalStateException e) {
            logger.warn("[ai-complete][backend] traceId={} stage=controller-exit scene=hazard costMs={} success=false message={}",
                    traceId,
                    System.currentTimeMillis() - startTime,
                    e.getMessage());
            return Result.error(e.getMessage());
        } catch (Exception e) {
            logger.error("[ai-complete][backend] traceId={} stage=controller-exit scene=hazard costMs={} success=false",
                    traceId,
                    System.currentTimeMillis() - startTime,
                    e);
            return Result.error("AI \u5b8c\u5584\u9690\u60a3\u4fe1\u606f\u5931\u8d25\uff0c\u8bf7\u7a0d\u540e\u91cd\u8bd5");
        }
    }

    @Log("hazard_add")
    @Auth(value = "", permissions = {"btn.hazard.add"})
    @PostMapping
    public Result<?> add(@RequestBody SecurityHazard hazard, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        hazard.setReporterId(userId);
        hazard.setStatus(0);
        hazardService.save(hazard);
        return Result.success();
    }

    @Log("hazard_update")
    @Auth(value = "", permissions = {"btn.hazard.edit"})
    @PutMapping("/{id}")
    public Result<?> update(@PathVariable Long id, @RequestBody SecurityHazard hazard) {
        hazard.setId(id);
        hazardService.updateById(hazard);
        return Result.success();
    }

    @Log("hazard_handle")
    @Auth(permissions = {"btn.hazard.handle"})
    @PutMapping("/{id}/handle")
    public Result<?> handle(@PathVariable Long id, @RequestBody SecurityHazard hazard, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        SecurityHazard current = hazardService.getById(id);
        current.setHandlerId(userId);
        current.setStatus(hazard.getStatus());
        current.setHandleResult(hazard.getHandleResult());
        current.setHandleTime(LocalDateTime.now());
        hazardService.updateById(current);
        return Result.success();
    }

    @Log("hazard_delete")
    @Auth(value = "", permissions = {"btn.hazard.delete"})
    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        hazardService.removeById(id);
        return Result.success();
    }
}
