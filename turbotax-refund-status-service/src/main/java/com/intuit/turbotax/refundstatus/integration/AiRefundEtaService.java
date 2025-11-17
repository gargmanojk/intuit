package com.intuit.turbotax.refundstatus.integration;

import java.util.Optional;

import com.intuit.turbotax.domainmodel.dto.RefundEtaRequest;
import com.intuit.turbotax.domainmodel.dto.RefundEtaDto;

public interface AiRefundEtaService {
    Optional<RefundEtaDto> predictEta(RefundEtaRequest req);
}
