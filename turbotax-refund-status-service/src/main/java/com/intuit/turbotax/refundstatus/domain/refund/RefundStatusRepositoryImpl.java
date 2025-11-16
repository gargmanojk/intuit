package com.intuit.turbotax.refundstatus.domain.refund;

import java.util.List;
import java.util.Collections;
import org.springframework.stereotype.Component;

@Component
public class RefundStatusRepositoryImpl implements RefundStatusRepository {
    public List<RefundStatus> findByFilingId(String filingId)
    {
        return Collections.emptyList();
    }
}
