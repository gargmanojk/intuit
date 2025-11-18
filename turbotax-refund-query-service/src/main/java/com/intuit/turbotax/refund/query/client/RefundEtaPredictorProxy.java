package com.intuit.turbotax.refund.query.client;

import com.intuit.turbotax.api.model.RefundEtaPrediction;
import com.intuit.turbotax.api.service.RefundEtaPredictor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

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
    public List<RefundEtaPrediction> predictEta(int filingId) {
        try {
            if (serviceUrl == null) {
                LOG.warn("Service URL is null, cannot make request");
                return List.of();
            }
            
            String uri = serviceUrl + "/" + filingId;
            RefundEtaPrediction[] predictions = restTemplate.getForObject(uri, RefundEtaPrediction[].class);
            return predictions != null ? Arrays.asList(predictions) : List.of();
        } catch (Exception e) {
            LOG.warn("Failed to GET AI refund ETA service at {}: {}", serviceUrl, e.getMessage());
            return List.of();
        }
    }
}
