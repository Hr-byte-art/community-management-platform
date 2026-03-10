package com.community.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.community.entity.OperationLog;
import com.community.mapper.OperationLogMapper;
import org.springframework.stereotype.Service;
import cn.hutool.core.util.StrUtil;

@Service
public class OperationLogService extends ServiceImpl<OperationLogMapper, OperationLog> {
    
    public Page<OperationLog> pageQuery(Integer pageNum, Integer pageSize, String username, String operation) {
        LambdaQueryWrapper<OperationLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StrUtil.isNotBlank(username), OperationLog::getUsername, username);
        wrapper.like(StrUtil.isNotBlank(operation), OperationLog::getOperation, operation);
        wrapper.orderByDesc(OperationLog::getCreateTime);
        return page(new Page<>(pageNum, pageSize), wrapper);
    }
}
