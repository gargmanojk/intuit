package com.intuit.turbotax.filing.data;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = RANDOM_PORT)
class FilingDataServiceApplicationTests {
	@Autowired
	private WebTestClient client;

	@Test
	void contextLoads() {
		// Simple test to verify Spring context loads
	}

	@Test
	void testHttpResponseIsOK() {
		client.get()
				.uri("/filing-info/mgarg")
				.accept(APPLICATION_JSON)
				.exchange()
				.expectStatus().isOk();
	}
}
