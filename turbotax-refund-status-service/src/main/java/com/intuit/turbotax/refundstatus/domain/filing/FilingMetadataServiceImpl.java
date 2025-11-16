package com.intuit.turbotax.refundstatus.domain.filing;

import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class FilingMetadataServiceImpl implements FilingMetadataService {    
    private final FilingMetadataRepository repository;

    public FilingMetadataServiceImpl(FilingMetadataRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<FilingMetadata> findLatestFilingForUser(String userId) {
        // Mock: delegate to repository
        return repository.findLatestByUserId(userId);
    }
}
