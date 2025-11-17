package com.intuit.turbotax.filingmetadata.domain;

import java.util.List;  

import com.intuit.turbotax.filingmetadata.domain.FilingMetadata;

public interface FilingMetadataRepository {
    
    List<FilingMetadata> findLatestByUserId(String userId);
}
