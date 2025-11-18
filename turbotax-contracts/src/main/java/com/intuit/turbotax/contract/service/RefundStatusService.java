package com.intuit.turbotax.contract.service;

import java.util.List;

import com.intuit.turbotax.contract.data.RefundSummaryInfo;

public interface RefundStatusService {
    List<RefundSummaryInfo> getLatestRefundStatus();
}