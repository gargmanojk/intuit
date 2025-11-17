package com.intuit.turbotax.aggregator.integration;

import org.springframework.stereotype.Component;

public interface MoneyMovementClient {
    // methods to query bank/money-movement systems
}

@Component
class MoneyMovementClientImpl implements MoneyMovementClient {    
}
