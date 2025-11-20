package com.intuit.turbotax.client;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.List;
import java.math.BigDecimal;

import com.intuit.turbotax.api.model.RefundStatus;

/**
 * Client interface for interacting with IRS (Internal Revenue Service) APIs
 * to retrieve federal tax refund status and processing information.
 */
public interface ExternalIrsClient {
    
    /**
     * Retrieves the current refund status from IRS systems
     * @param filingId the unique filing identifier
     * @param ssn the taxpayer's Social Security Number (masked for logging)
     * @return IRS refund status information
     */
    Optional<IrsRefundStatusResponse> getRefundStatus(int filingId, String ssn);
    
    /**
     * Gets estimated processing timeline from IRS
     * @param filingId the unique filing identifier
     * @return estimated processing information
     */
    Optional<IrsProcessingTimelineResponse> getProcessingTimeline(String filingId);
    
    /**
     * Checks if the return requires additional verification
     * @param filingId the unique filing identifier
     * @return verification requirements
     */
    IrsVerificationResponse checkVerificationRequirements(String filingId);
    
    /**
     * Gets detailed processing status including any holds or issues
     * @param filingId the unique filing identifier
     * @return detailed processing status
     */
    List<IrsProcessingStatusDetail> getDetailedProcessingStatus(String filingId);
    
    // Response DTOs
    record IrsRefundStatusResponse(
        String filingId,
        RefundStatus status,
        String statusCode,
        String statusDescription,
        BigDecimal refundAmount,
        LocalDate expectedDate,
        LocalDateTime lastUpdated,
        boolean hasIssues,
        String trackingNumber
    ) {}
    
    record IrsProcessingTimelineResponse(
        String filingId,
        LocalDate receivedDate,
        LocalDate expectedCompletionDate,
        int estimatedProcessingDays,
        String processingStage,
        double completionPercentage
    ) {}
    
    record IrsVerificationResponse(
        String filingId,
        boolean identityVerificationRequired,
        boolean documentVerificationRequired,
        List<String> requiredDocuments,
        String verificationMethod,
        LocalDate verificationDeadline
    ) {}
    
    record IrsProcessingStatusDetail(
        String filingId,
        String statusCode,
        String description,
        LocalDateTime timestamp,
        String processingCenter,
        boolean isBlockingIssue,
        String resolutionSteps
    ) {}
}