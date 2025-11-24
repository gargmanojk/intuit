package com.intuit.turbotax.filing.query.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.intuit.turbotax.api.v1.filing.model.TaxFiling;
import com.intuit.turbotax.filing.query.service.FilingQueryService;

/**
 * REST controller for tax filing query operations.
 * Handles HTTP requests and delegates business logic to the service layer.
 */
@RestController
@RequestMapping("/filings")
public class FilingQueryController {

    private static final Logger log = LoggerFactory.getLogger(FilingQueryController.class);

    private final FilingQueryService filingQueryService;

    public FilingQueryController(FilingQueryService filingQueryService) {
        this.filingQueryService = filingQueryService;
    }

    /**
     * Retrieves all tax filings for a user.
     *
     * @param userId the user ID from the X-USER-ID header
     * @return list of tax filings
     */
    @GetMapping(produces = "application/json")
    public ResponseEntity<List<TaxFiling>> getFilings(
            @RequestHeader("X-USER-ID") String userId) {
        log.debug("Received request to get filings for userId={}", userId);

        List<TaxFiling> filings = filingQueryService.getFilings(userId);
        return ResponseEntity.ok(filings);
    }
}