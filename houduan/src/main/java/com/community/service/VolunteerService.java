package com.community.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.community.entity.Volunteer;
import com.community.mapper.VolunteerMapper;
import org.springframework.stereotype.Service;
import cn.hutool.core.util.StrUtil;

@Service
public class VolunteerService extends ServiceImpl<VolunteerMapper, Volunteer> {
    
    public Page<Volunteer> pageQuery(Integer pageNum, Integer pageSize, String name, Integer status) {
        LambdaQueryWrapper<Volunteer> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StrUtil.isNotBlank(name), Volunteer::getName, name);
        wrapper.eq(status != null, Volunteer::getStatus, status);
        wrapper.orderByDesc(Volunteer::getCreateTime);
        return page(new Page<>(pageNum, pageSize), wrapper);
    }

    /**
     * 根据用户ID获取志愿者信息
     */
    public Volunteer getByUserId(Long userId) {
        return getOne(new LambdaQueryWrapper<Volunteer>().eq(Volunteer::getUserId, userId));
    }

    public long countApproved() {
        return count(new LambdaQueryWrapper<Volunteer>().eq(Volunteer::getStatus, 1));
    }
}
