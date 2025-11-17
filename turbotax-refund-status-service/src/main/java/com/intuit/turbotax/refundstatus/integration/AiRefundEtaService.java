package com.intuit.turbotax.refundstatus.integration;

import java.util.Optional;

import com.intuit.turbotax.contract.EtaRefundRequest;
import com.intuit.turbotax.contract.EtaRefundInfo;

public interface AiRefundEtaService {
    Optional<EtaRefundInfo> predictEta(EtaRefundRequest req);
}
