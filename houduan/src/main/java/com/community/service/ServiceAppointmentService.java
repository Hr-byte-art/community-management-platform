package com.community.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.community.entity.ServiceAppointment;
import com.community.mapper.ServiceAppointmentMapper;
import org.springframework.stereotype.Service;
import cn.hutool.core.util.StrUtil;

@Service
public class ServiceAppointmentService extends ServiceImpl<ServiceAppointmentMapper, ServiceAppointment> {
    
    public Page<ServiceAppointment> pageQuery(Integer pageNum, Integer pageSize, String serviceType, Integer status, Long userId) {
        LambdaQueryWrapper<ServiceAppointment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StrUtil.isNotBlank(serviceType), ServiceAppointment::getServiceType, serviceType);
        wrapper.eq(status != null, ServiceAppointment::getStatus, status);
        wrapper.eq(userId != null, ServiceAppointment::getUserId, userId);
        wrapper.orderByDesc(ServiceAppointment::getCreateTime);
        return page(new Page<>(pageNum, pageSize), wrapper);
    }
}
