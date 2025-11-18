package com.intuit.turbotax.refundstatus;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.intuit.turbotax.refundstatus.integration.FilingMetadataService;
import com.intuit.turbotax.refundstatus.integration.RefundStatusAggregatorService;
import com.intuit.turbotax.refundstatus.integration.AiRefundEtaService;
import com.intuit.turbotax.contract.FilingInfo;
import com.intuit.turbotax.contract.RefundInfo;
import com.intuit.turbotax.contract.EtaRefundInfo;
import com.intuit.turbotax.contract.Jurisdiction;
import com.intuit.turbotax.contract.RefundStatus;

import java.time.LocalDate;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@SpringBootTest(webEnvironment = RANDOM_PORT)
class RefundStatusServiceApplicationTests {
	@Autowired
	private WebTestClient client;

	@Autowired
	private FilingMetadataService filingMetadataService;

	@Autowired
	private RefundStatusAggregatorService refundStatusAggregatorService;

	@Autowired
	private AiRefundEtaService aiRefundEtaService;

	@TestConfiguration
	static class TestConfig {
		@Bean
		@Primary
		public FilingMetadataService filingMetadataService() {
			return mock(FilingMetadataService.class);
		}

		@Bean
		@Primary
		public RefundStatusAggregatorService refundStatusAggregatorService() {
			return mock(RefundStatusAggregatorService.class);
		}

		@Bean
		@Primary
		public AiRefundEtaService aiRefundEtaService() {
			return mock(AiRefundEtaService.class);
		}
	}

	@Test
	void contextLoads() {
		// Simple test to verify Spring context loads
	}

	@Test
	void testHttpResponseIsOK() {
		// Mock the external service calls
		FilingInfo mockFiling = FilingInfo.builder()
			.filingId("123")
			.userId("mock-user-id-123")
			.taxYear(2025)
			.jurisdiction(Jurisdiction.FEDERAL)
			.filingDate(LocalDate.now().minusDays(30))
			.build();

		RefundInfo mockRefundInfo = RefundInfo.builder()
			.filingId("123")
			.status(RefundStatus.PROCESSING)
			.jurisdiction(Jurisdiction.FEDERAL)
			.lastUpdatedAt(Instant.now())
			.build();

		EtaRefundInfo mockEtaInfo = EtaRefundInfo.builder()
			.expectedArrivalDate(LocalDate.now().plusDays(14))
			.confidence(0.85)
			.windowDays(5)
			.build();

		when(filingMetadataService.findLatestFilingForUser(any())).thenReturn(List.of(mockFiling));
		when(refundStatusAggregatorService.getRefundStatusesForFiling(any())).thenReturn(List.of(mockRefundInfo));
		when(aiRefundEtaService.predictEta(any())).thenReturn(Optional.of(mockEtaInfo));

		// Basic integration test - verify endpoint is accessible
		client.get()
				.uri("/refund-status")
				.accept(APPLICATION_JSON)
				.exchange()
				.expectStatus().isOk();
	}
}
