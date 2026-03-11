package com.community.controller;

import com.community.annotation.Auth;
import com.community.annotation.Log;
import com.community.common.Result;
import com.community.entity.NeighborHelp;
import com.community.service.NeighborHelpService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/neighborhelp")
public class NeighborHelpController {
    @Autowired
    private NeighborHelpService neighborHelpService;

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
        // 预览不增加浏览次数
        NeighborHelp help = neighborHelpService.getById(id);
        return Result.success(help);
    }

    @Log("发布互助信息")
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

    @Log("编辑互助信息")
    @Auth(value = "", permissions = {"btn.neighborhelp.edit"})
    @PutMapping("/{id}")
    public Result<?> update(@PathVariable Long id, @RequestBody NeighborHelp help) {
        help.setId(id);
        neighborHelpService.updateById(help);
        return Result.success();
    }

    @Log("更新互助状态")
    @Auth(value = "", permissions = {"btn.neighborhelp.status.complete", "btn.neighborhelp.status.close"}, requireAllPermissions = false)
    @PutMapping("/{id}/status")
    public Result<?> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        NeighborHelp help = neighborHelpService.getById(id);
        help.setStatus(status);
        neighborHelpService.updateById(help);
        return Result.success();
    }

    @Log("删除互助信息")
    @Auth(value = "", permissions = {"btn.neighborhelp.delete"})
    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        neighborHelpService.removeById(id);
        return Result.success();
    }
}
