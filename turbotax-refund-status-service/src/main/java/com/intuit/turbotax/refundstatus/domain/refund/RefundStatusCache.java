package com.intuit.turbotax.refundstatus.domain.refund;

import java.util.List;
import java.util.Collections;
import org.springframework.stereotype.Component;

public interface RefundStatusCache {

    List<RefundStatus> getStatuses(String filingId);

    void putStatuses(String filingId, List<RefundStatus> statuses);
}

// Simple in-memory mock
@Component
class InMemoryRefundStatusCache implements RefundStatusCache {

    @Override
    public List<RefundStatus> getStatuses(String filingId) {
        return Collections.emptyList();
    }

    @Override
    public void putStatuses(String filingId, List<RefundStatus> statuses) {
        // no-op
    }
}
