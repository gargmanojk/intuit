package com.intuit.turbotax.refundstatus.integration;

import java.util.List;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.stereotype.Component;
import com.intuit.turbotax.contract.data.RefundInfo;
import com.intuit.turbotax.contract.service.RefundDataAggregator;

@Component
public class RefundStatusAggregatorServiceProxy implements RefundDataAggregator{
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
    public List<RefundInfo> getRefundStatusesForFiling(String filingId) {
        try {
            String url = serviceUrl + filingId;
            RefundInfo[] response = restTemplate.getForObject(url, RefundInfo[].class);
            
            if (response != null) {
                return Arrays.asList(response);
            } else {
                return List.of();
            }
            
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                return List.of();
            }
            LOG.error("Error retrieving refund statuses for filing ID: {}", filingId, e);
            throw e;
        } catch (Exception e) {
            LOG.error("Unexpected error for filing ID: {}", filingId, e);
            throw e;
        }
    }
}
