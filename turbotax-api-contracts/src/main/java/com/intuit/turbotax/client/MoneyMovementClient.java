package com.intuit.turbotax.client;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.List;
import java.util.Map;
import java.math.BigDecimal;

/**
 * Client interface for interacting with money movement and banking systems
 * to track refund disbursements, validate account information, and monitor transfer status.
 */
public interface MoneyMovementClient {
    
    /**
     * Tracks the status of a refund disbursement
     * @param refundId the unique refund identifier
     * @param disbursementMethod the method of disbursement (direct deposit, check, etc.)
     * @return disbursement tracking information
     */
    Optional<DisbursementTrackingResponse> trackDisbursement(String refundId, DisbursementMethod disbursementMethod);
    
    /**
     * Validates bank account information for direct deposits
     * @param routingNumber the bank routing number
     * @param accountNumber the bank account number (encrypted/masked)
     * @param accountType the type of account (checking, savings)
     * @return validation results
     */
    BankAccountValidationResponse validateBankAccount(String routingNumber, String accountNumber, AccountType accountType);
    
    /**
     * Gets estimated delivery time for different disbursement methods
     * @param disbursementMethod the disbursement method
     * @param amount the refund amount
     * @param zipCode the delivery zip code for checks
     * @return estimated delivery information
     */
    DisbursementTimelineResponse getEstimatedDeliveryTime(DisbursementMethod disbursementMethod, BigDecimal amount, String zipCode);
    
    /**
     * Checks for any holds or restrictions on money movement
     * @param refundId the unique refund identifier
     * @param taxpayerId the taxpayer identifier
     * @return hold information if any
     */
    List<MoneyMovementHold> checkMoneyMovementHolds(String refundId, String taxpayerId);
    
    /**
     * Gets supported disbursement methods for a given amount and location
     * @param amount the refund amount
     * @param zipCode the taxpayer's zip code
     * @return list of available disbursement methods
     */
    List<DisbursementMethod> getSupportedDisbursementMethods(BigDecimal amount, String zipCode);
    
    /**
     * Retrieves fee information for different disbursement methods
     * @param disbursementMethod the disbursement method
     * @param amount the refund amount
     * @return fee information
     */
    DisbursementFeeResponse getDisbursementFees(DisbursementMethod disbursementMethod, BigDecimal amount);
    
    /**
     * Checks the status of a returned refund (bounced direct deposit, undelivered check)
     * @param refundId the unique refund identifier
     * @return return status information
     */
    Optional<RefundReturnResponse> checkRefundReturn(String refundId);
    
    // Enums
    enum DisbursementMethod {
        DIRECT_DEPOSIT,
        PAPER_CHECK,
        PREPAID_DEBIT_CARD,
        ELECTRONIC_CHECK,
        WIRE_TRANSFER
    }
    
    enum AccountType {
        CHECKING,
        SAVINGS,
        PREPAID
    }
    
    enum DisbursementStatus {
        PENDING,
        IN_TRANSIT,
        DELIVERED,
        RETURNED,
        CANCELLED,
        FAILED
    }
    
    // Response DTOs
    record DisbursementTrackingResponse(
        String refundId,
        DisbursementMethod method,
        DisbursementStatus status,
        BigDecimal amount,
        LocalDateTime initiatedAt,
        LocalDateTime estimatedDelivery,
        LocalDateTime actualDelivery,
        String trackingNumber,
        String bankName,
        String accountMask,
        List<DisbursementStatusUpdate> statusHistory
    ) {}
    
    record DisbursementStatusUpdate(
        DisbursementStatus status,
        LocalDateTime timestamp,
        String description,
        String location
    ) {}
    
    record BankAccountValidationResponse(
        boolean isValid,
        String routingNumber,
        String bankName,
        String bankAddress,
        AccountType supportedAccountType,
        boolean supportsDirectDeposit,
        List<String> validationErrors,
        String validationId
    ) {}
    
    record DisbursementTimelineResponse(
        DisbursementMethod method,
        int estimatedBusinessDays,
        LocalDate estimatedDeliveryDate,
        boolean expeditedAvailable,
        BigDecimal expeditedFee,
        Map<String, String> deliveryDetails
    ) {}
    
    record MoneyMovementHold(
        String holdId,
        String holdType,
        String reason,
        LocalDate holdDate,
        LocalDate estimatedReleaseDate,
        boolean requiresTaxpayerAction,
        String resolutionInstructions,
        String contactInfo
    ) {}
    
    record DisbursementFeeResponse(
        DisbursementMethod method,
        BigDecimal standardFee,
        BigDecimal expeditedFee,
        String feeDescription,
        boolean feeWaived,
        String waiverReason
    ) {}
    
    record RefundReturnResponse(
        String refundId,
        boolean wasReturned,
        LocalDate returnDate,
        String returnReason,
        DisbursementMethod originalMethod,
        String returnCode,
        boolean requiresNewDisbursementMethod,
        List<DisbursementMethod> alternativeMethods
    ) {}
}