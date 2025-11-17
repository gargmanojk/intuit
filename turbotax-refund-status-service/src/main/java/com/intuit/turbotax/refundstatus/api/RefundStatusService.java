package com.intuit.turbotax.refundstatus.api;

import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;

import com.intuit.turbotax.contract.RefundSummaryInfo;

public interface RefundStatusService {
    @GetMapping(
        value = "/refund-status", 
        produces = "application/json")
    List<RefundSummaryInfo> getLatestRefundStatus();
}
