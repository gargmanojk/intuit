package com.intuit.turbotax.refund.aggregation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {
	"com.intuit.turbotax.refund.aggregation",
	"com.intuit.turbotax.api.service",
	"com.intuit.turbotax.refund.aggregation.client",
	"com.intuit.turbotax.client"
})
public class RefundAggregationServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(RefundAggregationServiceApplication.class, args);
	}

}
