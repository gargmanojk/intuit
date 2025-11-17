package com.intuit.turbotax.refundstatus.integration;

import java.util.Optional;
import java.util.List;

import com.intuit.turbotax.refundstatus.dto.FilingMetadataResponse;

public interface FilingMetadataService {
    List<FilingMetadataResponse> findLatestFilingForUser(String userId);
    
}
