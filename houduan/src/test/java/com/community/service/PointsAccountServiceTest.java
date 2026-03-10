package com.community.service;

import com.community.common.Constants;
import com.community.entity.PointsAccount;
import com.community.mapper.PointsAccountMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PointsAccountServiceTest {

    @Mock
    private PointsRecordService pointsRecordService;

    @Mock
    private PointsAccountMapper pointsAccountMapper;

    @Spy
    @InjectMocks
    private PointsAccountService pointsAccountService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(pointsAccountService, "baseMapper", pointsAccountMapper);
    }

    @Test
    void getOrCreateByUserId_createsWhenMissing() {
        PointsAccount created = new PointsAccount();
        created.setUserId(100L);
        created.setTotalPoints(0);

        doReturn(null, created).when(pointsAccountService).getOne(any());
        doReturn(true).when(pointsAccountService).save(any(PointsAccount.class));

        PointsAccount result = pointsAccountService.getOrCreateByUserId(100L);

        assertNotNull(result);
        assertEquals(100L, result.getUserId());
        assertEquals(0, result.getTotalPoints());
        verify(pointsAccountService, times(1)).save(any(PointsAccount.class));
    }

    @Test
    void getOrCreateByUserId_returnsExistingOnDuplicateInsert() {
        PointsAccount existing = new PointsAccount();
        existing.setUserId(101L);
        existing.setTotalPoints(20);

        doReturn(null, existing).when(pointsAccountService).getOne(any());
        doThrow(new DuplicateKeyException("dup")).when(pointsAccountService).save(any(PointsAccount.class));

        PointsAccount result = pointsAccountService.getOrCreateByUserId(101L);

        assertNotNull(result);
        assertEquals(101L, result.getUserId());
        assertEquals(20, result.getTotalPoints());
    }

    @Test
    void addPoints_returnsFalseWhenRecordDuplicate() {
        PointsAccount existing = new PointsAccount();
        existing.setUserId(200L);
        existing.setTotalPoints(0);

        doReturn(existing).when(pointsAccountService).getOne(any());
        doThrow(new DuplicateKeyException("dup")).when(pointsRecordService).save(any());

        boolean result = pointsAccountService.addPoints(
                200L,
                10,
                Constants.PointsChangeType.INCOME,
                Constants.PointsBusinessType.ACTIVITY_CHECKIN,
                1L,
                "签到",
                1L
        );

        assertFalse(result);
        verify(pointsAccountMapper, never()).changePoints(anyLong(), anyInt());
    }

    @Test
    void addPoints_expenseShouldUseNegativeDelta() {
        PointsAccount existing = new PointsAccount();
        existing.setUserId(201L);
        existing.setTotalPoints(100);

        doReturn(existing).when(pointsAccountService).getOne(any());
        doReturn(true).when(pointsRecordService).save(any());

        boolean result = pointsAccountService.addPoints(
                201L,
                15,
                Constants.PointsChangeType.EXPENSE,
                "MANUAL_ADJUST",
                2L,
                "扣减",
                1L
        );

        assertTrue(result);
        verify(pointsAccountMapper, times(1)).changePoints(201L, -15);
    }

    @Test
    void addPoints_invalidInputShouldThrow() {
        assertThrows(IllegalArgumentException.class, () -> pointsAccountService.addPoints(
                null,
                10,
                Constants.PointsChangeType.INCOME,
                "BIZ",
                1L,
                "remark",
                1L
        ));
    }
}
