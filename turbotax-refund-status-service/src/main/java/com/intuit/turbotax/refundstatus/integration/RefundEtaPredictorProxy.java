package com.intuit.turbotax.refundstatus.integration;

import com.intuit.turbotax.contract.data.RefundPredictionInput;
import com.intuit.turbotax.contract.data.RefundEtaPrediction;
import com.intuit.turbotax.contract.service.RefundEtaPredictor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Optional;

@Component
public class RefundEtaPredictorProxy implements RefundEtaPredictor {
    private static final Logger LOG = LoggerFactory.getLogger(RefundEtaPredictorProxy.class);

    private final RestTemplate restTemplate;
    private final String serviceUrl;
  
    public RefundEtaPredictorProxy (RestTemplate restTemplate,
            @Value("${app.ai-refund-eta-service.host}") String serviceHost,
            @Value("${app.ai-refund-eta-service.port}") int servicePort) {
        this.restTemplate = restTemplate;
        this.serviceUrl = "http://" + serviceHost + ":" + servicePort + "/refund-eta";
    }

    @Override
    @SuppressWarnings("null")
    public Optional<RefundEtaPrediction> predictEta(RefundPredictionInput predictionInput) {
        try {
            if (serviceUrl == null) {
                LOG.warn("Service URL is null, cannot make request");
                return Optional.empty();
            }
            
            UriComponentsBuilder ub = UriComponentsBuilder.fromUriString(serviceUrl)
                    .queryParam("taxYear", predictionInput.getTaxYear());

            if (predictionInput.getFilingDate() != null) {
                ub.queryParam("filingDate", predictionInput.getFilingDate().toString());
            }
            if (predictionInput.getRefundAmount() != null) {
                ub.queryParam("refundAmount", predictionInput.getRefundAmount().toString());
            }
            if (predictionInput.getReturnStatus() != null) {
                ub.queryParam("returnStatus", predictionInput.getReturnStatus().name());
            }
            if (predictionInput.getDisbursementMethod() != null) {
                ub.queryParam("disbursementMethod", predictionInput.getDisbursementMethod());
            }
            if (predictionInput.getJurisdiction() != null) {
                ub.queryParam("jurisdiction", predictionInput.getJurisdiction().name());
            }   

            String uri = ub.build().toUriString();
            RefundEtaPrediction resp = restTemplate.getForObject(uri, RefundEtaPrediction.class);
            return Optional.ofNullable(resp);
        } catch (Exception e) {
            LOG.warn("Failed to GET AI refund ETA service at {}: {}", serviceUrl, e.getMessage());
            return Optional.empty();
        }
    }
}
