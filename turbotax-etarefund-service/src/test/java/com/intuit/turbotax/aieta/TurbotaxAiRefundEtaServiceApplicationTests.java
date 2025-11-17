package com.intuit.turbotax.aieta;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.intuit.turbotax.contract.EtaRefundRequest;
import com.intuit.turbotax.contract.Jurisdiction;
import com.intuit.turbotax.contract.RefundCanonicalStatus;

@SpringBootTest(webEnvironment = RANDOM_PORT)
class TurbotaxAiRefundEtaServiceApplicationTests {
	@Autowired
	private WebTestClient client;

	    @Test
	    void testRefundEta() {
		client.get()
			.uri(uriBuilder -> uriBuilder
				.path("/refund-eta")
				.queryParam("taxYear", 2025)
				.queryParam("jurisdiction", "FEDERAL")
				.queryParam("filingDate", "2025-02-15")
				.queryParam("refundAmount", "1500.00")
				.queryParam("returnStatus", "PROCESSING")
				.queryParam("disbursementMethod", "DIRECT_DEPOSIT")
				.build())
			.accept(APPLICATION_JSON)
			.exchange()
			.expectStatus().isOk()
			.expectHeader().contentType(APPLICATION_JSON)
			.expectBody()
			.jsonPath("$.expectedArrivalDate").exists()
			.jsonPath("$.confidence").exists()
			.jsonPath("$.windowDays").exists();
	    }
}