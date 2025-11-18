package com.intuit.turbotax.contract.service;

import java.util.Optional;

import com.intuit.turbotax.contract.data.AiFeatures;
import com.intuit.turbotax.contract.data.EtaRefundInfo;

public interface RefundEtaPredictor {
	Optional<EtaRefundInfo> predictEta(AiFeatures aiFeatures);
}