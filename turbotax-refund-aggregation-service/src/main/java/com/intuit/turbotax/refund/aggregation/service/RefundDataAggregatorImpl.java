package com.intuit.turbotax.refund.aggregation.service;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable; 
import org.springframework.web.bind.annotation.RestController; 

import com.intuit.turbotax.api.service.RefundDataAggregator;
import com.intuit.turbotax.api.model.RefundStatusData;
import com.intuit.turbotax.refund.aggregation.repository.RefundStatusAggregate;
import com.intuit.turbotax.refund.aggregation.repository.RefundStatusRepository;
import com.intuit.turbotax.client.ExternalIrsClient;
import com.intuit.turbotax.client.ExternalStateTaxClient;
import com.intuit.turbotax.client.MoneyMovementClient;


@RestController
public class RefundDataAggregatorImpl implements RefundDataAggregator {
    private static final Logger LOG = LoggerFactory.getLogger(RefundDataAggregatorImpl.class);

    private final RefundStatusRepository repository;
    private final RefundStatusMapper mapper;
    private final ExternalIrsClient irsClient;
    private final ExternalStateTaxClient stateClient;
    private final MoneyMovementClient moneyMovementClient;

    public RefundDataAggregatorImpl(RefundStatusRepository repository,
            RefundStatusMapper mapper,
            ExternalIrsClient irsClient,
            ExternalStateTaxClient stateClient,
            MoneyMovementClient moneyMovementClient) {
        this.repository = repository;
        this.mapper = mapper;
        this.irsClient = irsClient;
        this.stateClient = stateClient;
        this.moneyMovementClient = moneyMovementClient;
    }

    @Override
    @GetMapping(
        value = "/aggregate-status/{filingId}",
        produces = "application/json")
    public Optional<RefundStatusData> getRefundStatusForFiling(@PathVariable int filingId) {
        LOG.debug("Getting refund status for filingId={}", filingId);
        
        // Get status from repository directly
        Optional<RefundStatusAggregate> status = repository.findByFilingId(filingId);
        if (status.isEmpty()) {
            LOG.debug("No refund status found for filingId={}", filingId);
            return Optional.empty();
        }

        LOG.debug("Found refund status for filingId={}", filingId);
        // Convert to DTO using mapper
        RefundStatusData resultData = mapper.aggregateToDto(filingId, status.get());
        return Optional.of(resultData);
    }
}
