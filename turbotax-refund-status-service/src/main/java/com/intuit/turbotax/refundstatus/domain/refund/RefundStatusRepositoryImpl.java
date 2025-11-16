package com.intuit.turbotax.refundstatus.domain.refund;

import java.util.List;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import org.springframework.stereotype.Component;

@Component
public class RefundStatusRepositoryImpl implements RefundStatusRepository {

    public List<RefundStatus> findByFilingId(String filingId) {
        RefundStatus federalRefundStatus = new RefundStatus("DEPOSITED", filingId, Jurisdiction.FEDERAL,
                RefundCanonicalStatus.DEPOSITED, "1001", "MSG_REFUND_DEPOSITED", Instant.now(),
                BigDecimal.valueOf(1000));
        RefundStatus stateRefundStatus = new RefundStatus("DEPOSITED", filingId, Jurisdiction.STATE_CA,
                RefundCanonicalStatus.PROCESSING, "1002", "MSG_REFUND_PROCESSING", Instant.now(),
                BigDecimal.valueOf(100));

        var list = new ArrayList<RefundStatus>();
        list.add(federalRefundStatus);
        list.add(stateRefundStatus);

        return list;
    }
}
