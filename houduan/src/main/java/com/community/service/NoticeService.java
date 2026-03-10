package com.community.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.community.common.Constants;
import com.community.entity.Notice;
import com.community.entity.SysUser;
import com.community.mapper.NoticeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import cn.hutool.core.util.StrUtil;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class NoticeService extends ServiceImpl<NoticeMapper, Notice> {

    @Autowired
    private SysUserService sysUserService;

    public Page<Notice> pageQuery(Integer pageNum, Integer pageSize, String title, String noticeType, Integer status) {
        LambdaQueryWrapper<Notice> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StrUtil.isNotBlank(title), Notice::getTitle, title);
        wrapper.eq(StrUtil.isNotBlank(noticeType), Notice::getNoticeType, noticeType);
        wrapper.eq(status != null, Notice::getStatus, status);
        wrapper.orderByDesc(Notice::getIsTop).orderByDesc(Notice::getPublishTime);
        return page(new Page<>(pageNum, pageSize), wrapper);
    }

    public long countPublished() {
        return count(new LambdaQueryWrapper<Notice>().eq(Notice::getStatus, Constants.NoticeStatus.PUBLISHED));
    }

    /**
     * 发布通知，并推送给订阅用户
     */
    public void publishAndNotify(Notice notice, Long publisherId) {
        // 设置发布时间和状态
        notice.setPublishTime(LocalDateTime.now());
        notice.setStatus(Constants.NoticeStatus.PUBLISHED);
        notice.setPublisherId(publisherId);

        // 保存通知
        save(notice);

        // 推送给订阅了该通知类型的用户
        notifySubscribers(notice);
    }

    /**
     * 推送给订阅了该类型通知的用户
     */
    private void notifySubscribers(Notice notice) {
        // 获取所有启用了该类型通知的用户
        List<SysUser> users = sysUserService.list(
            new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getStatus, Constants.UserStatus.ENABLED)
        );

        // 这里可以集成消息推送功能，比如邮件、短信或站内信
        // 简单实现：为每个用户创建一条通知阅读记录（表示已发送）
        // 注意：实际环境中应该通过异步队列处理，以避免影响响应性能
    }
}
