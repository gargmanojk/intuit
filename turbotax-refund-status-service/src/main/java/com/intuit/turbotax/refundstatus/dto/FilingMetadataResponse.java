// package com.intuit.turbotax.refundstatus.dto;

// import java.math.BigDecimal;
// import java.time.LocalDate;

// import lombok.AllArgsConstructor;
// import lombok.Builder;
// import lombok.Data;
// import lombok.NoArgsConstructor;

// import com.fasterxml.jackson.annotation.JsonProperty;

// import com.intuit.turbotax.domainmodel.DisbursementMethod;
// import com.intuit.turbotax.domainmodel.Jurisdiction;

// /**
//  * Response containing filing metadata information including refund amounts and disbursement details.
//  * Generated from filing-metadata-response-schema.json
//  */
  
// @Data
// @Builder
// @NoArgsConstructor
// @AllArgsConstructor
// public class FilingMetadataResponse {
//     private String filingId;
//     private Jurisdiction jurisdiction;
//     private String userId;
//     private int taxYear;
//     private LocalDate filingDate;
//     private BigDecimal refundAmount;   
//     private String trackingId;  // tokenized
//     private DisbursementMethod disbursementMethod; // DIRECT_DEPOSIT, CARD, CHECK...
// }

