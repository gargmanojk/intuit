package com.intuit.turbotax.refundstatus.integration;

import java.util.Optional;

import com.intuit.turbotax.refundstatus.dto.FilingMetadataResponse;

public interface FilingMetadataService {
    Optional<FilingMetadataResponse> findLatestFilingForUser(String userId);
    
}
