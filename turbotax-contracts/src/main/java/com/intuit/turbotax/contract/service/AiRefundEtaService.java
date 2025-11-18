package com.intuit.turbotax.contract.service;

import java.util.Optional;

import com.intuit.turbotax.contract.AiFeatures;
import com.intuit.turbotax.contract.EtaRefundInfo;

public interface AiRefundEtaService {
	Optional<EtaRefundInfo> predictEta(AiFeatures aiFeatures);
}