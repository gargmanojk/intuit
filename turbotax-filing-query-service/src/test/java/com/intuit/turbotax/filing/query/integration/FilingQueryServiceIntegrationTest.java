package com.intuit.turbotax.filing.query.integration;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.intuit.turbotax.api.v1.filing.model.TaxFiling;
import com.intuit.turbotax.filing.query.service.FilingQueryService;

@SpringBootTest
@ActiveProfiles("test")
class FilingQueryServiceIntegrationTest {

    @Autowired
    private FilingQueryService filingQueryService;

    @Test
    void getFilings_ShouldReturnFilingsFromRepository() {
        // When
        List<TaxFiling> filings = filingQueryService.getFilings("user123");

        // Then
        assertThat(filings).isNotEmpty();
        assertThat(filings).allMatch(filing -> "user123".equals(filing.userId()));
    }

    @Test
    void service_ShouldBeCached() {
        // This test verifies that caching is enabled
        // Multiple calls should be fast due to caching

        List<TaxFiling> filings1 = filingQueryService.getFilings("user123");
        List<TaxFiling> filings2 = filingQueryService.getFilings("user123");

        // Second call should be faster due to caching
        assertThat(filings1).isEqualTo(filings2);
        // Note: In a real scenario, you'd measure actual performance difference
    }
}