package com.intuit.turbotax.refundstatus.domain.filing;

import java.util.Optional;

public interface FilingMetadataService {
    
    Optional<FilingMetadata> findLatestFilingForUser(String userId);
}
