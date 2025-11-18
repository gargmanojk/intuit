package com.intuit.turbotax.refund.aggregation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {
	"com.intuit.turbotax.refund.aggregation",
	"com.intuit.turbotax.api.service"
})
public class RefundAggregationServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(RefundAggregationServiceApplication.class, args);
	}

}
