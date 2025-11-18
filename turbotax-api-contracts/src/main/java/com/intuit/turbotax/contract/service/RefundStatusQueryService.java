package com.intuit.turbotax.contract.service;

import java.util.List;

import com.intuit.turbotax.contract.data.RefundSummary;

public interface RefundStatusQueryService {
    List<RefundSummary> getLatestRefundStatus();
}