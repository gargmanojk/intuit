package com.intuit.turbotax.contract.service;

import java.util.List;

import com.intuit.turbotax.contract.RefundInfo;

public interface RefundStatusAggregatorService {
    List<RefundInfo> getRefundStatusesForFiling(String filingId);
}