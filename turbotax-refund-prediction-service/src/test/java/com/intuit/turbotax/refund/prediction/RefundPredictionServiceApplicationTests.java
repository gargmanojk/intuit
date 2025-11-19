package com.intuit.turbotax.refund.prediction;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.intuit.turbotax.api.service.FilingQueryService;

@SpringBootTest(webEnvironment = RANDOM_PORT)
class RefundPredictionServiceApplicationTests {
	@Autowired
	private WebTestClient client;

	@MockBean
	private FilingQueryService filingQueryService;

	@Test
	void contextLoads() {
		// Simple test to verify Spring context loads
	}

	@Test
	void testHttpResponseIsOK() {
		// Test the new endpoint pattern with filing ID path parameter
		client.get()
				.uri("/refund-eta/12345")
				.accept(APPLICATION_JSON)
				.exchange()
				.expectStatus().isOk();
	}
}