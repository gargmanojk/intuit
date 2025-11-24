package com.intuit.turbotax.filing.query.controller;

import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.intuit.turbotax.api.v1.common.model.Jurisdiction;
import com.intuit.turbotax.api.v1.common.model.PaymentMethod;
import com.intuit.turbotax.api.v1.filing.model.TaxFiling;
import com.intuit.turbotax.filing.query.service.FilingQueryService;

@WebFluxTest(FilingQueryController.class)
class FilingQueryControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private FilingQueryService filingQueryService;

    @Test
    void getFilings_ShouldReturnFilings() {
        // Given
        TaxFiling filing = new TaxFiling(
                202410001, "TRACK-001", Jurisdiction.FEDERAL, "user123",
                2024, LocalDate.of(2024, 4, 15), BigDecimal.valueOf(2500.00),
                PaymentMethod.ACH, true);
        when(filingQueryService.getFilings("user123")).thenReturn(List.of(filing));

        // When & Then
        webTestClient.get()
                .uri("/api/v1/filings/latest")
                .header("X-USER-ID", "user123")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(TaxFiling.class).hasSize(1);
    }
}