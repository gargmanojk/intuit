package com.intuit.turbotax.filing.query.service;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.intuit.turbotax.api.v1.filing.model.TaxFiling;
import com.intuit.turbotax.filing.query.mapper.TaxFilingMapper;
import com.intuit.turbotax.filing.query.repository.TaxFilingEntity;
import com.intuit.turbotax.filing.query.repository.TaxFilingRepository;
import com.intuit.turbotax.filing.query.validation.FilingValidator;

/**
 * Implementation of the FilingQueryService.
 * Provides business logic for tax filing query operations with caching and
 * validation.
 */
@Service
@CacheConfig(cacheNames = "filings")
public class FilingQueryServiceImpl implements FilingQueryService {

    private static final Logger log = LoggerFactory.getLogger(FilingQueryServiceImpl.class);

    private final TaxFilingRepository repository;
    private final TaxFilingMapper mapper;
    private final FilingValidator validator;

    public FilingQueryServiceImpl(TaxFilingRepository repository, TaxFilingMapper mapper, FilingValidator validator) {
        this.repository = repository;
        this.mapper = mapper;
        this.validator = validator;
    }

    @Override
    @Cacheable(key = "#userId")
    public List<TaxFiling> getFilings(String userId) {
        MDC.put("userId", userId);
        MDC.put("operation", "getFilings");
        try {
            log.info("Retrieving all filings for user");
            validator.validateUserId(userId);

            List<TaxFilingEntity> entities = repository.findLatestByUserId(userId)
                    .collect(Collectors.toList());

            List<TaxFiling> filings = entities.stream()
                    .map(mapper::entityToApi)
                    .collect(Collectors.toList());

            log.info("Successfully retrieved {} filings for user", filings.size());
            return filings;
        } finally {
            MDC.clear();
        }
    }
}