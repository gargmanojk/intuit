package com.intuit.turbotax.refundstatus.domain.ai;

import com.intuit.turbotax.refundstatus.domain.filing.FilingMetadata;
import com.intuit.turbotax.refundstatus.domain.refund.RefundStatus;

public interface AiRefundEtaService {
    RefundEtaPrediction predictEta(FilingMetadata filing, RefundStatus status);
}
