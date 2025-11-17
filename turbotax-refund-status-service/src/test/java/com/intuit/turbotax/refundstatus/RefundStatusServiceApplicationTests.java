package com.intuit.turbotax.refundstatus;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;
import static org.mockito.Mockito.when;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;

import com.intuit.turbotax.refundstatus.dto.RefundStatusAggregatorResponse;
import com.intuit.turbotax.refundstatus.integration.RefundStatusAggregatorService;

@SpringBootTest(webEnvironment = RANDOM_PORT)
class RefundStatusServiceApplicationTests {
	@Autowired
	private WebTestClient client;

	@MockBean
	private RefundStatusAggregatorService refundStatusAggregatorService;

	@Test
	void testGetLatestRefundStatus() {
		// Mock aggregator response so test doesn't require external service
		RefundStatusAggregatorResponse agg = RefundStatusAggregatorResponse.builder()
			.filingId("TT2025100")
			.federalStatus("DEPOSITED")
			.federalCanonicalStatus(com.intuit.turbotax.domainmodel.RefundCanonicalStatus.DEPOSITED)
			.federalRawStatusCode("1001")
			.federalStatusMessageKey("MSG_REFUND_DEPOSITED")
			.federalStatusLastUpdatedAt(Instant.now())
			.federalAmount(BigDecimal.valueOf(1000))
			.stateStatus("DEPOSITED")
			.stateJurisdiction(com.intuit.turbotax.domainmodel.Jurisdiction.STATE_CA)
			.stateCanonicalStatus(com.intuit.turbotax.domainmodel.RefundCanonicalStatus.PROCESSING)
			.stateRawStatusCode("1002")
			.stateStatusMessageKey("MSG_REFUND_PROCESSING")
			.stateStatusLastUpdatedAt(Instant.now())
			.stateAmount(BigDecimal.valueOf(100))
			.build();

		when(refundStatusAggregatorService.getRefundStatusesForFiling("TT2025100")).thenReturn(Optional.of(agg));

		client.get()
				.uri("/refund-status")
				.accept(APPLICATION_JSON)
				.exchange()
				.expectStatus().isOk()
				.expectHeader().contentType(APPLICATION_JSON)
				.expectBody()
				.jsonPath("$.filingFound").isEqualTo(true)
				.jsonPath("$.taxYear").isNotEmpty()
				.jsonPath("$.refunds[0].jurisdiction").isNotEmpty()
				.jsonPath("$.refunds[0].status").isNotEmpty();
	}
}
