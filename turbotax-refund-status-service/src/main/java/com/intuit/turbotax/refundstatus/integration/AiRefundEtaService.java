package com.intuit.turbotax.refundstatus.integration;

import java.util.Optional;

import com.intuit.turbotax.contract.AiFeatures;
import com.intuit.turbotax.contract.EtaRefundInfo;

public interface AiRefundEtaService {
    Optional<EtaRefundInfo> predictEta(AiFeatures aiFeatures);
}
