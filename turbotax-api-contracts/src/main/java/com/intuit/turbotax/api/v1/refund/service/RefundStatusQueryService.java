package com.intuit.turbotax.api.v1.refund.service;

import java.util.List;

import com.intuit.turbotax.api.v1.refund.model.RefundSummary;

/**
 * Service interface for querying the latest refund status summaries for a user.
 * Provides a method to retrieve all refund summaries for a given user.
 */
public interface RefundStatusQueryService {

    /**
     * Retrieves the latest refund status summaries for a given user.
     *
     * @param userId the user identifier
     * @return list of refund summaries for the user (may be empty)
     */
    List<RefundSummary> getLatestRefundStatus(String userId);
}