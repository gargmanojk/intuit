package com.intuit.turbotax.refund.aggregation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {
	"com.intuit.turbotax.aggregator",
	"com.intuit.turbotax.contract.service"
})
public class TurbotaxRefundStatusAggregatorServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(TurbotaxRefundStatusAggregatorServiceApplication.class, args);
	}

}
