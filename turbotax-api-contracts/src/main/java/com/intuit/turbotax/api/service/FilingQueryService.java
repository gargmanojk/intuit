package com.intuit.turbotax.api.service;

import java.util.List;
import java.util.Optional;

import com.intuit.turbotax.api.model.TaxFiling;

public interface FilingQueryService {   
    List<TaxFiling> getFilings(String userId);
    Optional<TaxFiling> getFiling(int filingId);
}