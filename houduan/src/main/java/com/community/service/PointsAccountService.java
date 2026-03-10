package com.community.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.community.entity.PointsAccount;
import com.community.entity.PointsRecord;
import com.community.mapper.PointsAccountMapper;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PointsAccountService extends ServiceImpl<PointsAccountMapper, PointsAccount> {

    private final PointsRecordService pointsRecordService;

    public PointsAccountService(PointsRecordService pointsRecordService) {
        this.pointsRecordService = pointsRecordService;
    }

    public PointsAccount getOrCreateByUserId(Long userId) {
        PointsAccount account = getOne(new LambdaQueryWrapper<PointsAccount>()
                .eq(PointsAccount::getUserId, userId));
        if (account != null) {
            return account;
        }

        PointsAccount toSave = new PointsAccount();
        toSave.setUserId(userId);
        toSave.setTotalPoints(0);
        try {
            save(toSave);
            return toSave;
        } catch (DuplicateKeyException e) {
            return getOne(new LambdaQueryWrapper<PointsAccount>()
                    .eq(PointsAccount::getUserId, userId));
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean addPoints(Long userId, Integer points, Integer changeType,
                             String businessType, Long businessId, String remark, Long operatorId) {
        if (userId == null || points == null || points <= 0 || changeType == null) {
            throw new IllegalArgumentException("积分参数不合法");
        }

        getOrCreateByUserId(userId);

        PointsRecord record = new PointsRecord();
        record.setUserId(userId);
        record.setChangeType(changeType);
        record.setPoints(points);
        record.setBusinessType(businessType);
        record.setBusinessId(businessId);
        record.setRemark(remark);
        record.setOperatorId(operatorId);

        try {
            pointsRecordService.save(record);
        } catch (DuplicateKeyException e) {
            return false;
        }

        int delta = points;
        if (changeType < 0) {
            delta = -points;
        }
        baseMapper.changePoints(userId, delta);
        return true;
    }
}
