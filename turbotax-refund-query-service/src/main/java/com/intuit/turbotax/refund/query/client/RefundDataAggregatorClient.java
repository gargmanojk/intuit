package com.intuit.turbotax.refund.query.client;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.intuit.turbotax.api.v1.refund.model.RefundStatusData;
import com.intuit.turbotax.api.v1.refund.service.RefundDataAggregator;

/**
 * HTTP client proxy for the Refund Aggregation Service.
 * Handles communication with the turbotax-refund-aggregation-service
 * microservice.
 */
@Component
public class RefundDataAggregatorClient implements RefundDataAggregator {
    private static final Logger LOG = LoggerFactory.getLogger(RefundDataAggregatorClient.class);

    private final RestTemplate restTemplate;
    private final String baseUrl;

    public RefundDataAggregatorClient(RestTemplate restTemplate,
            @Value("${app.refund-aggregation-service.host}") String serviceHost,
            @Value("${app.refund-aggregation-service.port}") int servicePort) {
        this.restTemplate = restTemplate;
        this.baseUrl = "http://" + serviceHost + ":" + servicePort;
        LOG.info("Configured RefundDataAggregatorClient with base URL: {}", this.baseUrl);
    }

    @Override
    public Optional<RefundStatusData> getRefundStatusForFiling(int filingId) {
        String url = baseUrl + "/api/v1/aggregate-status/filings/" + filingId;

        LOG.debug("Requesting refund status data from: {}", url);

        try {
            ResponseEntity<RefundStatusData> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    RefundStatusData.class);

            RefundStatusData statusData = response.getBody();

            LOG.debug("Successfully retrieved refund status for filingId: {}", filingId);
            return Optional.ofNullable(statusData);

        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                LOG.debug("No refund status data found for filingId: {}", filingId);
                return Optional.empty();
            } else {
                LOG.error("HTTP error fetching refund status data for filingId: {} - Status: {}, Message: {}",
                        filingId, e.getStatusCode(), e.getMessage());
                throw new RuntimeException("Failed to fetch refund status data for filing: " + filingId, e);
            }
        } catch (Exception e) {
            LOG.error("Unexpected error fetching refund status data for filingId: {} - {}",
                    filingId, e.getMessage(), e);
            throw new RuntimeException("Failed to fetch refund status data for filing: " + filingId, e);
        }
    }
}
