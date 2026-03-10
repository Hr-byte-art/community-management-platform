package com.community.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.community.entity.SecurityHazard;
import com.community.mapper.SecurityHazardMapper;
import org.springframework.stereotype.Service;
import cn.hutool.core.util.StrUtil;

@Service
public class SecurityHazardService extends ServiceImpl<SecurityHazardMapper, SecurityHazard> {
    
    public Page<SecurityHazard> pageQuery(Integer pageNum, Integer pageSize, String title, String hazardType, Integer status) {
        LambdaQueryWrapper<SecurityHazard> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StrUtil.isNotBlank(title), SecurityHazard::getTitle, title);
        wrapper.eq(StrUtil.isNotBlank(hazardType), SecurityHazard::getHazardType, hazardType);
        wrapper.eq(status != null, SecurityHazard::getStatus, status);
        wrapper.orderByDesc(SecurityHazard::getCreateTime);
        return page(new Page<>(pageNum, pageSize), wrapper);
    }

    public long countByStatus(Integer status) {
        return count(new LambdaQueryWrapper<SecurityHazard>().eq(SecurityHazard::getStatus, status));
    }
}
