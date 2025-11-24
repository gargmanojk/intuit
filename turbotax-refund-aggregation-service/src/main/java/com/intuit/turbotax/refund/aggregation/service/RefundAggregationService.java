package com.intuit.turbotax.refund.aggregation.service;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.intuit.turbotax.api.v1.refund.model.RefundStatusData;
import com.intuit.turbotax.refund.aggregation.mapper.RefundStatusMapper;
import com.intuit.turbotax.refund.aggregation.repository.RefundStatusAggregate;
import com.intuit.turbotax.refund.aggregation.repository.RefundStatusRepository;
import com.intuit.turbotax.refund.aggregation.validation.RefundAggregationValidator;

import lombok.RequiredArgsConstructor;

/**
 * Service class for refund aggregation business logic.
 * Handles the core logic for retrieving and processing refund status data.
 */
@Service
@RequiredArgsConstructor
public class RefundAggregationService {

    private static final Logger LOG = LoggerFactory.getLogger(RefundAggregationService.class);

    private final RefundStatusRepository repository;
    private final RefundStatusMapper mapper;
    private final RefundAggregationValidator validator;

    /**
     * Retrieves refund status data for a specific filing ID.
     * This method is cached to improve performance for repeated requests.
     *
     * @param filingId the unique filing identifier
     * @return Optional containing refund status data if found
     */
    @Cacheable(value = "refundStatus", key = "#filingId")
    public Optional<RefundStatusData> getRefundStatusForFiling(int filingId) {
        LOG.debug("Getting refund status for filingId={}", filingId);

        validator.validateFilingId(filingId);

        Optional<RefundStatusAggregate> status = repository.findByFilingId(filingId);
        if (status.isEmpty()) {
            LOG.debug("No refund status found for filingId={}", filingId);
            return Optional.empty();
        }

        LOG.debug("Found refund status for filingId={}", filingId);
        RefundStatusData resultData = mapper.mapToApi(filingId, status.get());
        return Optional.of(resultData);
    }
}