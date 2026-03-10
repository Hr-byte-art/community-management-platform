package com.community.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.community.common.Constants;
import com.community.entity.ActivityRegistration;
import com.community.mapper.ActivityRegistrationMapper;
import org.springframework.stereotype.Service;

@Service
public class ActivityRegistrationService extends ServiceImpl<ActivityRegistrationMapper, ActivityRegistration> {

    /**
     * 分页查询活动报名记录
     */
    public Page<ActivityRegistration> pageByActivityId(Long activityId, Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<ActivityRegistration> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ActivityRegistration::getActivityId, activityId);
        wrapper.orderByDesc(ActivityRegistration::getRegisterTime);
        return page(new Page<>(pageNum, pageSize), wrapper);
    }

    /**
     * 查询用户报名的活动
     */
    public Page<ActivityRegistration> pageByUserId(Long userId, Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<ActivityRegistration> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ActivityRegistration::getUserId, userId);
        wrapper.orderByDesc(ActivityRegistration::getRegisterTime);
        return page(new Page<>(pageNum, pageSize), wrapper);
    }

    /**
     * 检查用户是否已报名某个活动
     */
    public boolean isUserRegistered(Long activityId, Long userId) {
        LambdaQueryWrapper<ActivityRegistration> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ActivityRegistration::getActivityId, activityId);
        wrapper.eq(ActivityRegistration::getUserId, userId);
        wrapper.ne(ActivityRegistration::getStatus, Constants.RegistrationStatus.CANCELLED); // 排除已取消的报名
        return count(wrapper) > 0;
    }
}