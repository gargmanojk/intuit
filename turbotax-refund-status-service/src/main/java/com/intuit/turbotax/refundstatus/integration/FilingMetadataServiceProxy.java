package com.intuit.turbotax.refundstatus.integration;

import com.intuit.turbotax.refundstatus.dto.FilingMetadataResponse;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Component
public class FilingMetadataServiceProxy implements FilingMetadataService {
    private static final Logger LOG = LoggerFactory.getLogger(FilingMetadataServiceProxy.class);

    private final RestTemplate restTemplate;
    private final String serviceUrl;

    public FilingMetadataServiceProxy(RestTemplate restTemplate,
            @Value("${app.filing-metadata-service.host}") String serviceHost,
            @Value("${app.filing-metadata-service.port}") int servicePort) {
        this.restTemplate = restTemplate;
        this.serviceUrl = "http://" + serviceHost + ":" + servicePort + "/filing-status/";
    };

    @Override
    public Optional<FilingMetadataResponse> findLatestFilingForUser(String userId) {
        String url = serviceUrl + userId;
        try {
            FilingMetadataResponse response = restTemplate.getForObject(url, FilingMetadataResponse.class);
            return Optional.ofNullable(response);
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                LOG.info("No filing metadata found for userId: {}", userId);
                return Optional.empty();
            } else {
                LOG.error("Error fetching filing metadata for userId: {}: {}", userId, e.getMessage());
                throw e;
            }
        }
    }
}
