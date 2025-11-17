package com.intuit.turbotax.refundstatus.integration;

import java.util.Optional;

import com.intuit.turbotax.refundstatus.dto.RefundEtaRequest;
import com.intuit.turbotax.refundstatus.dto.RefundEtaResponse;

public interface AiRefundEtaService {
    Optional<RefundEtaResponse> predictEta(RefundEtaRequest req);
}
