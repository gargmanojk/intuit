package com.intuit.turbotax.refund.aggregation.job;

import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.intuit.turbotax.refund.aggregation.service.RefundStatusUpdateService;

@ExtendWith(MockitoExtension.class)
class RefundStatusUpdateJobTest {

    @Mock
    private RefundStatusUpdateService updateService;

    @InjectMocks
    private RefundStatusUpdateJob job;

    @Test
    void updateRefundStatuses_ShouldCallUpdateService() {
        // When
        job.updateRefundStatuses();

        // Then
        verify(updateService).updateAllActiveFilings();
    }
}