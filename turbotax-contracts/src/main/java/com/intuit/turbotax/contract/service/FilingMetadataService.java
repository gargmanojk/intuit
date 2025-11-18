package com.intuit.turbotax.contract.service;

import java.util.List;

import com.intuit.turbotax.contract.FilingInfo;

public interface FilingMetadataService {   
    List<FilingInfo> findLatestFilingForUser(String userId);
}