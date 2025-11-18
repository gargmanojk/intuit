package com.intuit.turbotax.refund.aggregation;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = RANDOM_PORT)
class RefundAggregationServiceApplicationTests {
	@Autowired
	private WebTestClient client;

	@Test
	void contextLoads() {
		// Simple test to verify Spring context loads
	}

	@Test
	void testHttpResponseIsOK() {
		int filingId = 123456789;
		client.get()
				.uri("/aggregate-status/{filingId}", filingId)
				.accept(APPLICATION_JSON)
				.exchange()
				.expectStatus().isOk();
	}
}
