package com.intuit.turbotax.api.service;

import java.util.List;

import com.intuit.turbotax.api.model.RefundStatusData;

public interface RefundDataAggregator {
    List<RefundStatusData> getRefundStatusesForFiling(int filingId);
}