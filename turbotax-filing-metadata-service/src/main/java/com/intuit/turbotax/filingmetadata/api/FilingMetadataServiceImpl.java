package com.intuit.turbotax.filingmetadata.api;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.intuit.turbotax.filingmetadata.domain.FilingMetadata;
import com.intuit.turbotax.contract.data.FilingInfo;
import com.intuit.turbotax.filingmetadata.domain.FilingMetadataRepository;
import com.intuit.turbotax.contract.service.FilingMetadataService;

@RestController
public class FilingMetadataServiceImpl implements FilingMetadataService {    
    private static final Logger LOG = LoggerFactory.getLogger(FilingMetadataServiceImpl.class);
    private final FilingMetadataRepository repository;

    public FilingMetadataServiceImpl(FilingMetadataRepository repository) {
        this.repository = repository;
    }

    @Override
    @GetMapping(
        value = "/filing-status/{userId}", 
        produces = "application/json") 
    public List<FilingInfo> findLatestFilingForUser(@PathVariable String userId) {
        // Mock: delegate to repository
        List<FilingMetadata> entity = repository.findLatestByUserId(userId);
        return entity.stream().map(e -> toDto(e)).toList();
    }

    public FilingInfo toDto(FilingMetadata entity) {  
        if (entity == null) {
            return null;
        }

        FilingInfo dto = FilingInfo.builder()
                .filingId(entity.getFilingId())
                .jurisdiction(entity.getJurisdiction())
                .userId(entity.getUserId())
                .taxYear(entity.getTaxYear())
                .filingDate(entity.getFilingDate())
                .refundAmount(entity.getRefundAmount())
                .trackingId(entity.getTrackingId())
                .disbursementMethod(entity.getDisbursementMethod())
                .build();

        return dto;
    }
}
