package com.community.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.community.entity.FamilyRelation;
import com.community.mapper.FamilyRelationMapper;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class FamilyRelationService extends ServiceImpl<FamilyRelationMapper, FamilyRelation> {
    
    public List<FamilyRelation> findByResidentId(Long residentId) {
        return list(new LambdaQueryWrapper<FamilyRelation>().eq(FamilyRelation::getResidentId, residentId));
    }
}
