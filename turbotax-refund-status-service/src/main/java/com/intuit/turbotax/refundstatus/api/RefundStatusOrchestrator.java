// package com.intuit.turbotax.refundstatus.api;

// import com.intuit.turbotax.domainmodel.dto.FilingMetadataDto;
// import com.intuit.turbotax.refundstatus.domain.filing.FilingMetadata;
// import com.intuit.turbotax.refundstatus.domain.refund.RefundStatus;
// import com.intuit.turbotax.refundstatus.integration.RefundStatusAggregatorService;
// import com.intuit.turbotax.domainmodel.Jurisdiction;
// import com.intuit.turbotax.domainmodel.RefundCanonicalStatus;
// import com.intuit.turbotax.domainmodel.dto.RefundStatusDto;
// import com.intuit.turbotax.domainmodel.dto.RefundDetailsDto;
// import com.intuit.turbotax.domainmodel.dto.EtaPredictionDto;
// import com.intuit.turbotax.domainmodel.dto.RefundStatusAggregatorDto;
// import com.intuit.turbotax.refundstatus.integration.AiRefundEtaService;
// import com.intuit.turbotax.domainmodel.dto.RefundEtaRequest;
// import com.intuit.turbotax.domainmodel.dto.RefundEtaDto;
// import com.intuit.turbotax.refundstatus.integration.FilingMetadataService;


// import org.springframework.stereotype.Service;

// import java.util.List;
// import java.util.Optional;
// import java.util.ArrayList;

// @Service
// public class RefundStatusOrchestrator {

//     private final FilingMetadataService filingMetadataService;
//     private final RefundStatusAggregatorService refundStatusAggregatorService;
//     private final AiRefundEtaService aiRefundEtaService;

//     public RefundStatusOrchestrator(FilingMetadataService filingMetadataService,
//                                     RefundStatusAggregatorService refundStatusAggregatorService,
//                                     AiRefundEtaService aiRefundEtaService) {
//         this.filingMetadataService = filingMetadataService;
//         this.refundStatusAggregatorService = refundStatusAggregatorService;
//         this.aiRefundEtaService = aiRefundEtaService;
//     }

//     /**
//      * Convert FilingMetadataDto (DTO) to FilingMetadata (domain object).
//      */
//     private FilingMetadata dtoToDomain(FilingMetadataDto dto) {
//         FilingMetadata domain = new FilingMetadata();
//         domain.setFilingId(dto.getFilingId());  
//         domain.setUserId(dto.getUserId());
//         domain.setTaxYear(dto.getTaxYear());
//         domain.setFilingDate(dto.getFilingDate());
//         domain.setRefundAmount(dto.getRefundAmount());
//         domain.setDisbursementMethod(dto.getDisbursementMethod());
//         domain.setJurisdiction(dto.getJurisdiction());

//         return domain;
//     }

//     public RefundStatusDto getLatestRefundStatus(String userId) {
//         // 1. Find latest filing for this user
//         List<FilingMetadataDto> filings = filingMetadataService.findLatestFilingForUser(userId);

//         if (filings.isEmpty()) {
//             // No filing found – user hasn't filed or data not available
//             return RefundStatusDto.noFilingFound();
//         }

//         for (FilingMetadataDto filingMetadataDto : filings) {
            
//         }

//         FilingMetadataDto filingDto = filings.get(0);  
//         FilingMetadata filing = dtoToDomain(filingDto);

//         // 2. Fetch refund statuses across jurisdictions (federal + states)
//         Optional<RefundStatusAggregatorDto> aggResp = refundStatusAggregatorService.getRefundStatusesForFiling(filing.getFilingId());
        
//         if (aggResp.isEmpty()) {
//             return RefundStatusDto.noFilingFound();
//         }

//         // Convert aggregator response to domain RefundStatus objects
//         List<RefundStatus> statuses = aggregatorResponseToStatuses(aggResp.get());
        
//         // 3. For each non-final status, ask AI for ETA
//         List<RefundDetailsDto> refundDetails = statuses.stream()
//                 .map(status -> {
//                     EtaPredictionDto etaDto = null;

//                     if (!status.getCanonicalStatus().isFinal()) {
//                         RefundEtaRequest req = RefundEtaRequest.builder()
//                                 .jurisdiction(status.getJurisdiction())
//                                 .taxYear(filing.getTaxYear())
//                                 .filingDate(filing.getFilingDate())
//                                 .refundAmount(filing.getRefundAmount())
//                                 .disbursementMethod(filing.getDisbursementMethod())
//                                 .returnStatus(status.getCanonicalStatus())
//                                 .build();

//                         java.util.Optional<RefundEtaDto> respOpt = aiRefundEtaService.predictEta(req);
//                         if (respOpt.isPresent()) {
//                             RefundEtaDto resp = respOpt.get();
//                             if (status.getJurisdiction() == Jurisdiction.FEDERAL) {
//                                 etaDto = EtaPredictionDto.builder()
//                                         .expectedArrivalDate(resp.getExpectedArrivalDate())
//                                         .confidence(resp.getConfidence())
//                                         .windowDays(resp.getWindowDays())
//                                         .build();
//                             } else {
//                                 etaDto = EtaPredictionDto.builder()
//                                         .expectedArrivalDate(resp.getExpectedArrivalDate())
//                                         .confidence(resp.getConfidence())
//                                         .windowDays(resp.getWindowDays())
//                                         .build();   
//                             }
//                         }
//                     }
//                     return RefundDetailsDto.builder()
//                             .jurisdiction(status.getJurisdiction())
//                             .amount(status.getAmount())
//                             .status(status.getCanonicalStatus())
//                             .statusLastUpdatedAt(status.getStatusLastUpdatedAt())
//                             .statusMessageKey(status.getStatusMessageKey())
//                             .etaPrediction(etaDto)
//                             .build();
//                 })
//                 .toList();

//         return RefundStatusDto.withRefunds(filing.getTaxYear(), refundDetails);
//     }

//     /**
//      * Convert RefundStatusAggregatorDto to a list of RefundStatus domain objects.
//      */
//     private List<RefundStatus> aggregatorResponseToStatuses(RefundStatusAggregatorDto aggResp) {
//         List<RefundStatus> statuses = new ArrayList<>();
        
//         // Federal status
//         if (aggResp.getFederalStatus() != null) {
//             RefundStatus federal = RefundStatus.builder()
//                     .statusId(aggResp.getFederalStatus())
//                     .filingId(aggResp.getFilingId())
//                     .jurisdiction(Jurisdiction.FEDERAL)
//                     .canonicalStatus(aggResp.getFederalCanonicalStatus())
//                     .rawStatusCode(aggResp.getFederalRawStatusCode())
//                     .statusMessageKey(aggResp.getFederalStatusMessageKey())
//                     .statusLastUpdatedAt(aggResp.getFederalStatusLastUpdatedAt())
//                     .amount(aggResp.getFederalAmount())
//                     .build();
//             statuses.add(federal);
//         }
        
//         // State status
//         if (aggResp.getStateStatus() != null) {
//             RefundStatus state = RefundStatus.builder()
//                     .statusId(aggResp.getStateStatus())
//                     .filingId(aggResp.getFilingId())
//                     .jurisdiction(aggResp.getStateJurisdiction())
//                     .canonicalStatus(aggResp.getStateCanonicalStatus())
//                     .rawStatusCode(aggResp.getStateRawStatusCode())
//                     .statusMessageKey(aggResp.getStateStatusMessageKey())
//                     .statusLastUpdatedAt(aggResp.getStateStatusLastUpdatedAt())
//                     .amount(aggResp.getStateAmount())
//                     .build();
//             statuses.add(state);
//         }
        
//         return statuses;
//     }
// }
