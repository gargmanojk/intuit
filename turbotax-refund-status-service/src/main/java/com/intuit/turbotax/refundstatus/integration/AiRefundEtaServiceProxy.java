package com.intuit.turbotax.refundstatus.integration;

import com.intuit.turbotax.refundstatus.dto.RefundEtaRequest;
import com.intuit.turbotax.refundstatus.dto.RefundEtaResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Optional;

@Service
public class AiRefundEtaServiceProxy implements AiRefundEtaService {
    private static final Logger LOG = LoggerFactory.getLogger(AiRefundEtaServiceProxy.class);

    private final RestTemplate restTemplate;
    private final String serviceUrl;
  
    public AiRefundEtaServiceProxy (RestTemplate restTemplate,
            @Value("${app.ai-refund-eta-service.host}") String serviceHost,
            @Value("${app.ai-refund-eta-service.port}") int servicePort) {
        this.restTemplate = restTemplate;
        this.serviceUrl = "http://" + serviceHost + ":" + servicePort + "/refund-eta";
    }

    @Override
    public Optional<RefundEtaResponse> predictEta(RefundEtaRequest req) {
        try {
            UriComponentsBuilder ub = UriComponentsBuilder.fromHttpUrl(serviceUrl)
                    .queryParam("taxYear", req.getTaxYear());

            if (req.getFilingDate() != null) {
                ub.queryParam("filingDate", req.getFilingDate().toString());
            }
            if (req.getFederalRefundAmount() != null) {
                ub.queryParam("federalRefundAmount", req.getFederalRefundAmount().toString());
            }
            if (req.getFederalReturnStatus() != null) {
                ub.queryParam("federalReturnStatus", req.getFederalReturnStatus().name());
            }
            if (req.getFederalDisbursementMethod() != null) {
                ub.queryParam("federalDisbursementMethod", req.getFederalDisbursementMethod());
            }
            if (req.getStateRefundAmount() != null) {
                ub.queryParam("stateRefundAmount", req.getStateRefundAmount().toString());
            }
            if (req.getStateJurisdiction() != null) {
                ub.queryParam("stateJurisdiction", req.getStateJurisdiction().name());
            }
            if (req.getStateReturnStatus() != null) {
                ub.queryParam("stateReturnStatus", req.getStateReturnStatus().name());
            }
            if (req.getStateDisbursementMethod() != null) {
                ub.queryParam("stateDisbursementMethod", req.getStateDisbursementMethod());
            }

            String uri = ub.build().toUriString();
            RefundEtaResponse resp = restTemplate.getForObject(uri, RefundEtaResponse.class);
            return Optional.ofNullable(resp);
        } catch (Exception e) {
            LOG.warn("Failed to GET AI refund ETA service at {}: {}", serviceUrl, e.getMessage());
            return Optional.empty();
        }
    }
}
