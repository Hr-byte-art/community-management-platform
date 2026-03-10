package com.community.service;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.community.entity.MessageNotice;
import com.community.mapper.MessageNoticeMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class MessageNoticeService extends ServiceImpl<MessageNoticeMapper, MessageNotice> {

    public Page<MessageNotice> pageByUser(Long userId, Integer pageNum, Integer pageSize,
                                          Integer isRead, String messageType) {
        LambdaQueryWrapper<MessageNotice> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MessageNotice::getUserId, userId);
        wrapper.eq(isRead != null, MessageNotice::getIsRead, isRead);
        wrapper.eq(StrUtil.isNotBlank(messageType), MessageNotice::getMessageType, messageType);
        wrapper.orderByDesc(MessageNotice::getCreateTime);
        return page(new Page<>(pageNum, pageSize), wrapper);
    }

    public Page<MessageNotice> pageAll(Integer pageNum, Integer pageSize, Long userId,
                                       Integer isRead, String messageType) {
        LambdaQueryWrapper<MessageNotice> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(userId != null, MessageNotice::getUserId, userId);
        wrapper.eq(isRead != null, MessageNotice::getIsRead, isRead);
        wrapper.eq(StrUtil.isNotBlank(messageType), MessageNotice::getMessageType, messageType);
        wrapper.orderByDesc(MessageNotice::getCreateTime);
        return page(new Page<>(pageNum, pageSize), wrapper);
    }

    public long unreadCount(Long userId) {
        return count(new LambdaQueryWrapper<MessageNotice>()
                .eq(MessageNotice::getUserId, userId)
                .eq(MessageNotice::getIsRead, 0));
    }

    public boolean markRead(Long id, Long userId) {
        MessageNotice message = getById(id);
        if (message == null || !message.getUserId().equals(userId)) {
            return false;
        }
        if (message.getIsRead() != null && message.getIsRead() == 1) {
            return true;
        }
        message.setIsRead(1);
        message.setReadTime(LocalDateTime.now());
        return updateById(message);
    }

    public void markAllRead(Long userId) {
        LambdaQueryWrapper<MessageNotice> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MessageNotice::getUserId, userId).eq(MessageNotice::getIsRead, 0);

        MessageNotice updateEntity = new MessageNotice();
        updateEntity.setIsRead(1);
        updateEntity.setReadTime(LocalDateTime.now());
        update(updateEntity, wrapper);
    }

    public void sendMessage(Long userId, String title, String content,
                            String messageType, String businessType, Long businessId) {
        MessageNotice message = new MessageNotice();
        message.setUserId(userId);
        message.setTitle(title);
        message.setContent(content);
        message.setMessageType(messageType);
        message.setBusinessType(businessType);
        message.setBusinessId(businessId);
        message.setIsRead(0);
        save(message);
    }
}

