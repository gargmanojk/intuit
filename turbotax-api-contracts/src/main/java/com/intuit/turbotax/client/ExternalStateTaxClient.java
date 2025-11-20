package com.intuit.turbotax.client;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.math.BigDecimal;

import com.intuit.turbotax.api.model.RefundStatus;
import com.intuit.turbotax.api.model.Jurisdiction;

/**
 * Client interface for interacting with various state tax authority APIs
 * to retrieve state tax refund status and processing information.
 */
public interface ExternalStateTaxClient {
    
    /**
     * Retrieves state refund status for a specific jurisdiction
     */
    Optional<StateRefundStatusResponse> getStateRefundStatus(String filingId, Jurisdiction jurisdiction, String stateFilingId);
    
    /**
     * Gets state-specific processing timelines
     */
    StateProcessingTimelineResponse getProcessingTimeline(Jurisdiction jurisdiction, LocalDate filingDate);
    
    /**
     * Checks if the state requires additional documentation
     */
    StateDocumentationResponse checkDocumentationRequirements(String filingId, Jurisdiction jurisdiction);
    
    /**
     * Gets state-specific refund hold information
     */
    List<StateRefundHold> getRefundHolds(String filingId, Jurisdiction jurisdiction);
    
    /**
     * Gets list of supported state jurisdictions
     */
    Set<Jurisdiction> getSupportedJurisdictions();
    
    /**
     * Checks if a specific state supports electronic refund status lookup
     */
    boolean supportsElectronicLookup(Jurisdiction jurisdiction);
    
    // Response DTOs
    record StateRefundStatusResponse(
        String filingId,
        Jurisdiction jurisdiction,
        RefundStatus status,
        String stateStatusCode,
        String statusDescription,
        BigDecimal refundAmount,
        LocalDate expectedDate,
        LocalDateTime lastUpdated,
        String stateTrackingNumber,
        boolean requiresAction,
        String actionRequired
    ) {}
    
    record StateProcessingTimelineResponse(
        Jurisdiction jurisdiction,
        int typicalProcessingDays,
        int currentBacklogDays,
        LocalDate estimatedCompletionDate,
        String processingStatus,
        Map<String, String> jurisdictionSpecificInfo
    ) {}
    
    record StateDocumentationResponse(
        String filingId,
        Jurisdiction jurisdiction,
        boolean requiresAdditionalDocuments,
        List<String> requiredDocuments,
        LocalDate submissionDeadline,
        String submissionMethod,
        String contactInfo
    ) {}
    
    record StateRefundHold(
        String holdId,
        String holdType,
        String description,
        LocalDate holdDate,
        LocalDate estimatedReleaseDate,
        String resolutionSteps,
        boolean taxpayerActionRequired
    ) {}
}