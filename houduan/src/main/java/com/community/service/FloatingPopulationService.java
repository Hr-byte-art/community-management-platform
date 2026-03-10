package com.community.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.community.entity.FloatingPopulation;
import com.community.mapper.FloatingPopulationMapper;
import org.springframework.stereotype.Service;
import cn.hutool.core.util.StrUtil;

@Service
public class FloatingPopulationService extends ServiceImpl<FloatingPopulationMapper, FloatingPopulation> {
    
    public Page<FloatingPopulation> pageQuery(Integer pageNum, Integer pageSize, String name, Integer status) {
        LambdaQueryWrapper<FloatingPopulation> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StrUtil.isNotBlank(name), FloatingPopulation::getName, name);
        wrapper.eq(status != null, FloatingPopulation::getStatus, status);
        wrapper.orderByDesc(FloatingPopulation::getCreateTime);
        return page(new Page<>(pageNum, pageSize), wrapper);
    }

    public long countActive() {
        return count(new LambdaQueryWrapper<FloatingPopulation>().eq(FloatingPopulation::getStatus, 1));
    }
}
