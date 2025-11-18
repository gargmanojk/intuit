package com.intuit.turbotax.refund.aggregation.client;

import org.springframework.stereotype.Component;

public interface ExternalIrsClient {
    // methods to call IRS APIs
}

@Component
class ExternalIrsClientImpl implements ExternalIrsClient {
}