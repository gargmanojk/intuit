package com.intuit.turbotax.filingmetadata.api;

import java.util.Optional;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.intuit.turbotax.filingmetadata.dto.FilingMetadataResponse;

public interface FilingMetadataService {   
    @GetMapping(
        value = "/filing-status/{userId}", 
        produces = "application/json") 
    Optional<FilingMetadataResponse> findLatestFilingForUser(@PathVariable String userId);
}
