package com.intuit.turbotax.filing.query;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = RANDOM_PORT)
class FilingQueryServiceApplicationTests {

	@Autowired
	private WebTestClient client;

	@Test
	void contextLoads() {
		// Test that the application context loads successfully
		// and all beans are properly configured
	}

	@Test
	void healthEndpoint_ShouldReturnOk() {
		client.get()
				.uri("/actuator/health")
				.exchange()
				.expectStatus().isOk();
	}

	@Test
	void filingsEndpoint_ShouldReturnOk() {
		client.get()
				.uri("/api/v1/filings/latest")
				.header("X-USER-ID", "user123")
				.exchange()
				.expectStatus().isOk();
	}
}
