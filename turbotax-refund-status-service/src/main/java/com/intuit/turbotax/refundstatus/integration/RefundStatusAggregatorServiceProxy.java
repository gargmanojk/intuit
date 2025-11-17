package com.intuit.turbotax.refundstatus.integration;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.stereotype.Component;
import com.intuit.turbotax.refundstatus.dto.RefundStatusAggregatorResponse;

@Component
public class RefundStatusAggregatorServiceProxy implements RefundStatusAggregatorService{
    private static final Logger LOG = LoggerFactory.getLogger(FilingMetadataServiceProxy.class);

    private final RestTemplate restTemplate;
    private final String serviceUrl;

    public RefundStatusAggregatorServiceProxy(RestTemplate restTemplate,
            @Value("${app.refund-status-aggregator-service.host}") String serviceHost,
            @Value("${app.refund-status-aggregator-service.port}") int servicePort) {
        this.restTemplate = restTemplate;
        this.serviceUrl = "http://" + serviceHost + ":" + servicePort + "/aggregate-status/";
    };

    @Override
    public Optional<RefundStatusAggregatorResponse> getRefundStatusesForFiling(String filingId) {
        String url = serviceUrl + filingId;
        try {
            RefundStatusAggregatorResponse response = restTemplate.getForObject(url, RefundStatusAggregatorResponse.class);
            return Optional.ofNullable(response);
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                LOG.info("No refund status aggregator data found for filingId: {}", filingId);
                return Optional.empty();
            } else {
                LOG.error("Error fetching refund status aggregator data for filingId: {}: {}", filingId, e.getMessage());
                throw e;
            }
        }
    }
}
