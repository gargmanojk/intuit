package com.intuit.turbotax.refund.query.client;

import com.intuit.turbotax.api.model.RefundPredictionInput;
import com.intuit.turbotax.api.model.RefundEtaPrediction;
import com.intuit.turbotax.api.service.RefundEtaPredictor;

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
                    .queryParam("taxYear", predictionInput.taxYear());

            if (predictionInput.filingDate() != null) {
                ub.queryParam("filingDate", predictionInput.filingDate().toString());
            }
            if (predictionInput.refundAmount() != null) {
                ub.queryParam("refundAmount", predictionInput.refundAmount().toString());
            }
            if (predictionInput.returnStatus() != null) {
                ub.queryParam("returnStatus", predictionInput.returnStatus().name());
            }
            if (predictionInput.disbursementMethod() != null) {
                ub.queryParam("disbursementMethod", predictionInput.disbursementMethod());
            }
            if (predictionInput.jurisdiction() != null) {
                ub.queryParam("jurisdiction", predictionInput.jurisdiction().name());
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
