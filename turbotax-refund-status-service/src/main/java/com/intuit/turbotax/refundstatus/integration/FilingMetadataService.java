package com.intuit.turbotax.refundstatus.integration;

import java.util.Optional;
import java.util.List;

import com.intuit.turbotax.domainmodel.dto.FilingMetadataDto;

public interface FilingMetadataService {
    List<FilingMetadataDto> findLatestFilingForUser(String userId);
    
}
