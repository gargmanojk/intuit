package com.intuit.turbotax.refundstatus;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = RANDOM_PORT)
class RefundStatusServiceApplicationTests {
	@Autowired
	private WebTestClient client;

	// @Test
	void testHttpResponseIsOK() {
		// Basic integration test - verify endpoint is accessible
		client.get()
				.uri("/refund-status")
				.accept(APPLICATION_JSON)
				.exchange()
				.expectStatus().isOk();
	}

	@Test
	void contextLoads() {
		// Simple test to verify Spring context loads
	}
}
