package com.intuit.turbotax.filingmetadata.api;

import java.util.Optional;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.intuit.turbotax.domainmodel.dto.FilingMetadataDto;

public interface FilingMetadataService {   
    @GetMapping(
        value = "/filing-status/{userId}", 
        produces = "application/json") 
    List<FilingMetadataDto> findLatestFilingForUser(@PathVariable String userId);
}
