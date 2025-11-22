package com.intuit.turbotax.filing.query.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.intuit.turbotax.filing.query.mapper.TaxFilingMapper;
import com.intuit.turbotax.filing.query.repository.TaxFilingEntity;
import com.intuit.turbotax.api.model.TaxFiling;
import com.intuit.turbotax.filing.query.repository.TaxFilingRepository;
import com.intuit.turbotax.api.service.FilingQueryService;

@RestController
public class FilingQueryServiceRestController implements FilingQueryService {    
    private static final Logger LOG = LoggerFactory.getLogger(FilingQueryServiceRestController.class);
    private final TaxFilingRepository repository;
    private final TaxFilingMapper mapper;

    public FilingQueryServiceRestController(TaxFilingRepository repository, TaxFilingMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    @GetMapping(
        value = "/filings", 
        produces = "application/json") 
    public List<TaxFiling> getFilings(@RequestHeader("X-USER-ID") String userId) {    
        LOG.debug("Finding latest filings for userId={}", userId);        
        try (Stream<TaxFilingEntity> entityStream = repository.findLatestByUserId(userId)) {
            List<TaxFiling> filings = entityStream
                .map(e -> mapper.entityToApi(e))
                .toList();
            LOG.debug("Found {} filing entities for userId={}", filings.size(), userId);
            return filings;
        }
    }

    @Override 
    @GetMapping(
        value = "/filings/{filingId}", 
        produces = "application/json") 
    public Optional<TaxFiling> getFiling(@PathVariable int filingId) {
        LOG.debug("Finding filing by filingId={}", filingId);
        
        Optional<TaxFilingEntity> matchingEntity = repository.findByFilingId(filingId);
            
        if (matchingEntity.isPresent()) {
            TaxFiling filing = mapper.entityToApi(matchingEntity.get());
            LOG.debug("Found filing for filingId={}, jurisdiction={}", filingId, filing.jurisdiction());
            return Optional.of(filing);
        } else {
            LOG.debug("No filing found for filingId={}", filingId);
            return Optional.empty();
        }
    }
}