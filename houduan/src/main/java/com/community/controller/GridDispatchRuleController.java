package com.community.controller;

import com.community.annotation.Auth;
import com.community.common.Result;
import com.community.entity.GridDispatchRule;
import com.community.service.GridDispatchRuleService;
import com.community.service.SysUserService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/dispatch/rule")
public class GridDispatchRuleController {

    private final GridDispatchRuleService gridDispatchRuleService;
    private final SysUserService sysUserService;

    public GridDispatchRuleController(GridDispatchRuleService gridDispatchRuleService, SysUserService sysUserService) {
        this.gridDispatchRuleService = gridDispatchRuleService;
        this.sysUserService = sysUserService;
    }

    @Auth
    @GetMapping("/list")
    public Result<?> list(@RequestParam(defaultValue = "1") Integer pageNum,
                          @RequestParam(defaultValue = "10") Integer pageSize,
                          @RequestParam(required = false) String gridName,
                          @RequestParam(required = false) String orderType,
                          @RequestParam(required = false) Integer enabled) {
        return Result.success(gridDispatchRuleService.pageQuery(pageNum, pageSize, gridName, orderType, enabled));
    }

    @Auth
    @PostMapping
    public Result<?> add(@RequestBody GridDispatchRule rule) {
        String error = validateRule(rule);
        if (error != null) {
            return Result.error(error);
        }
        if (rule.getEnabled() == null) {
            rule.setEnabled(1);
        }
        return gridDispatchRuleService.save(rule) ? Result.success() : Result.error("新增规则失败");
    }

    @Auth
    @PutMapping("/{id}")
    public Result<?> update(@PathVariable Long id, @RequestBody GridDispatchRule rule) {
        GridDispatchRule existing = gridDispatchRuleService.getById(id);
        if (existing == null) {
            return Result.error("规则不存在");
        }
        String error = validateRule(rule);
        if (error != null) {
            return Result.error(error);
        }

        rule.setId(id);
        return gridDispatchRuleService.updateById(rule) ? Result.success() : Result.error("更新规则失败");
    }

    @Auth
    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        return gridDispatchRuleService.removeById(id) ? Result.success() : Result.error("删除规则失败");
    }

    @Auth
    @GetMapping("/preview")
    public Result<?> preview(@RequestParam(required = false) String orderType,
                             @RequestParam(required = false) Integer priority) {
        return Result.success(gridDispatchRuleService.matchAssigneeId(orderType, priority));
    }

    private String validateRule(GridDispatchRule rule) {
        if (rule == null) {
            return "参数不能为空";
        }
        if (rule.getAssigneeId() == null) {
            return "责任人不能为空";
        }
        if (sysUserService.getById(rule.getAssigneeId()) == null) {
            return "责任人不存在";
        }
        if (rule.getEnabled() != null && rule.getEnabled() != 0 && rule.getEnabled() != 1) {
            return "启用状态只能是0或1";
        }
        return null;
    }
}
