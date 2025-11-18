package com.intuit.turbotax.contract.service;

import java.util.List;

import com.intuit.turbotax.contract.data.FilingInfo;

public interface FilingQueryService {   
    List<FilingInfo> findLatestFilingForUser(String userId);
}