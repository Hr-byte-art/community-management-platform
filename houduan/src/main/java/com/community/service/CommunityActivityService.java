package com.community.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.community.entity.CommunityActivity;
import com.community.mapper.CommunityActivityMapper;
import org.springframework.stereotype.Service;
import cn.hutool.core.util.StrUtil;
import java.util.*;

@Service
public class CommunityActivityService extends ServiceImpl<CommunityActivityMapper, CommunityActivity> {
    
    public Page<CommunityActivity> pageQuery(Integer pageNum, Integer pageSize, String title, String activityType, Integer isCancelled) {
        LambdaQueryWrapper<CommunityActivity> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StrUtil.isNotBlank(title), CommunityActivity::getTitle, title);
        wrapper.eq(StrUtil.isNotBlank(activityType), CommunityActivity::getActivityType, activityType);
        wrapper.eq(isCancelled != null, CommunityActivity::getIsCancelled, isCancelled);
        wrapper.orderByDesc(CommunityActivity::getCreateTime);
        return page(new Page<>(pageNum, pageSize), wrapper);
    }

    public long countByCancelled(Integer isCancelled) {
        return count(new LambdaQueryWrapper<CommunityActivity>().eq(CommunityActivity::getIsCancelled, isCancelled));
    }

    public List<Map<String, Object>> countByType() {
        List<Map<String, Object>> result = new ArrayList<>();
        String[] types = {"CULTURE", "SPORT", "VOLUNTEER", "OTHER"};
        String[] names = {"文化", "体育", "志愿", "其他"};
        for (int i = 0; i < types.length; i++) {
            Map<String, Object> item = new HashMap<>();
            item.put("type", types[i]);
            item.put("name", names[i]);
            item.put("count", count(new LambdaQueryWrapper<CommunityActivity>().eq(CommunityActivity::getActivityType, types[i])));
            result.add(item);
        }
        return result;
    }
}
