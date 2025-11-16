package com.intuit.turbotax.refundstatus.domain.refund;

import org.springframework.stereotype.Component;

public interface ExternalIrsClient {
    // methods to call IRS APIs
}

@Component
class ExternalIrsClientImpl implements ExternalIrsClient {
}