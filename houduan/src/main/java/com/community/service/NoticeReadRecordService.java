package com.community.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.community.entity.NoticeReadRecord;
import com.community.mapper.NoticeReadRecordMapper;
import org.springframework.stereotype.Service;

@Service
public class NoticeReadRecordService extends ServiceImpl<NoticeReadRecordMapper, NoticeReadRecord> {
    
    public long countByNoticeId(Long noticeId) {
        return count(new LambdaQueryWrapper<NoticeReadRecord>().eq(NoticeReadRecord::getNoticeId, noticeId));
    }
    
    public boolean hasRead(Long noticeId, Long userId) {
        return count(new LambdaQueryWrapper<NoticeReadRecord>()
                .eq(NoticeReadRecord::getNoticeId, noticeId)
                .eq(NoticeReadRecord::getUserId, userId)) > 0;
    }
}
