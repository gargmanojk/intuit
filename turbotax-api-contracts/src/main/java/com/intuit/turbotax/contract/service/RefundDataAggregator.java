package com.intuit.turbotax.contract.service;

import java.util.List;

import com.intuit.turbotax.contract.data.RefundStatusData;

public interface RefundDataAggregator {
    List<RefundStatusData> getRefundStatusesForFiling(String filingId);
}