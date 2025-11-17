package com.intuit.turbotax.aggregator.integration;

import org.springframework.stereotype.Component;

public interface ExternalIrsClient {
    // methods to call IRS APIs
}

@Component
class ExternalIrsClientImpl implements ExternalIrsClient {
}