package com.intuit.turbotax.filingmetadata;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = RANDOM_PORT)
class TurbotaxFilingMetadataServiceApplicationTests {

	@Autowired
	private WebTestClient client;

	@Test
	void testGetLatestRefundStatus() {
		client.get()
				.uri("/filing-status/mgarg")
				.accept(APPLICATION_JSON)
				.exchange()
				.expectStatus().isOk()
				.expectHeader().contentType(APPLICATION_JSON)
				.expectBody()
				.jsonPath("$").isArray()
				.jsonPath("$[0].filingId").isNotEmpty()
				.jsonPath("$[0].userId").isEqualTo("mgarg")
				.jsonPath("$[0].taxYear").isNumber()
				.jsonPath("$[0].refundAmount").isNumber()
				.jsonPath("$[0].trackingId").isNotEmpty()
				.jsonPath("$[0].disbursementMethod").isNotEmpty();
	}

}
