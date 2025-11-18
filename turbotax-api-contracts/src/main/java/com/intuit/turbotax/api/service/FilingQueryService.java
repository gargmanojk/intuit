package com.intuit.turbotax.api.service;

import java.util.List;

import com.intuit.turbotax.api.model.TaxFiling;

public interface FilingQueryService {   
    List<TaxFiling> findLatestFilingForUser(String userId);
}