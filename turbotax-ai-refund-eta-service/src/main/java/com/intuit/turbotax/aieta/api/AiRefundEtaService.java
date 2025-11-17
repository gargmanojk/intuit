package com.intuit.turbotax.aieta.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.intuit.turbotax.aieta.dto.RefundEtaRequest;
import com.intuit.turbotax.aieta.dto.RefundEtaResponse;

public interface AiRefundEtaService {
    @GetMapping(value = "/refund-eta", produces = "application/json")
	RefundEtaResponse predictEta(@ModelAttribute RefundEtaRequest req);
}
