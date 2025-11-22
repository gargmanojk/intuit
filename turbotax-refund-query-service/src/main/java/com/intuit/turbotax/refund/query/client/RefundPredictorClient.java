package com.intuit.turbotax.refund.query.client;

import java.util.List;
import java.util.Optional;
import java.util.Map;

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

import com.intuit.turbotax.api.model.PredictionFeature;
import com.intuit.turbotax.api.model.RefundPrediction;
import com.intuit.turbotax.api.service.RefundPredictor;

/**
 * HTTP client proxy for the Refund Prediction Service.
 * Handles communication with the turbotax-refund-prediction-service
 * microservice.
 */
@Component
public class RefundPredictorClient implements RefundPredictor {
    private static final Logger LOG = LoggerFactory.getLogger(RefundPredictorClient.class);

    private final RestTemplate restTemplate;
    private final String serviceUrl;
    private final String apiKey;

    public RefundPredictorClient(
            RestTemplate restTemplate,
            @Value("${app.refund-prediction-service.url}") String serviceUrl,
            @Value("${app.refund-prediction-service.api-key}") String apiKey) {
        this.restTemplate = restTemplate;
        this.serviceUrl = serviceUrl;
        this.apiKey = apiKey;
        LOG.info("Configured RefundPredictorClient with base URL: {}", this.serviceUrl);
    }

    @Override
    public Optional<RefundPrediction> predictEta(Map<PredictionFeature, Object> predictionFeatures) {
        try {
            // Build request payload
            ServiceInput payload = buildPayload(predictionFeatures);

            org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
            headers.set("Authorization", "Bearer " + apiKey);
            org.springframework.http.HttpEntity<ServiceInput> entity = new org.springframework.http.HttpEntity<>(payload, headers);

            ResponseEntity<List<Integer>> response = restTemplate.exchange(
                serviceUrl,
                HttpMethod.POST,
                entity,
                new ParameterizedTypeReference<List<Integer>>() {}
            );

            List<Integer> body = response.getBody();
            if (response.getStatusCode() == HttpStatus.OK && body != null && !body.isEmpty()) {
                int etaDays = body.get(0);
                var submissionDate = predictionFeatures.get(PredictionFeature.Submission_Date).toString();
                RefundPrediction prediction = new RefundPrediction(
                    java.time.LocalDate.parse(submissionDate).plusDays(etaDays),
                    0.8, // default confidence
                    3    // default window days
                );
                return Optional.of(prediction);
            }
            return Optional.empty();
        } catch (HttpClientErrorException e) {
            LOG.error("Prediction service error: {}", e.getMessage());
            return Optional.empty();
        } catch (Exception e) {
            LOG.error("Unexpected error calling prediction service: {}", e.getMessage());
            return Optional.empty();
        }
    }

    private ServiceInput buildPayload(Map<PredictionFeature, Object> predictionFeatures) {
        // Extract columns and values from the map
        List<PredictionFeature> columns = predictionFeatures.keySet().stream().toList();
        List<Object> values = columns.stream().map(predictionFeatures::get).toList();
        List<Integer> index = List.of(0); // single row, index 0
        List<List<Object>> data = List.of(values); // single row of values
        InputData inputData = new InputData(columns, index, data);
        return new ServiceInput(inputData);
    }   
}

record ServiceInput(
    InputData input_data
) {}

record InputData(
    List<PredictionFeature> columns,
    List<Integer> index,
    List<List<Object>> data
) {}

