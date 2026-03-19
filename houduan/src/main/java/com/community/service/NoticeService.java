package com.community.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.community.common.Constants;
import com.community.entity.Notice;
import com.community.entity.NoticeSubscription;
import com.community.entity.SysUser;
import com.community.mapper.NoticeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import cn.hutool.core.util.StrUtil;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class NoticeService extends ServiceImpl<NoticeMapper, Notice> {

    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private NoticeSubscriptionService noticeSubscriptionService;
    @Autowired
    private MessageNoticeService messageNoticeService;

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

        // 保存或更新通知
        if (notice.getId() == null) {
            if (notice.getViewCount() == null) {
                notice.setViewCount(0);
            }
            save(notice);
        } else {
            Notice existing = getById(notice.getId());
            if (existing != null && notice.getViewCount() == null) {
                notice.setViewCount(existing.getViewCount());
            }
            if (notice.getViewCount() == null) {
                notice.setViewCount(0);
            }
            updateById(notice);
        }

        // 推送给订阅了该通知类型的用户
        notifySubscribers(notice);
    }

    /**
     * 推送给订阅了该类型通知的用户
     */
    private void notifySubscribers(Notice notice) {
        if (notice == null || StrUtil.isBlank(notice.getNoticeType())) {
            return;
        }

        // 获取所有启用用户，未显式关闭订阅时默认接收通知
        List<SysUser> users = sysUserService.list(
            new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getStatus, Constants.UserStatus.ENABLED)
        );

        Map<Long, Boolean> subscriptionMap = noticeSubscriptionService.listByNoticeType(notice.getNoticeType())
                .stream()
                .collect(Collectors.toMap(
                        NoticeSubscription::getUserId,
                        item -> Boolean.TRUE.equals(item.getReceiveNotification()),
                        (left, right) -> right
                ));

        Set<Long> receiverIds = users.stream()
                .map(SysUser::getId)
                .filter(userId -> !userId.equals(notice.getPublisherId()))
                .filter(userId -> !subscriptionMap.containsKey(userId) || Boolean.TRUE.equals(subscriptionMap.get(userId)))
                .collect(Collectors.toSet());

        String typeLabel = switch (notice.getNoticeType()) {
            case Constants.NoticeType.ANNOUNCEMENT -> "公告";
            case Constants.NoticeType.NEWS -> "新闻";
            default -> "通知";
        };

        for (Long receiverId : receiverIds) {
            messageNoticeService.sendMessage(
                    receiverId,
                    "您订阅的" + typeLabel + "已发布",
                    "新发布的" + typeLabel + "：" + notice.getTitle(),
                    Constants.MessageType.SYSTEM,
                    Constants.MessageBusinessType.NOTICE,
                    notice.getId()
            );
        }
    }
}
