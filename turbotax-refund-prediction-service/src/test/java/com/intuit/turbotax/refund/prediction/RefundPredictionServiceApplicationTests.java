package com.intuit.turbotax.refund.prediction;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.intuit.turbotax.api.service.FilingQueryService;
import com.intuit.turbotax.api.model.TaxFiling;
import com.intuit.turbotax.api.model.Jurisdiction;
import com.intuit.turbotax.api.model.PaymentMethod;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDate;

@SpringBootTest(webEnvironment = RANDOM_PORT)
class RefundPredictionServiceApplicationTests {
	@Autowired
	private WebTestClient client;

	@MockBean
	private FilingQueryService filingQueryService;

	@BeforeEach
	void setUp() {
		// Setup mock data for FilingQueryService
		TaxFiling mockFiling = new TaxFiling(
			12345,
			"TRACK-12345",
			Jurisdiction.FEDERAL,
			"test-user",
			2024,
			LocalDate.now().minusDays(30),
			new BigDecimal("1000.00"),
			PaymentMethod.DIRECT_DEPOSIT
		);
		
		when(filingQueryService.getFiling(anyInt()))
			.thenReturn(Mono.just(mockFiling));
	}

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