package com.community.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.community.entity.ServiceGuide;
import com.community.mapper.ServiceGuideMapper;
import org.springframework.stereotype.Service;
import cn.hutool.core.util.StrUtil;

@Service
public class ServiceGuideService extends ServiceImpl<ServiceGuideMapper, ServiceGuide> {
    
    public Page<ServiceGuide> pageQuery(Integer pageNum, Integer pageSize, String title, String category) {
        LambdaQueryWrapper<ServiceGuide> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StrUtil.isNotBlank(title), ServiceGuide::getTitle, title);
        wrapper.eq(StrUtil.isNotBlank(category), ServiceGuide::getCategory, category);
        wrapper.eq(ServiceGuide::getStatus, 1);
        wrapper.orderByDesc(ServiceGuide::getCreateTime);
        return page(new Page<>(pageNum, pageSize), wrapper);
    }
}
