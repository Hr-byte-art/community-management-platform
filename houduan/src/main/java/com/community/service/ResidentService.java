package com.community.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.community.entity.Resident;
import com.community.mapper.ResidentMapper;
import org.springframework.stereotype.Service;
import cn.hutool.core.util.StrUtil;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ResidentService extends ServiceImpl<ResidentMapper, Resident> {
    
    public Page<Resident> pageQuery(Integer pageNum, Integer pageSize, String name, String buildingNo, Integer status) {
        LambdaQueryWrapper<Resident> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StrUtil.isNotBlank(name), Resident::getName, name);
        wrapper.eq(StrUtil.isNotBlank(buildingNo), Resident::getBuildingNo, buildingNo);
        wrapper.eq(status != null, Resident::getStatus, status);
        wrapper.orderByDesc(Resident::getCreateTime);
        return page(new Page<>(pageNum, pageSize), wrapper);
    }

    public long countByStatus(Integer status) {
        return count(new LambdaQueryWrapper<Resident>().eq(Resident::getStatus, status));
    }

    public List<Map<String, Object>> countByBuilding() {
        List<Resident> residents = list(new LambdaQueryWrapper<Resident>().eq(Resident::getStatus, 1));
        Map<String, Long> buildingCount = residents.stream()
            .filter(r -> StrUtil.isNotBlank(r.getBuildingNo()))
            .collect(Collectors.groupingBy(Resident::getBuildingNo, Collectors.counting()));
        return buildingCount.entrySet().stream()
            .map(e -> {
                Map<String, Object> item = new HashMap<>();
                item.put("building", e.getKey() + "号楼");
                item.put("count", e.getValue());
                return item;
            })
            .sorted((a, b) -> ((String)a.get("building")).compareTo((String)b.get("building")))
            .collect(Collectors.toList());
    }
    
    /**
     * 检查身份证号是否已存在
     */
    public boolean existsByIdCard(String idCard, Long excludeId) {
        LambdaQueryWrapper<Resident> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Resident::getIdCard, idCard);
        if (excludeId != null) {
            wrapper.ne(Resident::getId, excludeId);
        }
        return count(wrapper) > 0;
    }
    
    /**
     * 检查手机号是否已存在
     */
    public boolean existsByPhone(String phone, Long excludeId) {
        LambdaQueryWrapper<Resident> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Resident::getPhone, phone);
        if (excludeId != null) {
            wrapper.ne(Resident::getId, excludeId);
        }
        return count(wrapper) > 0;
    }
}
