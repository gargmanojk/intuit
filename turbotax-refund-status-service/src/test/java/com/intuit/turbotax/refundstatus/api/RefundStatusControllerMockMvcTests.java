// package com.intuit.turbotax.refundstatus.api;

// import com.intuit.turbotax.refundstatus.domain.ai.AiRefundEtaService;
// import com.intuit.turbotax.refundstatus.domain.ai.RefundEtaPrediction;
// import com.intuit.turbotax.refundstatus.domain.filing.FilingMetadata;
// import com.intuit.turbotax.refundstatus.domain.filing.FilingMetadataService;
// import com.intuit.turbotax.refundstatus.domain.refund.Jurisdiction;
// import com.intuit.turbotax.refundstatus.domain.refund.RefundCanonicalStatus;
// import com.intuit.turbotax.refundstatus.domain.refund.RefundStatus;
// import com.intuit.turbotax.refundstatus.domain.refund.RefundStatusAggregatorService;
// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.boot.test.mock.mockito.MockBean;
// import org.springframework.test.web.servlet.MockMvc;

// import java.math.BigDecimal;
// import java.time.Instant;
// import java.time.LocalDate;
// import java.util.List;
// import java.util.Optional;

// import static org.mockito.Mockito.when;
// import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
// import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// // Full Spring context, but collaborators mocked.
// // This hits the real controller + orchestrator via HTTP.
// @SpringBootTest
// @AutoConfigureMockMvc
// class RefundStatusControllerMockMvcTests {

//     @Autowired
//     private MockMvc mockMvc;

//     @MockBean
//     private FilingMetadataService filingMetadataService;

//     @MockBean
//     private RefundStatusAggregatorService refundStatusAggregatorService;

//     @MockBean
//     private AiRefundEtaService aiRefundEtaService;

//     @Test
//     void getLatestRefundStatus_returnsExpectedJsonPayload() throws Exception {
//         // Arrange domain objects using Lombok builders
//         String userId = "user-123"; // In real system, from auth; here it's hardcoded in controller

//         FilingMetadata filing = FilingMetadata.builder()
//                 .filingId("filing-1")
//                 .userId(userId)
//                 .taxYear(2024)
//                 .federalRefundAmount(new BigDecimal("1500.00"))
//                 .stateRefundAmountTotal(BigDecimal.ZERO)
//                 .build();

//         when(filingMetadataService.findLatestFilingForUser(userId))
//                 .thenReturn(Optional.of(filing));

//         RefundStatus status = RefundStatus.builder()
//                 .statusId("status-1")
//                 .filingId("filing-1")
//                 .jurisdiction(Jurisdiction.FEDERAL)
//                 .canonicalStatus(RefundCanonicalStatus.PROCESSING)
//                 .statusLastUpdatedAt(Instant.parse("2025-03-01T10:15:30Z"))
//                 .amount(new BigDecimal("1500.00"))
//                 .build();

//         when(refundStatusAggregatorService.getRefundStatusesForFiling("filing-1"))
//                 .thenReturn(List.of(status));

//         RefundEtaPrediction prediction = RefundEtaPrediction.builder()
//                 .expectedArrivalDate(LocalDate.of(2025, 3, 15))
//                 .confidence(0.82)
//                 .windowDays(3)
//                 .explanationKey("IRS_EFILE_DIRECT_DEPOSIT_TYPICAL")
//                 .modelVersion("v1")
//                 .build();

//         when(aiRefundEtaService.predictEta(filing, status))
//                 .thenReturn(prediction);

//         // Act + Assert: hit the HTTP endpoint and assert JSON structure
//         mockMvc.perform(get("/refunds/latest-status"))
//                 .andExpect(status().isOk())
//                 .andExpect(content().contentType("application/json"))
//                 // root-level fields
//                 .andExpect(jsonPath("$.filingFound").value(true))
//                 .andExpect(jsonPath("$.taxYear").value(2024))
//                 // refunds[0] object
//                 .andExpect(jsonPath("$.refunds[0].jurisdiction").value("FEDERAL"))
//                 .andExpect(jsonPath("$.refunds[0].amount").value(1500.00))
//                 .andExpect(jsonPath("$.refunds[0].status").value("PROCESSING"))
//                 // etaPrediction nested object
//                 .andExpect(jsonPath("$.refunds[0].etaPrediction.expectedArrivalDate")
//                         .value("2025-03-15"))
//                 .andExpect(jsonPath("$.refunds[0].etaPrediction.confidence")
//                         .value(0.82));
//     }

//     @Test
//     void getLatestRefundStatus_whenNoFiling_returnsNoFilingJson() throws Exception {
//         String userId = "user-123";

//         when(filingMetadataService.findLatestFilingForUser(userId))
//                 .thenReturn(Optional.empty());

//         mockMvc.perform(get("/refunds/latest-status"))
//                 .andExpect(status().isOk())
//                 .andExpect(jsonPath("$.filingFound").value(false))
//                 .andExpect(jsonPath("$.refunds").isArray())
//                 .andExpect(jsonPath("$.refunds").isEmpty());
//     }
// }
