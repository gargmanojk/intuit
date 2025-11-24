package com.intuit.turbotax.refund.aggregation.job.processors;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.intuit.turbotax.api.v1.external.service.ExternalIrsClient;
import com.intuit.turbotax.api.v1.refund.model.RefundStatus;

import lombok.RequiredArgsConstructor;

/**
 * Processor for handling IRS refund status updates.
 */
@Component
@RequiredArgsConstructor
public class IrsStatusProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(IrsStatusProcessor.class);

    private final ExternalIrsClient irsClient;

    /**
     * Fetches refund status from IRS for a given filing ID.
     *
     * @param filingId the filing ID
     * @return Optional containing the refund status if available
     */
    public Optional<RefundStatus> fetchStatus(int filingId) {
        try {
            LOG.debug("Fetching IRS status for filingId={}", filingId);
            // Using a placeholder SSN for demo purposes
            return irsClient.getRefundStatus(filingId, "***-**-1234");
        } catch (Exception e) {
            LOG.error("Error fetching IRS status for filingId={}: {}", filingId, e.getMessage());
            return Optional.empty();
        }
    }
}