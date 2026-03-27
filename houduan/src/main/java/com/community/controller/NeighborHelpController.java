package com.community.controller;

import cn.hutool.core.util.StrUtil;
import com.community.annotation.Auth;
import com.community.annotation.Log;
import com.community.ai.dto.NeighborHelpEnhanceRequest;
import com.community.ai.service.AIService;
import com.community.common.Result;
import com.community.entity.NeighborHelp;
import com.community.service.NeighborHelpService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/neighborhelp")
public class NeighborHelpController {
    private static final Logger logger = LoggerFactory.getLogger(NeighborHelpController.class);
    private static final String AI_TRACE_ID_HEADER = "X-AI-Trace-Id";

    @Autowired
    private NeighborHelpService neighborHelpService;
    @Autowired
    private AIService aiService;

    @GetMapping("/list")
    public Result<?> list(@RequestParam(defaultValue = "1") Integer pageNum,
                          @RequestParam(defaultValue = "10") Integer pageSize,
                          String title, String helpType, String category) {
        return Result.success(neighborHelpService.pageQuery(pageNum, pageSize, title, helpType, category));
    }

    @GetMapping("/{id}")
    public Result<?> getById(@PathVariable Long id) {
        NeighborHelp help = neighborHelpService.getById(id);
        help.setViewCount(help.getViewCount() + 1);
        neighborHelpService.updateById(help);
        return Result.success(help);
    }

    @GetMapping("/preview/{id}")
    public Result<?> preview(@PathVariable Long id) {
        NeighborHelp help = neighborHelpService.getById(id);
        return Result.success(help);
    }

    @Auth(value = "", permissions = {"btn.neighborhelp.add", "btn.neighborhelp.edit"}, requireAllPermissions = false)
    @PostMapping("/ai/complete")
    public Result<?> enhance(@RequestBody NeighborHelpEnhanceRequest enhanceRequest, HttpServletRequest httpRequest) {
        String traceId = StrUtil.blankToDefault(httpRequest.getHeader(AI_TRACE_ID_HEADER), "unknown");
        long startTime = System.currentTimeMillis();
        if (enhanceRequest == null || StrUtil.isAllBlank(enhanceRequest.getTitle(), enhanceRequest.getContent())) {
            logger.warn("[ai-complete][backend] traceId={} stage=controller-exit scene=neighborhelp costMs={} success=false reason=empty_input",
                    traceId,
                    System.currentTimeMillis() - startTime);
            return Result.error(400, "\u8bf7\u5148\u8f93\u5165\u4e92\u52a9\u6807\u9898\u6216\u4e92\u52a9\u5185\u5bb9");
        }
        logger.info("[ai-complete][backend] traceId={} stage=controller-enter scene=neighborhelp uri={} titleChars={} contentChars={}",
                traceId,
                httpRequest.getRequestURI(),
                enhanceRequest.getTitle() == null ? 0 : enhanceRequest.getTitle().length(),
                enhanceRequest.getContent() == null ? 0 : enhanceRequest.getContent().length());
        try {
            Result<?> result = Result.success(aiService.enhanceNeighborHelp(enhanceRequest));
            logger.info("[ai-complete][backend] traceId={} stage=controller-exit scene=neighborhelp costMs={} success=true",
                    traceId,
                    System.currentTimeMillis() - startTime);
            return result;
        } catch (IllegalStateException e) {
            logger.warn("[ai-complete][backend] traceId={} stage=controller-exit scene=neighborhelp costMs={} success=false message={}",
                    traceId,
                    System.currentTimeMillis() - startTime,
                    e.getMessage());
            return Result.error(e.getMessage());
        } catch (Exception e) {
            logger.error("[ai-complete][backend] traceId={} stage=controller-exit scene=neighborhelp costMs={} success=false",
                    traceId,
                    System.currentTimeMillis() - startTime,
                    e);
            return Result.error("AI \u5b8c\u5584\u4e92\u52a9\u4fe1\u606f\u5931\u8d25\uff0c\u8bf7\u7a0d\u540e\u91cd\u8bd5");
        }
    }

    @Log("neighborhelp_add")
    @Auth(value = "", permissions = {"btn.neighborhelp.add"})
    @PostMapping
    public Result<?> add(@RequestBody NeighborHelp help, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        help.setUserId(userId);
        help.setViewCount(0);
        help.setStatus(1);
        neighborHelpService.save(help);
        return Result.success();
    }

    @Log("neighborhelp_update")
    @Auth(value = "", permissions = {"btn.neighborhelp.edit"})
    @PutMapping("/{id}")
    public Result<?> update(@PathVariable Long id, @RequestBody NeighborHelp help) {
        help.setId(id);
        neighborHelpService.updateById(help);
        return Result.success();
    }

    @Log("neighborhelp_status")
    @Auth(value = "", permissions = {"btn.neighborhelp.status.complete", "btn.neighborhelp.status.close"}, requireAllPermissions = false)
    @PutMapping("/{id}/status")
    public Result<?> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        NeighborHelp help = neighborHelpService.getById(id);
        help.setStatus(status);
        neighborHelpService.updateById(help);
        return Result.success();
    }

    @Log("neighborhelp_delete")
    @Auth(value = "", permissions = {"btn.neighborhelp.delete"})
    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        neighborHelpService.removeById(id);
        return Result.success();
    }
}
