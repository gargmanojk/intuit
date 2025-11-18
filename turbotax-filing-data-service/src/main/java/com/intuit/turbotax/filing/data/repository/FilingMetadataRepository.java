package com.intuit.turbotax.filing.data.repository;

import java.util.List;

public interface FilingMetadataRepository {
    
    List<FilingMetadata> findLatestByUserId(String userId);
}
