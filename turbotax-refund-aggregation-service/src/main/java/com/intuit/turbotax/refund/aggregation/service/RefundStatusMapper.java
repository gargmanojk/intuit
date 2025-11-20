package com.intuit.turbotax.refund.aggregation.service;

import org.springframework.stereotype.Component;

import com.intuit.turbotax.api.model.RefundStatusData;
import com.intuit.turbotax.refund.aggregation.repository.RefundStatusAggregate;

/**
 * Mapper class for converting between RefundStatusAggregate domain objects 
 * and RefundStatusData DTOs.
 */
@Component
public class RefundStatusMapper {

    /**
     * Converts a RefundStatusAggregate domain object to a RefundStatusData DTO.
     * 
     * @param filingId the filing ID
     * @param aggregate the domain aggregate object
     * @return the converted DTO or null if aggregate is null
     */
    public RefundStatusData aggregateToDto(int filingId, RefundStatusAggregate aggregate) {
        if (aggregate == null) {
            return null;
        }
        
        return new RefundStatusData(
                filingId,
                aggregate.status(),
                aggregate.jurisdiction(),
                aggregate.lastUpdatedAt()
        );
    }
}