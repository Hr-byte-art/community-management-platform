package com.community.service;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.community.common.Constants;
import com.community.entity.GridDispatchRule;
import com.community.entity.SysUser;
import com.community.mapper.GridDispatchRuleMapper;
import org.springframework.stereotype.Service;

@Service
public class GridDispatchRuleService extends ServiceImpl<GridDispatchRuleMapper, GridDispatchRule> {

    private final SysUserService sysUserService;

    public GridDispatchRuleService(SysUserService sysUserService) {
        this.sysUserService = sysUserService;
    }

    public Page<GridDispatchRule> pageQuery(Integer pageNum, Integer pageSize, String gridName, String orderType, Integer enabled) {
        LambdaQueryWrapper<GridDispatchRule> wrapper = new LambdaQueryWrapper<GridDispatchRule>()
                .like(StrUtil.isNotBlank(gridName), GridDispatchRule::getGridName, gridName)
                .eq(StrUtil.isNotBlank(orderType), GridDispatchRule::getOrderType, orderType)
                .eq(enabled != null, GridDispatchRule::getEnabled, enabled)
                .orderByDesc(GridDispatchRule::getUpdateTime)
                .orderByDesc(GridDispatchRule::getId);
        return page(new Page<>(pageNum, pageSize), wrapper);
    }

    public Long matchAssigneeId(String orderType, Integer priority) {
        GridDispatchRule rule = findRule(orderType, priority);
        if (rule == null || rule.getAssigneeId() == null) {
            return null;
        }

        SysUser user = sysUserService.getById(rule.getAssigneeId());
        if (user == null || !Constants.UserStatus.ENABLED.equals(user.getStatus())) {
            return null;
        }

        return user.getId();
    }

    private GridDispatchRule findRule(String orderType, Integer priority) {
        GridDispatchRule first = getOne(new LambdaQueryWrapper<GridDispatchRule>()
                .eq(GridDispatchRule::getEnabled, 1)
                .eq(StrUtil.isNotBlank(orderType), GridDispatchRule::getOrderType, orderType)
                .eq(priority != null, GridDispatchRule::getPriority, priority)
                .orderByDesc(GridDispatchRule::getId)
                .last("limit 1"), false);
        if (first != null) {
            return first;
        }

        GridDispatchRule second = getOne(new LambdaQueryWrapper<GridDispatchRule>()
                .eq(GridDispatchRule::getEnabled, 1)
                .eq(StrUtil.isNotBlank(orderType), GridDispatchRule::getOrderType, orderType)
                .isNull(GridDispatchRule::getPriority)
                .orderByDesc(GridDispatchRule::getId)
                .last("limit 1"), false);
        if (second != null) {
            return second;
        }

        GridDispatchRule third = getOne(new LambdaQueryWrapper<GridDispatchRule>()
                .eq(GridDispatchRule::getEnabled, 1)
                .isNull(GridDispatchRule::getOrderType)
                .eq(priority != null, GridDispatchRule::getPriority, priority)
                .orderByDesc(GridDispatchRule::getId)
                .last("limit 1"), false);
        if (third != null) {
            return third;
        }

        return getOne(new LambdaQueryWrapper<GridDispatchRule>()
                .eq(GridDispatchRule::getEnabled, 1)
                .isNull(GridDispatchRule::getOrderType)
                .isNull(GridDispatchRule::getPriority)
                .orderByDesc(GridDispatchRule::getId)
                .last("limit 1"), false);
    }
}
