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
import com.intuit.turbotax.refund.aggregation.client.ExternalIrsClient;
import com.intuit.turbotax.refund.aggregation.client.ExternalStateTaxClient;
import com.intuit.turbotax.refund.aggregation.client.MoneyMovementClient;


@RestController
public class RefundDataAggregatorImpl implements RefundDataAggregator {
    private static final Logger LOG = LoggerFactory.getLogger(RefundDataAggregatorImpl.class);

    private final RefundStatusRepository repository;
    private final ExternalIrsClient irsClient;
    private final ExternalStateTaxClient stateClient;
    private final MoneyMovementClient moneyMovementClient;

    public RefundDataAggregatorImpl(RefundStatusRepository repository,
            ExternalIrsClient irsClient,
            ExternalStateTaxClient stateClient,
            MoneyMovementClient moneyMovementClient) {
        this.repository = repository;
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
        // Convert to aggregator DTO
        RefundStatusData resultData = convertToAggregatorDto(filingId, status.get());
        return Optional.of(resultData);
    }

    /**
     * Converts a single RefundStatus domain object to a RefundStatusAggregatorDto.
    */  
    private RefundStatusData convertToAggregatorDto(int filingId, RefundStatusAggregate status) {
        if (status == null) {
            return null;
        }       
        return new RefundStatusData(
                filingId,
                status.status(),
                status.jurisdiction(),
                status.lastUpdatedAt()
        );
    }    
}
