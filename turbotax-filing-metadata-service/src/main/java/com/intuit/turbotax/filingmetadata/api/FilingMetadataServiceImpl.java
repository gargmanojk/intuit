package com.intuit.turbotax.filingmetadata.api;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.stereotype.Service;

import com.intuit.turbotax.filingmetadata.domain.FilingMetadata;
import com.intuit.turbotax.filingmetadata.dto.FilingMetadataResponse;
import com.intuit.turbotax.filingmetadata.domain.FilingMetadataRepository;

@RestController
public class FilingMetadataServiceImpl implements FilingMetadataService {    
    private static final Logger LOG = LoggerFactory.getLogger(FilingMetadataServiceImpl.class);
    private final FilingMetadataRepository repository;

    public FilingMetadataServiceImpl(FilingMetadataRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<FilingMetadataResponse> findLatestFilingForUser(String userId) {
        // Mock: delegate to repository
        Optional<FilingMetadata> entity = repository.findLatestByUserId(userId);
        return FilingMetadataResponse.fromEntity(entity);
    }
}
