package com.intuit.turbotax.refundstatus.integration;

import java.util.List;

import com.intuit.turbotax.contract.data.FilingInfo;

public interface FilingMetadataService {
    List<FilingInfo> findLatestFilingForUser(String userId);
    
}
