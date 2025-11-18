package com.intuit.turbotax.refund.prediction;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = RANDOM_PORT)
class RefundPredictionServiceApplicationTests {
	@Autowired
	private WebTestClient client;

	@Test
	void contextLoads() {
		// Simple test to verify Spring context loads
	}

	@Test
	void testHttpResponseIsOK() {
		client.get()
				.uri(uriBuilder -> uriBuilder
						.path("/refund-eta")
						.queryParam("taxYear", 2025)
						.queryParam("jurisdiction", "FEDERAL")
						.queryParam("filingDate", "2025-02-15")
						.queryParam("refundAmount", "1500.00")
						.queryParam("returnStatus", "PROCESSING")
						.queryParam("disbursementMethod", "DIRECT_DEPOSIT")
						.build())
				.accept(APPLICATION_JSON)
				.exchange()
				.expectStatus().isOk();
	}
}