package com.community.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.community.entity.NeighborHelp;
import com.community.mapper.NeighborHelpMapper;
import org.springframework.stereotype.Service;
import cn.hutool.core.util.StrUtil;

@Service
public class NeighborHelpService extends ServiceImpl<NeighborHelpMapper, NeighborHelp> {
    
    public Page<NeighborHelp> pageQuery(Integer pageNum, Integer pageSize, String title, String helpType, String category) {
        LambdaQueryWrapper<NeighborHelp> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StrUtil.isNotBlank(title), NeighborHelp::getTitle, title);
        wrapper.eq(StrUtil.isNotBlank(helpType), NeighborHelp::getHelpType, helpType);
        wrapper.eq(StrUtil.isNotBlank(category), NeighborHelp::getCategory, category);
        wrapper.orderByDesc(NeighborHelp::getCreateTime);
        return page(new Page<>(pageNum, pageSize), wrapper);
    }
}
