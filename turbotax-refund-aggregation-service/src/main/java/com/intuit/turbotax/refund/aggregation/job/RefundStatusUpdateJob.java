package com.intuit.turbotax.refund.aggregation.job;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.intuit.turbotax.api.model.RefundStatus;
import com.intuit.turbotax.refund.aggregation.client.ExternalIrsClient;
import com.intuit.turbotax.refund.aggregation.client.ExternalStateTaxClient;
import com.intuit.turbotax.refund.aggregation.client.MoneyMovementClient;
import com.intuit.turbotax.refund.aggregation.client.MoneyMovementClient.DisbursementMethod;
import com.intuit.turbotax.refund.aggregation.repository.RefundStatusAggregate;
import com.intuit.turbotax.refund.aggregation.repository.RefundStatusRepository;

/**
 * Scheduled job that periodically fetches refund status updates from external services
 * and updates the local repository with the latest information.
 */
@Service
public class RefundStatusUpdateJob {
    
    private static final Logger LOG = LoggerFactory.getLogger(RefundStatusUpdateJob.class);
    
    private final RefundStatusRepository repository;
    private final ExternalIrsClient irsClient;
    private final ExternalStateTaxClient stateTaxClient;
    private final MoneyMovementClient moneyMovementClient;
    
    public RefundStatusUpdateJob(RefundStatusRepository repository,
                                 ExternalIrsClient irsClient,
                                 ExternalStateTaxClient stateTaxClient,
                                 MoneyMovementClient moneyMovementClient) {
        this.repository = repository;
        this.irsClient = irsClient;
        this.stateTaxClient = stateTaxClient;
        this.moneyMovementClient = moneyMovementClient;
    }
    
    /**
     * Runs every 30 minutes to fetch updated refund status from external services.
     * Updates local repository with the latest information.
     */
    @Scheduled(fixedRate = 30 * 60 * 1000) // 30 minutes in milliseconds
    public void updateRefundStatuses() {
        LOG.info("Starting scheduled refund status update job");
        
        try {
            // Get all active filing IDs that need status updates
            List<Integer> activeFilingIds = getActiveFilingIds();
            
            LOG.info("Processing {} active filings for status updates", activeFilingIds.size());
            
            for (Integer filingId : activeFilingIds) {
                updateFilingStatus(filingId);
            }
            
            LOG.info("Completed refund status update job successfully");
            
        } catch (Exception e) {
            LOG.error("Error during scheduled refund status update: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Updates the status for a specific filing by contacting external services.
     */
    private void updateFilingStatus(int filingId) {
        LOG.debug("Updating status for filingId={}", filingId);
        
        try {
            // Get current status from repository
            Optional<RefundStatusAggregate> currentStatus = repository.findByFilingId(filingId);
            if (currentStatus.isEmpty()) {
                LOG.debug("No existing status found for filingId={}, skipping", filingId);
                return;
            }
            
            RefundStatusAggregate current = currentStatus.get();
            
            // Skip if already in final status
            if (current.status().isFinal()) {
                LOG.debug("FilingId={} is already in final status {}, skipping", filingId, current.status());
                return;
            }
            
            // Fetch updated status from appropriate external service
            RefundStatus updatedStatus = fetchUpdatedStatus(filingId, current);
            
            if (updatedStatus != null && updatedStatus != current.status()) {
                LOG.info("Status changed for filingId={}: {} -> {}", filingId, current.status(), updatedStatus);
                
                // Create updated aggregate for processing
                RefundStatusAggregate updatedAggregate = new RefundStatusAggregate(
                    current.filingId(),
                    current.trackingId(),
                    current.jurisdiction(),
                    updatedStatus,
                    current.rawStatusCode(),
                    generateStatusMessage(updatedStatus),
                    Instant.now(), // Update timestamp
                    current.amount()
                );
                
                // Note: Repository update would happen here in a real implementation
                LOG.info("Would save updated status for filingId={} to repository", filingId);
                
                // If status is now SENT_TO_BANK, also check deposit status
                if (updatedStatus == RefundStatus.SENT_TO_BANK) {
                    checkDepositStatus(filingId, updatedAggregate);
                }
            }
            
        } catch (Exception e) {
            LOG.error("Error updating status for filingId={}: {}", filingId, e.getMessage(), e);
        }
    }
    
    /**
     * Fetches updated status from the appropriate external service based on jurisdiction.
     */
    private RefundStatus fetchUpdatedStatus(int filingId, RefundStatusAggregate current) {
        try {
            String trackingId = String.valueOf(filingId);
            
            switch (current.jurisdiction()) {
                case FEDERAL:
                    LOG.debug("Fetching federal status from IRS for filingId={}", filingId);
                    // Use correct method signature (int filingId, String ssn)
                    var irsResponse = irsClient.getRefundStatus(filingId, "***-**-" + String.valueOf(filingId).substring(4));
                    return irsResponse.map(response -> response.status()).orElse(null);
                    
                case STATE_CA:
                case STATE_NY:
                case STATE_NJ: // Use available jurisdiction
                    LOG.debug("Fetching state status for jurisdiction={}, filingId={}", current.jurisdiction(), filingId);
                    var stateResponse = stateTaxClient.getStateRefundStatus(trackingId, current.jurisdiction(), trackingId);
                    return stateResponse.map(response -> response.status()).orElse(null);
                    
                default:
                    LOG.warn("Unknown jurisdiction {} for filingId={}", current.jurisdiction(), filingId);
                    return null;
            }
        } catch (Exception e) {
            LOG.error("Error fetching status from external service for filingId={}: {}", filingId, e.getMessage());
            return null;
        }
    }
    
    /**
     * Checks deposit status for refunds that have been sent.
     */
    private void checkDepositStatus(int filingId, RefundStatusAggregate current) {
        try {
            LOG.debug("Checking deposit status for filingId={}", filingId);
            
            // Use available trackDisbursement method from MoneyMovementClient
            var disbursementResponse = moneyMovementClient.trackDisbursement(
                current.trackingId(), 
                DisbursementMethod.DIRECT_DEPOSIT
            );
            
            if (disbursementResponse.isPresent()) {
                LOG.info("Disbursement status for filingId={}: {}", filingId, disbursementResponse.get());
            } else {
                LOG.debug("No disbursement info available for filingId={}", filingId);
            }
            
        } catch (Exception e) {
            LOG.error("Error checking disbursement status for filingId={}: {}", filingId, e.getMessage());
        }
    }
    
    /**
     * Returns a list of active filing IDs that need status updates.
     * In a real implementation, this would query the database for non-final statuses.
     */
    private List<Integer> getActiveFilingIds() {
        // Mock implementation - return some sample filing IDs
        return List.of(
            202410001, // Federal filing
            202410002, // State filing
            202410003, // Federal filing
            202410004  // State filing
        );
    }
    
    /**
     * Generates a user-friendly status message based on the refund status.
     */
    private String generateStatusMessage(RefundStatus status) {
        return switch (status) {
            case FILED -> "Your tax return has been filed and is being processed";
            case ACCEPTED -> "Your tax return has been accepted";
            case PROCESSING -> "Your refund is being processed";
            case SENT_TO_BANK -> "Your refund has been sent to the bank";
            case DEPOSITED -> "Your refund has been deposited to your account";
            case ERROR -> "There was an error processing your refund. Please contact support";
            default -> "Refund status: " + status.toString();
        };
    }
}