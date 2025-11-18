package com.intuit.turbotax.contract.service;

import java.util.List;

import com.intuit.turbotax.contract.data.RefundInfo;

public interface RefundDataAggregator {
    List<RefundInfo> getRefundStatusesForFiling(String filingId);
}