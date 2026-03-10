package com.community.service;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.community.entity.PointsRecord;
import com.community.mapper.PointsRecordMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class PointsRecordService extends ServiceImpl<PointsRecordMapper, PointsRecord> {

    public Page<PointsRecord> pageByUserId(Long userId, Integer pageNum, Integer pageSize,
                                           Integer changeType, String businessType) {
        LambdaQueryWrapper<PointsRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PointsRecord::getUserId, userId);
        wrapper.eq(changeType != null, PointsRecord::getChangeType, changeType);
        wrapper.eq(StrUtil.isNotBlank(businessType), PointsRecord::getBusinessType, businessType);
        wrapper.orderByDesc(PointsRecord::getCreateTime);
        return page(new Page<>(pageNum, pageSize), wrapper);
    }

    public List<Map<String, Object>> rankList(Integer limit) {
        return baseMapper.rankList(limit);
    }
}

