package com.intuit.turbotax.aieta.api;

import java.util.Optional;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.intuit.turbotax.contract.EtaRefundRequest;
import com.intuit.turbotax.contract.EtaRefundInfo;

public interface AiRefundEtaService {
    @GetMapping(value = "/refund-eta", produces = "application/json")
	Optional<EtaRefundInfo> predictEta(@ModelAttribute EtaRefundRequest req);
}
