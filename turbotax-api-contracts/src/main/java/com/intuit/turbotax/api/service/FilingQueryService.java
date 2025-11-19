package com.intuit.turbotax.api.service;

import java.util.List;
import java.util.Optional;

import com.intuit.turbotax.api.model.TaxFiling;
import reactor.core.publisher.Mono;

public interface FilingQueryService {   
    List<TaxFiling> getFilings(String userId);
    Mono<TaxFiling> getFiling(int filingId);
}