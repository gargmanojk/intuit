package com.intuit.turbotax.refundstatus.domain.refund;

import org.springframework.stereotype.Component;

public interface ExternalStateTaxClient {
    // methods to call state APIs
}

@Component
class ExternalStateTaxClientImpl implements ExternalStateTaxClient {    
}
