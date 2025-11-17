package com.intuit.turbotax.refundstatus.api;

import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;

import com.intuit.turbotax.domainmodel.dto.RefundStatusDto;

public interface RefundStatusService {
    @GetMapping(
        value = "/refund-status", 
        produces = "application/json")
    RefundStatusDto getLatestRefundStatus();
}
