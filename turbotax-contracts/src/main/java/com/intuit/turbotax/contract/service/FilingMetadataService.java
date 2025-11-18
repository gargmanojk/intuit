package com.intuit.turbotax.contract.service;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.intuit.turbotax.contract.FilingInfo;

public interface FilingMetadataService {   
    @GetMapping(
        value = "/filing-status/{userId}", 
        produces = "application/json") 
    List<FilingInfo> findLatestFilingForUser(@PathVariable String userId);
}