package com.intuit.turbotax.refundstatus.domain.refund;

import org.springframework.stereotype.Component;

public interface MoneyMovementClient {
    // methods to query bank/money-movement systems
}

@Component
class MoneyMovementClientImpl implements MoneyMovementClient {    
}
