package com.intuit.turbotax.refund.prediction.client;

import static org.springframework.http.HttpMethod.GET;

import com.intuit.turbotax.api.model.TaxFiling;
import com.intuit.turbotax.api.service.FilingQueryService;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Component
public class FilingQueryServiceProxy implements FilingQueryService {
    private static final Logger LOG = LoggerFactory.getLogger(FilingQueryServiceProxy.class);

    private final RestTemplate restTemplate;
    private final String serviceUrl;

    public FilingQueryServiceProxy(RestTemplate restTemplate,
            @Value("${app.filing-query-service.host}") String serviceHost,
            @Value("${app.filing-query-service.port}") int servicePort) {
        this.restTemplate = restTemplate;
        this.serviceUrl = "http://" + serviceHost + ":" + servicePort + "/filings";
    }

    @Override
    @SuppressWarnings("null")
    public List<TaxFiling> getFilings(String userId) {
        LOG.debug("Fetching latest filings for userId={}", userId);
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("X-User-Id", userId);
            HttpEntity<Void> entity = new HttpEntity<>(headers);
            
            List<TaxFiling> response = restTemplate
                .exchange(serviceUrl, GET, entity, new ParameterizedTypeReference<List<TaxFiling>>() {})
                .getBody();
 
            LOG.debug("Retrieved {} filings for userId={}", response != null ? response.size() : 0, userId);
            return response != null ? response : List.of();
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                LOG.info("No filing metadata found for userId: {}", userId);
                return List.of();
            } else {
                LOG.error("Error fetching filing metadata for userId: {}: {}", userId, e.getMessage());
                throw e;
            }
        }
    }

    @Override
    @SuppressWarnings("null")
    public Mono<TaxFiling> getFiling(int filingId) {
        LOG.debug("Fetching filing for filingId={}", filingId);
        String url = serviceUrl + "/" + filingId;
        try {
            TaxFiling response = restTemplate
                .exchange(url, GET, null, TaxFiling.class)
                .getBody();
                
            LOG.debug("Retrieved filing for filingId={}: {}", filingId, response != null ? "found" : "not found");
            return response != null ? Mono.just(response) : Mono.empty();
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                LOG.debug("No filing found for filingId={}", filingId);
                return Mono.empty();
            } else {
                LOG.error("Error fetching filing for filingId={}: {}", filingId, e.getMessage());
                return Mono.error(e);
            }
        }
    }
}