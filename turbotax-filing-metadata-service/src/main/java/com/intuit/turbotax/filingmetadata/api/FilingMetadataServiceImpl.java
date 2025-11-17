package com.intuit.turbotax.filingmetadata.api;

import java.util.Optional;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.stereotype.Service;

import com.intuit.turbotax.filingmetadata.domain.FilingMetadata;
import com.intuit.turbotax.domainmodel.dto.FilingMetadataDto;
import com.intuit.turbotax.filingmetadata.domain.FilingMetadataRepository;
import com.intuit.turbotax.filingmetadata.api.FilingMetadataService;

@RestController
public class FilingMetadataServiceImpl implements FilingMetadataService {    
    private static final Logger LOG = LoggerFactory.getLogger(FilingMetadataServiceImpl.class);
    private final FilingMetadataRepository repository;

    public FilingMetadataServiceImpl(FilingMetadataRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<FilingMetadataDto> findLatestFilingForUser(String userId) {
        // Mock: delegate to repository
        List<FilingMetadata> entity = repository.findLatestByUserId(userId);
        return entity.stream().map(e -> toDto(e)).toList();
    }

    public FilingMetadataDto toDto(FilingMetadata entity) {  
        if (entity == null) {
            return null;
        }

        FilingMetadataDto dto = FilingMetadataDto.builder()
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
