package com.intuit.turbotax.refund.query.client;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.intuit.turbotax.api.model.RefundEtaPrediction;
import com.intuit.turbotax.api.service.RefundEtaPredictor;

/**
 * HTTP client proxy for the Refund Prediction Service.
 * Handles communication with the turbotax-refund-prediction-service microservice.
 */
@Component
public class RefundEtaPredictorProxy implements RefundEtaPredictor {
    private static final Logger LOG = LoggerFactory.getLogger(RefundEtaPredictorProxy.class);

    private final RestTemplate restTemplate;
    private final String baseUrl;

    public RefundEtaPredictorProxy(RestTemplate restTemplate,
            @Value("${app.refund-prediction-service.host:localhost}") String serviceHost,
            @Value("${app.refund-prediction-service.port:7003}") int servicePort) {
        this.restTemplate = restTemplate;
        this.baseUrl = "http://" + serviceHost + ":" + servicePort;
        LOG.info("Configured RefundEtaPredictorProxy with base URL: {}", this.baseUrl);
    }

    @Override
    public List<RefundEtaPrediction> predictEta(int filingId) {
        String url = baseUrl + "/refund-eta/" + filingId;
        
        LOG.debug("Requesting ETA predictions from: {}", url);
        
        try {
            ResponseEntity<List<RefundEtaPrediction>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<RefundEtaPrediction>>() {}
            );
            
            List<RefundEtaPrediction> predictions = response.getBody();
            if (predictions == null) {
                predictions = List.of();
            }
            
            LOG.debug("Successfully retrieved {} ETA predictions for filingId: {}", 
                     predictions.size(), filingId);
            return predictions;
            
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                LOG.debug("No ETA predictions found for filingId: {}", filingId);
                return List.of();
            } else {
                LOG.error("HTTP error fetching ETA predictions for filingId: {} - Status: {}, Message: {}", 
                         filingId, e.getStatusCode(), e.getMessage());
                throw new RuntimeException("Failed to fetch ETA predictions for filing: " + filingId, e);
            }
        } catch (Exception e) {
            LOG.error("Unexpected error fetching ETA predictions for filingId: {} - {}", 
                     filingId, e.getMessage(), e);
            throw new RuntimeException("Failed to fetch ETA predictions for filing: " + filingId, e);
        }
    }
}
