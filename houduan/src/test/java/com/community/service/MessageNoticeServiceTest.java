package com.community.service;

import com.community.entity.MessageNotice;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MessageNoticeServiceTest {

    @Spy
    @InjectMocks
    private MessageNoticeService messageNoticeService;

    @Test
    void markRead_shouldReturnFalseWhenNotFound() {
        doReturn(null).when(messageNoticeService).getById(1L);

        boolean result = messageNoticeService.markRead(1L, 10L);

        assertFalse(result);
    }

    @Test
    void markRead_shouldReturnFalseWhenNoPermission() {
        MessageNotice message = new MessageNotice();
        message.setId(2L);
        message.setUserId(99L);
        message.setIsRead(0);
        doReturn(message).when(messageNoticeService).getById(2L);

        boolean result = messageNoticeService.markRead(2L, 10L);

        assertFalse(result);
    }

    @Test
    void markRead_shouldUpdateStatusAndReadTime() {
        MessageNotice message = new MessageNotice();
        message.setId(3L);
        message.setUserId(10L);
        message.setIsRead(0);

        doReturn(message).when(messageNoticeService).getById(3L);
        doReturn(true).when(messageNoticeService).updateById(any(MessageNotice.class));

        boolean result = messageNoticeService.markRead(3L, 10L);

        assertTrue(result);
        assertEquals(1, message.getIsRead());
        assertNotNull(message.getReadTime());
    }

    @Test
    void markAllRead_shouldCallUpdateWithReadFlag() {
        doReturn(true).when(messageNoticeService).update(any(MessageNotice.class), any());
        ArgumentCaptor<MessageNotice> captor = ArgumentCaptor.forClass(MessageNotice.class);

        messageNoticeService.markAllRead(10L);

        verify(messageNoticeService, times(1)).update(captor.capture(), any());
        MessageNotice payload = captor.getValue();
        assertEquals(1, payload.getIsRead());
        assertNotNull(payload.getReadTime());
    }

    @Test
    void unreadCount_shouldReturnCountResult() {
        doReturn(5L).when(messageNoticeService).count(any());

        long count = messageNoticeService.unreadCount(10L);

        assertEquals(5L, count);
    }

    @Test
    void sendMessage_shouldPersistUnreadMessage() {
        doReturn(true).when(messageNoticeService).save(any(MessageNotice.class));
        ArgumentCaptor<MessageNotice> captor = ArgumentCaptor.forClass(MessageNotice.class);

        messageNoticeService.sendMessage(10L, "标题", "内容", "SYSTEM", "BIZ", 100L);

        verify(messageNoticeService, times(1)).save(captor.capture());
        MessageNotice saved = captor.getValue();
        assertEquals(10L, saved.getUserId());
        assertEquals("标题", saved.getTitle());
        assertEquals(0, saved.getIsRead());
    }
}
