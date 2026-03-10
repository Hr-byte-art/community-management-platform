package com.community.controller;

import com.community.annotation.Auth;
import com.community.common.Result;
import com.community.service.OperationLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/log")
public class OperationLogController {
    @Autowired
    private OperationLogService logService;

    @Auth
    @GetMapping("/list")
    public Result<?> list(@RequestParam(defaultValue = "1") Integer pageNum,
                          @RequestParam(defaultValue = "10") Integer pageSize,
                          String username, String operation) {
        return Result.success(logService.pageQuery(pageNum, pageSize, username, operation));
    }

    @Auth
    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        logService.removeById(id);
        return Result.success();
    }

    @Auth
    @DeleteMapping("/clear")
    public Result<?> clear() {
        logService.remove(null);
        return Result.success();
    }
}
