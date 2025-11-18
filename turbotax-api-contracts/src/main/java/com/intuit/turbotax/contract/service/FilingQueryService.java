package com.intuit.turbotax.contract.service;

import java.util.List;

import com.intuit.turbotax.contract.data.TaxFiling;

public interface FilingQueryService {   
    List<TaxFiling> findLatestFilingForUser(String userId);
}