package com.intuit.turbotax.refundstatus.integration;

import java.util.Optional;
import java.util.List;

import com.intuit.turbotax.domainmodel.FilingInfo;

public interface FilingMetadataService {
    List<FilingInfo> findLatestFilingForUser(String userId);
    
}
