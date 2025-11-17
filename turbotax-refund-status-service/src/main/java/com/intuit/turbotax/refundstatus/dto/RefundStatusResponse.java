// package com.intuit.turbotax.refundstatus.dto;

// import java.math.BigDecimal;
// import java.time.Instant;
// import java.util.Collections;
// import java.util.List;

// import lombok.AllArgsConstructor;
// import lombok.Builder;
// import lombok.Data;
// import lombok.NoArgsConstructor;

// import com.intuit.turbotax.domainmodel.Jurisdiction;
// import com.intuit.turbotax.domainmodel.RefundCanonicalStatus;

// @Data
// @Builder
// @NoArgsConstructor
// @AllArgsConstructor
// public class RefundStatusResponse {
//     private Integer taxYear;
//     private Jurisdiction jurisdiction;
//     private BigDecimal amount;
//     private RefundCanonicalStatus status;
//     private Instant statusLastUpdatedAt;
//     private String statusMessageKey;
//     private EtaPredictionResponse etaPrediction;
    
//     public static RefundStatusResponse noFilingFound() {
//         return RefundStatusResponse.builder()
//                 .taxYear(null)
//                 .jurisdiction(null)
//                 .amount(BigDecimal.ZERO)
//                 .status(RefundCanonicalStatus.NO_FILING)
//                 .statusLastUpdatedAt(null)
//                 .statusMessageKey("refund.status.no_filing")
//                 .etaPrediction(null)
//                 .build();
//     }   
// }

