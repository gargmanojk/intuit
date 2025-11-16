package com.intuit.turbotax.refundstatus;

import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@ComponentScan("com.intuit.turbotax")
public class RefundStatusServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(RefundStatusServiceApplication.class, args);
	}

}
