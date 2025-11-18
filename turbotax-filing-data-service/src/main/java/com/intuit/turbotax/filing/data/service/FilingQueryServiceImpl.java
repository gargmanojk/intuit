package com.intuit.turbotax.filing.data.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.intuit.turbotax.filing.data.repository.TaxFilingEntity;
import com.intuit.turbotax.api.model.TaxFiling;
import com.intuit.turbotax.filing.data.repository.TaxFilingRepository;
import com.intuit.turbotax.api.service.FilingQueryService;

@RestController
public class FilingQueryServiceImpl implements FilingQueryService {    
    private static final Logger LOG = LoggerFactory.getLogger(FilingQueryServiceImpl.class);
    private final TaxFilingRepository repository;

    public FilingQueryServiceImpl(TaxFilingRepository repository) {
        this.repository = repository;
    }

    @Override
    @GetMapping(
        value = "/filing-info/{userId}", 
        produces = "application/json") 
    public List<TaxFiling> findLatestFilingForUser(@PathVariable String userId) {
        // Mock: delegate to repository
        List<TaxFilingEntity> entity = repository.findLatestByUserId(userId);
        return entity.stream().map(e -> toDto(e)).toList();
    }

    public TaxFiling toDto(TaxFilingEntity entity) {  
        if (entity == null) {
            return null;
        }

        return new TaxFiling(
                entity.getFilingId(),
                entity.getTrackingId(),
                entity.getJurisdiction(),
                entity.getUserId(),
                entity.getTaxYear(),
                entity.getFilingDate(),
                entity.getRefundAmount(),
                entity.getDisbursementMethod()
        );
    }
}
