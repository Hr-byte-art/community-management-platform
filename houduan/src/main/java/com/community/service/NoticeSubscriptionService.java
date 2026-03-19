package com.community.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.community.common.Constants;
import com.community.entity.NoticeSubscription;
import com.community.mapper.NoticeSubscriptionMapper;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class NoticeSubscriptionService extends ServiceImpl<NoticeSubscriptionMapper, NoticeSubscription> {

    public List<String> supportedTypes() {
        return List.of(
                Constants.NoticeType.NOTICE,
                Constants.NoticeType.ANNOUNCEMENT,
                Constants.NoticeType.NEWS
        );
    }

    public Map<String, Boolean> getUserSubscriptions(Long userId) {
        LinkedHashMap<String, Boolean> result = new LinkedHashMap<>();
        for (String type : supportedTypes()) {
            result.put(type, true);
        }
        if (userId == null) {
            return result;
        }

        List<NoticeSubscription> subscriptions = list(new LambdaQueryWrapper<NoticeSubscription>()
                .eq(NoticeSubscription::getUserId, userId));
        for (NoticeSubscription subscription : subscriptions) {
            if (result.containsKey(subscription.getNoticeType())) {
                result.put(subscription.getNoticeType(), Boolean.TRUE.equals(subscription.getReceiveNotification()));
            }
        }
        return result;
    }

    public void saveUserSubscriptions(Long userId, Map<String, Boolean> preferences) {
        if (userId == null || preferences == null) {
            return;
        }

        for (String type : supportedTypes()) {
            Boolean receiveNotification = preferences.containsKey(type) ? preferences.get(type) : Boolean.TRUE;
            NoticeSubscription existing = getOne(new LambdaQueryWrapper<NoticeSubscription>()
                    .eq(NoticeSubscription::getUserId, userId)
                    .eq(NoticeSubscription::getNoticeType, type)
                    .last("limit 1"), false);

            if (existing == null) {
                NoticeSubscription subscription = new NoticeSubscription();
                subscription.setUserId(userId);
                subscription.setNoticeType(type);
                subscription.setReceiveNotification(Boolean.TRUE.equals(receiveNotification));
                save(subscription);
            } else {
                existing.setReceiveNotification(Boolean.TRUE.equals(receiveNotification));
                updateById(existing);
            }
        }
    }

    public List<NoticeSubscription> listByNoticeType(String noticeType) {
        return list(new LambdaQueryWrapper<NoticeSubscription>()
                .eq(NoticeSubscription::getNoticeType, noticeType));
    }
}
