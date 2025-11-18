package com.intuit.turbotax.filingmetadata.domain;

import java.util.List;

public interface FilingMetadataRepository {
    
    List<FilingMetadata> findLatestByUserId(String userId);
}
