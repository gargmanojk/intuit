package com.intuit.turbotax.filingmetadata.domain;

import java.util.Optional;

public interface FilingMetadataRepository {
    
    Optional<FilingMetadata> findLatestByUserId(String userId);
}
