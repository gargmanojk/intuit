package com.intuit.turbotax.refund.aggregation.client;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Arrays;
import java.math.BigDecimal;
import java.util.Set;
import java.util.HashSet;

import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

@Component
class ExternalStateTaxClientImpl implements ExternalStateTaxClient {
    
    private static final Logger log = LoggerFactory.getLogger(ExternalStateTaxClientImpl.class);
    
    private final Map<String, StateRefundStatusResponse> mockStateRefundData = new HashMap<>();
    private final Map<Jurisdiction, StateProcessingTimelineResponse> mockTimelineData = new HashMap<>();
    private final Set<Jurisdiction> supportedJurisdictions = new HashSet<>();
    
    public ExternalStateTaxClientImpl() {
        initializeMockData();
    }
    
    @Override
    public Optional<StateRefundStatusResponse> getStateRefundStatus(String filingId, Jurisdiction jurisdiction, String stateFilingId) {
        log.info("Retrieving state refund status for filing: {}, jurisdiction: {}, state filing ID: {}", 
                filingId, jurisdiction, stateFilingId);
        
        if (!supportedJurisdictions.contains(jurisdiction)) {
            log.warn("Jurisdiction {} not supported for electronic lookup", jurisdiction);
            return Optional.empty();
        }
        
        simulateNetworkDelay(300, 1200);
        
        String key = filingId + "_" + jurisdiction.name();
        StateRefundStatusResponse response = mockStateRefundData.get(key);
        if (response == null) {
            response = generateDynamicStateRefundStatus(filingId, jurisdiction, stateFilingId);
        }
        
        return Optional.ofNullable(response);
    }
    
    @Override
    public StateProcessingTimelineResponse getProcessingTimeline(Jurisdiction jurisdiction, LocalDate filingDate) {
        log.info("Retrieving processing timeline for jurisdiction: {}, filing date: {}", jurisdiction, filingDate);
        
        simulateNetworkDelay(200, 800);
        
        StateProcessingTimelineResponse response = mockTimelineData.get(jurisdiction);
        if (response == null) {
            response = generateDynamicTimeline(jurisdiction, filingDate);
        }
        
        return response;
    }
    
    @Override
    public StateDocumentationResponse checkDocumentationRequirements(String filingId, Jurisdiction jurisdiction) {
        log.info("Checking documentation requirements for filing: {}, jurisdiction: {}", filingId, jurisdiction);
        
        simulateNetworkDelay(150, 600);
        
        return generateDocumentationResponse(filingId, jurisdiction);
    }
    
    @Override
    public List<StateRefundHold> getRefundHolds(String filingId, Jurisdiction jurisdiction) {
        log.info("Retrieving refund holds for filing: {}, jurisdiction: {}", filingId, jurisdiction);
        
        simulateNetworkDelay(250, 900);
        
        return generateRefundHolds(filingId, jurisdiction);
    }
    
    @Override
    public Set<Jurisdiction> getSupportedJurisdictions() {
        return new HashSet<>(supportedJurisdictions);
    }
    
    @Override
    public boolean supportsElectronicLookup(Jurisdiction jurisdiction) {
        return supportedJurisdictions.contains(jurisdiction);
    }
    
    private void initializeMockData() {
        // Initialize supported jurisdictions (simplified - use available enum values)
        supportedJurisdictions.addAll(Arrays.asList(
            Jurisdiction.STATE_CA,
            Jurisdiction.STATE_NY,
            Jurisdiction.STATE_NJ
        ));
        
        // Sample state refund data
        mockStateRefundData.put("FILING_001_STATE_CA", new StateRefundStatusResponse(
            "FILING_001",
            Jurisdiction.STATE_CA,
            RefundStatus.PROCESSING,
            "CA_PROC_001",
            "California return is being processed",
            new BigDecimal("450.00"),
            LocalDate.now().plusDays(10),
            LocalDateTime.now().minusHours(4),
            "CA_TRK_789012345",
            false,
            null
        ));
        
        // Sample timeline data
        mockTimelineData.put(Jurisdiction.STATE_CA, new StateProcessingTimelineResponse(
            Jurisdiction.STATE_CA,
            14,
            3,
            LocalDate.now().plusDays(17),
            "NORMAL_PROCESSING",
            Map.of(
                "website", "https://webapp.ftb.ca.gov/RefundInquiry/",
                "phone", "1-800-338-0505",
                "hours", "Monday-Friday 7am-7pm PST"
            )
        ));
        
        mockTimelineData.put(Jurisdiction.STATE_NY, new StateProcessingTimelineResponse(
            Jurisdiction.STATE_NY,
            21,
            5,
            LocalDate.now().plusDays(26),
            "ELEVATED_PROCESSING_TIME",
            Map.of(
                "website", "https://www.tax.ny.gov/pit/file/refund.htm",
                "phone", "1-518-457-5149",
                "hours", "Monday-Friday 8am-4:30pm EST"
            )
        ));
    }
    
    private StateRefundStatusResponse generateDynamicStateRefundStatus(String filingId, Jurisdiction jurisdiction, String stateFilingId) {
        RefundStatus status = determineStatusFromFilingId(filingId, jurisdiction);
        BigDecimal amount = generateStateRefundAmount(filingId, jurisdiction);
        LocalDate expectedDate = calculateStateExpectedDate(jurisdiction, status);
        
        return new StateRefundStatusResponse(
            filingId,
            jurisdiction,
            status,
            jurisdiction.name().substring(0, 2) + "_" + status.name() + "_001",
            getStateStatusDescription(status, jurisdiction),
            amount,
            expectedDate,
            LocalDateTime.now().minusHours(Math.abs(filingId.hashCode()) % 24),
            generateStateTrackingNumber(jurisdiction, filingId),
            status == RefundStatus.DELAYED,
            status == RefundStatus.DELAYED ? "Additional documentation may be required" : null
        );
    }
    
    private StateProcessingTimelineResponse generateDynamicTimeline(Jurisdiction jurisdiction, LocalDate filingDate) {
        int baseDays = getStateBaseProcessingDays(jurisdiction);
        int backlogDays = Math.abs(jurisdiction.hashCode()) % 7;
        
        return new StateProcessingTimelineResponse(
            jurisdiction,
            baseDays,
            backlogDays,
            LocalDate.now().plusDays(baseDays + backlogDays),
            getProcessingStatus(backlogDays),
            getJurisdictionInfo(jurisdiction)
        );
    }
    
    private StateDocumentationResponse generateDocumentationResponse(String filingId, Jurisdiction jurisdiction) {
        boolean needsDocs = Math.abs((filingId + jurisdiction.name()).hashCode()) % 10 < 1; // 10% need docs
        
        return new StateDocumentationResponse(
            filingId,
            jurisdiction,
            needsDocs,
            needsDocs ? Arrays.asList("W-2 Forms", "1099 Forms", "State ID Copy") : Arrays.asList(),
            needsDocs ? LocalDate.now().plusDays(21) : null,
            needsDocs ? "ONLINE_UPLOAD" : null,
            needsDocs ? getJurisdictionContactInfo(jurisdiction) : null
        );
    }
    
    private List<StateRefundHold> generateRefundHolds(String filingId, Jurisdiction jurisdiction) {
        boolean hasHolds = Math.abs((filingId + jurisdiction.name()).hashCode()) % 15 < 1; // ~7% have holds
        
        if (!hasHolds) {
            return Arrays.asList();
        }
        
        return Arrays.asList(
            new StateRefundHold(
                "HOLD_" + Math.abs(filingId.hashCode()),
                "INCOME_VERIFICATION",
                "Income verification hold - additional documentation required",
                LocalDate.now().minusDays(5),
                LocalDate.now().plusDays(15),
                "Submit requested documentation through state portal",
                true
            )
        );
    }
    
    private RefundStatus determineStatusFromFilingId(String filingId, Jurisdiction jurisdiction) {
        int hash = Math.abs((filingId + jurisdiction.name()).hashCode()) % 10;
        return switch (hash) {
            case 0, 1, 2, 3, 4, 5 -> RefundStatus.PROCESSING;
            case 6, 7 -> RefundStatus.SENT_TO_BANK;
            case 8 -> RefundStatus.DELAYED;
            case 9 -> RefundStatus.ERROR;
            default -> RefundStatus.PROCESSING;
        };
    }
    
    private BigDecimal generateStateRefundAmount(String filingId, Jurisdiction jurisdiction) {
        // State refunds typically smaller than federal
        int baseAmount = 100 + Math.abs((filingId + jurisdiction.name()).hashCode()) % 800;
        return new BigDecimal(String.valueOf(baseAmount));
    }
    
    private LocalDate calculateStateExpectedDate(Jurisdiction jurisdiction, RefundStatus status) {
        int baseDays = getStateBaseProcessingDays(jurisdiction);
        if (status == RefundStatus.DELAYED) baseDays += 14;
        return LocalDate.now().plusDays(baseDays);
    }
    
    private String generateStateTrackingNumber(Jurisdiction jurisdiction, String filingId) {
        return jurisdiction.name().substring(0, 2) + "_TRK_" + Math.abs(filingId.hashCode());
    }
    
    private String getStateStatusDescription(RefundStatus status, Jurisdiction jurisdiction) {
        return switch (status) {
            case PROCESSING -> jurisdiction.name() + " return is being processed";
            case SENT_TO_BANK -> jurisdiction.name() + " refund has been approved";
            case DELAYED -> jurisdiction.name() + " return is under review";
            case ERROR -> jurisdiction.name() + " return has been rejected";
            case ACCEPTED -> jurisdiction.name() + " return has been accepted";
            case DEPOSITED -> jurisdiction.name() + " refund has been deposited";
            case FILED -> jurisdiction.name() + " return has been filed";
            case NO_FILING -> "No filing found for " + jurisdiction.name();
        };
    }
    
    private int getStateBaseProcessingDays(Jurisdiction jurisdiction) {
        // Different states have different processing times
        return switch (jurisdiction) {
            case STATE_CA, STATE_NY -> 14;
            case STATE_NJ -> 18;
            case FEDERAL -> 10; // Not applicable but included for completeness
            default -> 15;
        };
    }
    
    private String getProcessingStatus(int backlogDays) {
        if (backlogDays <= 2) return "NORMAL_PROCESSING";
        if (backlogDays <= 5) return "ELEVATED_PROCESSING_TIME";
        return "SIGNIFICANT_DELAYS";
    }
    
    private Map<String, String> getJurisdictionInfo(Jurisdiction jurisdiction) {
        return switch (jurisdiction) {
            case STATE_CA -> Map.of(
                "website", "https://webapp.ftb.ca.gov/RefundInquiry/",
                "phone", "1-800-338-0505",
                "hours", "Monday-Friday 7am-7pm PST"
            );
            case STATE_NY -> Map.of(
                "website", "https://www.tax.ny.gov/pit/file/refund.htm",
                "phone", "1-518-457-5149",
                "hours", "Monday-Friday 8am-4:30pm EST"
            );
            case STATE_NJ -> Map.of(
                "website", "https://www.state.nj.us/treasury/taxation/",
                "phone", "1-609-292-6400",
                "hours", "Monday-Friday 8:30am-4:30pm EST"
            );
            default -> Map.of(
                "website", "Contact state tax authority",
                "phone", "Contact state tax authority"
            );
        };
    }
    
    private String getJurisdictionContactInfo(Jurisdiction jurisdiction) {
        Map<String, String> info = getJurisdictionInfo(jurisdiction);
        return "Website: " + info.get("website") + ", Phone: " + info.get("phone");
    }
    
    private void simulateNetworkDelay(int minMs, int maxMs) {
        try {
            int delay = minMs + (int) (Math.random() * (maxMs - minMs));
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("Network simulation interrupted", e);
        }
    }
}
