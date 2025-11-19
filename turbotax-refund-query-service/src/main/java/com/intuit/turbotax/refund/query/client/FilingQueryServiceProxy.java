package com.intuit.turbotax.refund.query.client;

import java.util.List;
import java.util.Optional;

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

import com.intuit.turbotax.api.model.TaxFiling;
import com.intuit.turbotax.api.service.FilingQueryService;
import reactor.core.publisher.Mono;

/**
 * HTTP client proxy for the Filing Data Service.
 * Handles communication with the turbotax-filing-query-service microservice.
 */
@Component
public class FilingQueryServiceProxy implements FilingQueryService {
    private static final Logger LOG = LoggerFactory.getLogger(FilingQueryServiceProxy.class);

    private final RestTemplate restTemplate;
    private final String baseUrl;

    public FilingQueryServiceProxy(RestTemplate restTemplate,
            @Value("${app.filing-query-service.host:localhost}") String serviceHost,
            @Value("${app.filing-query-service.port:7001}") int servicePort) {
        this.restTemplate = restTemplate;
        this.baseUrl = "http://" + serviceHost + ":" + servicePort;
        LOG.info("Configured FilingQueryServiceProxy with base URL: {}", this.baseUrl);
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
                new ParameterizedTypeReference<List<TaxFiling>>() {}
            );
            
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

    @Override
    public Mono<TaxFiling> getFiling(int filingId) {
        String url = baseUrl + "/filings/" + filingId;
        
        LOG.debug("Requesting filing data from: {} for filingId: {}", url, filingId);
        
        try {
            ResponseEntity<TaxFiling> response = restTemplate.exchange(
                url, 
                HttpMethod.GET, 
                null, 
                TaxFiling.class
            );
            
            TaxFiling filing = response.getBody();
            LOG.debug("Successfully retrieved filing for filingId: {}: {}", 
                     filingId, filing != null ? "found" : "not found");
            return filing != null ? Mono.just(filing) : Mono.empty();
            
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                LOG.debug("No filing data found for filingId: {}", filingId);
                return Mono.empty();
            } else {
                LOG.error("HTTP error fetching filing data for filingId: {} - Status: {}, Message: {}", 
                         filingId, e.getStatusCode(), e.getMessage());
                return Mono.error(new RuntimeException("Failed to fetch filing data for filingId: " + filingId, e));
            }
        } catch (Exception e) {
            LOG.error("Unexpected error fetching filing data for filingId: {} - {}", filingId, e.getMessage(), e);
            return Mono.error(new RuntimeException("Failed to fetch filing data for filingId: " + filingId, e));
        }
    }
}
