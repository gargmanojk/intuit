package com.intuit.turbotax.refundstatus.domain.refund;

import java.util.List;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import org.springframework.stereotype.Component;
import com.intuit.turbotax.domainmodel.Jurisdiction;
import com.intuit.turbotax.domainmodel.RefundCanonicalStatus;

@Component
public class RefundStatusRepositoryImpl implements RefundStatusRepository {

    public List<RefundStatus> findByFilingId(String filingId) {
        RefundStatus federalRefundStatus = RefundStatus.builder()
                .statusId("DEPOSITED")
                .filingId(filingId)
                .jurisdiction(Jurisdiction.FEDERAL)
                .canonicalStatus(RefundCanonicalStatus.DEPOSITED)
                .rawStatusCode("1001")
                .statusMessageKey("MSG_REFUND_DEPOSITED")
                .statusLastUpdatedAt(Instant.now())
                .amount(BigDecimal.valueOf(1000))
                .build();

        RefundStatus stateRefundStatus = RefundStatus.builder()
                .statusId("DEPOSITED")
                .filingId(filingId)
                .jurisdiction(Jurisdiction.STATE_CA)
                .canonicalStatus(RefundCanonicalStatus.PROCESSING)
                .rawStatusCode("1002")
                .statusMessageKey("MSG_REFUND_PROCESSING")
                .statusLastUpdatedAt(Instant.now())
                .amount(BigDecimal.valueOf(100))
                .build();

        var list = new ArrayList<RefundStatus>();
        list.add(federalRefundStatus);
        list.add(stateRefundStatus);

        return list;
    }
}
