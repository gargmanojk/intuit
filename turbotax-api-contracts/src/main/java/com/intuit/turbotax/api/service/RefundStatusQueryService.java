package com.intuit.turbotax.api.service;

import java.util.List;

import com.intuit.turbotax.api.model.RefundSummary;

public interface RefundStatusQueryService {
    List<RefundSummary> getLatestRefundStatus();
}