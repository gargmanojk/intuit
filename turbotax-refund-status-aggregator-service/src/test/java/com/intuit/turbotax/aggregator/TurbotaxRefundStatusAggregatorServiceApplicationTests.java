package com.intuit.turbotax.aggregator;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = RANDOM_PORT)
class TurbotaxRefundStatusAggregatorServiceApplicationTests {
	@Autowired
	private WebTestClient client;

	@Test
	void testGestGetAggregatedRefundStatus() {
		String filingId = "test-filing-123";
		client.get()
				.uri("/aggregate-status/{filingId}", filingId)
				.accept(APPLICATION_JSON)
				.exchange()
				.expectStatus().isOk()
				.expectHeader().contentType(APPLICATION_JSON)
				.expectBody()
				.jsonPath("$.filingId").isEqualTo(filingId)
				.jsonPath("$.federalStatus").isNotEmpty()
				.jsonPath("$.federalCanonicalStatus").isNotEmpty()
				.jsonPath("$.stateStatus").isNotEmpty()
				.jsonPath("$.stateJurisdiction").isNotEmpty();
	}
}
