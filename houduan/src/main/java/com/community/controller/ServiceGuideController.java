package com.community.controller;

import com.community.annotation.Log;
import com.community.common.Result;
import com.community.entity.ServiceGuide;
import com.community.service.ServiceGuideService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/guide")
public class ServiceGuideController {
    @Autowired
    private ServiceGuideService guideService;

    @GetMapping("/list")
    public Result<?> list(@RequestParam(defaultValue = "1") Integer pageNum,
                          @RequestParam(defaultValue = "10") Integer pageSize,
                          String title, String category) {
        return Result.success(guideService.pageQuery(pageNum, pageSize, title, category));
    }

    @GetMapping("/{id}")
    public Result<?> getById(@PathVariable Long id) {
        ServiceGuide guide = guideService.getById(id);
        guide.setViewCount(guide.getViewCount() + 1);
        guideService.updateById(guide);
        return Result.success(guide);
    }

    @Log("发布办事指南")
    @PostMapping
    public Result<?> add(@RequestBody ServiceGuide guide) {
        guide.setViewCount(0);
        guide.setStatus(1);
        guideService.save(guide);
        return Result.success();
    }

    @Log("编辑办事指南")
    @PutMapping("/{id}")
    public Result<?> update(@PathVariable Long id, @RequestBody ServiceGuide guide) {
        guide.setId(id);
        guideService.updateById(guide);
        return Result.success();
    }

    @Log("删除办事指南")
    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        guideService.removeById(id);
        return Result.success();
    }
}
