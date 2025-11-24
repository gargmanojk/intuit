package com.intuit.turbotax.refund.aggregation.job.processors;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.intuit.turbotax.api.v1.common.model.Jurisdiction;
import com.intuit.turbotax.api.v1.external.service.ExternalStateTaxClient;
import com.intuit.turbotax.api.v1.refund.model.RefundStatus;

import lombok.RequiredArgsConstructor;

/**
 * Processor for handling state tax refund status updates.
 */
@Component
@RequiredArgsConstructor
public class StateTaxStatusProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(StateTaxStatusProcessor.class);

    private final ExternalStateTaxClient stateTaxClient;

    /**
     * Fetches refund status from state tax service for a given filing ID and
     * jurisdiction.
     *
     * @param filingId     the filing ID
     * @param jurisdiction the state jurisdiction
     * @return Optional containing the refund status if available
     */
    public Optional<RefundStatus> fetchStatus(int filingId, Jurisdiction jurisdiction) {
        try {
            LOG.debug("Fetching state tax status for filingId={}, jurisdiction={}", filingId, jurisdiction);
            return stateTaxClient.getRefundStatus(
                    String.valueOf(filingId),
                    jurisdiction,
                    String.valueOf(filingId));
        } catch (Exception e) {
            LOG.error("Error fetching state tax status for filingId={}, jurisdiction={}: {}",
                    filingId, jurisdiction, e.getMessage());
            return Optional.empty();
        }
    }
}