package com.intuit.turbotax.aieta.api;

import java.util.Optional;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.intuit.turbotax.domainmodel.dto.RefundEtaRequest;
import com.intuit.turbotax.domainmodel.dto.RefundEtaResponse;

public interface AiRefundEtaService {
    @GetMapping(value = "/refund-eta", produces = "application/json")
	Optional<RefundEtaResponse> predictEta(@ModelAttribute RefundEtaRequest req);
}
