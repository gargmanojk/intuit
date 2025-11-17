package com.intuit.turbotax.refundstatus.integration;

import java.util.Optional;

import com.intuit.turbotax.domainmodel.EtaRefundRequest;
import com.intuit.turbotax.domainmodel.EtaRefundInfo;

public interface AiRefundEtaService {
    Optional<EtaRefundInfo> predictEta(EtaRefundRequest req);
}
