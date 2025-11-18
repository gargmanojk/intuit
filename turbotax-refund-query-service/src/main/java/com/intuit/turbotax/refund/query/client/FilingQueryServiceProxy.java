package com.intuit.turbotax.refund.query.client;

import static org.springframework.http.HttpMethod.GET;

import com.intuit.turbotax.api.model.TaxFiling;
import com.intuit.turbotax.api.service.FilingQueryService;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
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
            @Value("${app.filing-metadata-service.host}") String serviceHost,
            @Value("${app.filing-metadata-service.port}") int servicePort) {
        this.restTemplate = restTemplate;
        this.serviceUrl = "http://" + serviceHost + ":" + servicePort + "/filing-status/";
    };

    @Override
    @SuppressWarnings("null")
    public List<TaxFiling> findLatestFilingForUser(String userId) {
        String url = serviceUrl + userId;
        try {
            // List<TaxFiling> response = restTemplate.getForObject(url, TaxFiling.class);
            List<TaxFiling> response = restTemplate
                .exchange(url, GET, null, new ParameterizedTypeReference<List<TaxFiling>>() {})
                .getBody();
 
            return response;
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
}
