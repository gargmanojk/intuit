package com.intuit.turbotax.refund.query.client;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.intuit.turbotax.api.v1.filing.model.TaxFiling;
import com.intuit.turbotax.api.v1.filing.service.FilingQueryService;

/**
 * HTTP client proxy for the Filing Data Service.
 * Handles communication with the turbotax-filing-query-service microservice.
 */
@Component
public class FilingQueryServiceClient implements FilingQueryService {
    private static final Logger LOG = LoggerFactory.getLogger(FilingQueryServiceClient.class);

    private final RestTemplate restTemplate;
    private final String baseUrl;

    public FilingQueryServiceClient(RestTemplate restTemplate,
            @Value("${app.filing-query-service.host:localhost}") String serviceHost,
            @Value("${app.filing-query-service.port:7001}") int servicePort) {
        this.restTemplate = restTemplate;
        this.baseUrl = "http://" + serviceHost + ":" + servicePort;
        LOG.info("Configured FilingQueryServiceClient with base URL: {}", this.baseUrl);
    }

    @Override
    public List<TaxFiling> getFilings(String userId) {
        String url = baseUrl + "/filings";

        LOG.debug("Requesting filing data from: {}", url);

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("X-User-Id", userId);
            HttpEntity<Void> entity = new HttpEntity<>(headers);

            ResponseEntity<List<TaxFiling>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    new ParameterizedTypeReference<List<TaxFiling>>() {
                    });

            List<TaxFiling> filings = response.getBody();
            if (filings == null) {
                filings = List.of();
            }

            LOG.debug("Successfully retrieved {} filings for userId: {}", filings.size(), userId);
            return filings;

        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                LOG.debug("No filing data found for userId: {}", userId);
                return List.of();
            } else {
                LOG.error("HTTP error fetching filing data for userId: {} - Status: {}, Message: {}",
                        userId, e.getStatusCode(), e.getMessage());
                throw new RuntimeException("Failed to fetch filing data for user: " + userId, e);
            }
        } catch (Exception e) {
            LOG.error("Unexpected error fetching filing data for userId: {} - {}", userId, e.getMessage(), e);
            throw new RuntimeException("Failed to fetch filing data for user: " + userId, e);
        }
    }
}
