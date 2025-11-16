package com.intuit.turbotax.refundstatus.domain.filing;

import java.util.Optional;

public interface FilingMetadataRepository {
    
    Optional<FilingMetadata> findLatestByUserId(String userId);
}
