package com.intuit.turbotax.refund.query;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.intuit.turbotax.api.service.FilingQueryService;
import com.intuit.turbotax.api.service.RefundDataAggregator;
import com.intuit.turbotax.api.service.RefundEtaPredictor;
import com.intuit.turbotax.api.model.TaxFiling;
import com.intuit.turbotax.api.model.RefundStatusData;
import com.intuit.turbotax.api.model.RefundEtaPrediction;
import com.intuit.turbotax.api.model.Jurisdiction;
import com.intuit.turbotax.api.model.RefundStatus;

import java.time.LocalDate;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@SpringBootTest(webEnvironment = RANDOM_PORT)
class RefundQueryServiceApplicationTests {
	@Autowired
	private WebTestClient client;

	@Autowired
	private FilingQueryService filingQueryService;

	@Autowired
	private RefundDataAggregator refundDataAggregator;

	@Autowired
	private RefundEtaPredictor refundEtaPredictor;

	@TestConfiguration
	static class TestConfig {
		@Bean
		@Primary
		public FilingQueryService filingQueryService() {
			return mock(FilingQueryService.class);
		}

		@Bean
		@Primary
		public RefundDataAggregator refundDataAggregator() {
			return mock(RefundDataAggregator.class);
		}

		@Bean
		@Primary
		public RefundEtaPredictor refundEtaPredictor() {
			return mock(RefundEtaPredictor.class);
		}
	}

	@Test
	void contextLoads() {
		// Simple test to verify Spring context loads
	}

	@Test
	void testHttpResponseIsOK() {
		// Mock the external service calls
		TaxFiling mockFiling = new TaxFiling(
			"123",
			null, // trackingId
			Jurisdiction.FEDERAL,
			"mock-user-id-123",
			2025,
			LocalDate.now().minusDays(30),
			null, // refundAmount
			null // disbursementMethod
		);

		RefundStatusData mockRefundInfo = new RefundStatusData(
			"123",
			RefundStatus.PROCESSING,
			Jurisdiction.FEDERAL,
			Instant.now()
		);

		RefundEtaPrediction mockEtaInfo = new RefundEtaPrediction(
			LocalDate.now().plusDays(14),
			0.85,
			5
		);

		when(filingQueryService.findLatestFilingForUser(any())).thenReturn(List.of(mockFiling));
		when(refundDataAggregator.getRefundStatusesForFiling(any())).thenReturn(List.of(mockRefundInfo));
		when(refundEtaPredictor.predictEta(anyInt())).thenReturn(List.of(mockEtaInfo));

		// Basic integration test - verify endpoint is accessible
		client.get()
				.uri("/refund-status")
				.accept(APPLICATION_JSON)
				.exchange()
				.expectStatus().isOk();
	}
}
