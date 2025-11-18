package com.intuit.turbotax.refundstatus.integration;

import java.util.Optional;

import com.intuit.turbotax.contract.data.AiFeatures;
import com.intuit.turbotax.contract.data.EtaRefundInfo;

public interface AiRefundEtaService {
    Optional<EtaRefundInfo> predictEta(AiFeatures aiFeatures);
}
