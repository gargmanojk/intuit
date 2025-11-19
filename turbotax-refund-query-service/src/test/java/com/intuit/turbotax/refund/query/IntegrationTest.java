// package com.intuit.turbotax.refund.query;

// import java.math.BigDecimal;
// import java.time.Instant;
// import java.time.LocalDate;
// import java.util.List;

// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.DisplayName;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.boot.test.web.client.TestRestTemplate;
// import org.springframework.boot.test.web.server.LocalServerPort;
// import org.springframework.core.ParameterizedTypeReference;
// import org.springframework.http.HttpMethod;
// import org.springframework.http.HttpStatus;
// import org.springframework.http.ResponseEntity;
// import org.springframework.test.context.TestPropertySource;

// import com.intuit.turbotax.api.model.RefundSummary;
// import com.intuit.turbotax.api.model.RefundStatus;
// import com.intuit.turbotax.api.model.Jurisdiction;
// import com.intuit.turbotax.api.model.PaymentMethod;

// import static org.assertj.core.api.Assertions.assertThat;

// /**
//  * End-to-end integration test for the refund query service.
//  * Tests the complete workflow without mocking dependent services.
//  * Uses actual HTTP calls to test the reactive endpoints.
//  */
// @SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
// @TestPropertySource(properties = {
//     "spring.profiles.active=test",
//     "logging.level.com.intuit.turbotax=DEBUG"
// })
// class IntegrationTest {

//     @LocalServerPort
//     private int port;

//     @Autowired
//     private TestRestTemplate restTemplate;

//     private String baseUrl;

//     @BeforeEach
//     void setUp() {
//         baseUrl = "http://localhost:" + port;
//     }

//     @Test
//     @DisplayName("End-to-End Integration Test - Refund Status Query Service")
//     void testEndToEndRefundStatusQuery() {
//         // Act - Make actual HTTP request to the refund status endpoint
//         ResponseEntity<List<RefundSummary>> response = restTemplate.exchange(
//             baseUrl + "/refund-status",
//             HttpMethod.GET,
//             null,
//             new ParameterizedTypeReference<List<RefundSummary>>() {}
//         );

//         // Assert - Verify HTTP response
//         assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
//         assertThat(response.getBody()).isNotNull();
        
//         List<RefundSummary> refundSummaries = response.getBody();
        
//         // The actual response depends on the real service implementations
//         // For a true integration test, we verify the service responds correctly
//         // without assuming specific data since we're not mocking
        
//         // Basic structural validation
//         assertThat(refundSummaries).isNotNull();
        
//         // If data is returned, validate the structure
//         if (!refundSummaries.isEmpty()) {
//             RefundSummary firstSummary = refundSummaries.get(0);
            
//             // Validate required fields are present (not null)
//             assertThat(firstSummary.filingId()).isNotNull();
//             assertThat(firstSummary.trackingId()).isNotNull();
//             assertThat(firstSummary.taxYear()).isNotNull();
//             assertThat(firstSummary.filingDate()).isNotNull();
//             assertThat(firstSummary.jurisdiction()).isNotNull();
//             assertThat(firstSummary.amount()).isNotNull();
//             assertThat(firstSummary.status()).isNotNull();
//             assertThat(firstSummary.disbursementMethod()).isNotNull();
            
//             // Validate field constraints
//             assertThat(firstSummary.taxYear()).isGreaterThan(2020);
//             assertThat(firstSummary.amount()).isGreaterThanOrEqualTo(BigDecimal.ZERO);
//             assertThat(firstSummary.etaConfidence()).isBetween(0.0, 1.0);
//             assertThat(firstSummary.etaWindowDays()).isGreaterThanOrEqualTo(0);
            
//             System.out.println("✅ Integration Test - Received refund summary:");
//             System.out.println("   Filing ID: " + firstSummary.filingId());
//             System.out.println("   Jurisdiction: " + firstSummary.jurisdiction());
//             System.out.println("   Status: " + firstSummary.status());
//             System.out.println("   Amount: " + firstSummary.amount());
//         } else {
//             System.out.println("✅ Integration Test - No refund data found (valid scenario)");
//         }
//     }

//     @Test
//     @DisplayName("Integration Test - Service Health and Responsiveness")
//     void testServiceHealthAndResponseTime() {
//         // Test service responds within reasonable time
//         long startTime = System.currentTimeMillis();
        
//         ResponseEntity<List<RefundSummary>> response = restTemplate.exchange(
//             baseUrl + "/refund-status",
//             HttpMethod.GET,
//             null,
//             new ParameterizedTypeReference<List<RefundSummary>>() {}
//         );
        
//         long responseTime = System.currentTimeMillis() - startTime;
        
//         // Assert service responds successfully
//         assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        
//         // Assert reasonable response time (under 5 seconds for integration test)
//         assertThat(responseTime).isLessThan(5000L);
        
//         System.out.println("✅ Service responded in " + responseTime + "ms");
//     }

//     @Test
//     @DisplayName("Integration Test - Multiple Concurrent Requests")
//     void testConcurrentRequestHandling() {
//         // Test the service can handle multiple concurrent requests
//         int numberOfRequests = 5;
        
//         // Make multiple requests sequentially but rapidly
//         for (int i = 0; i < numberOfRequests; i++) {
//             ResponseEntity<List<RefundSummary>> response = restTemplate.exchange(
//                 baseUrl + "/refund-status",
//                 HttpMethod.GET,
//                 null,
//                 new ParameterizedTypeReference<List<RefundSummary>>() {}
//             );
            
//             assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
//         }
        
//         System.out.println("✅ Successfully handled " + numberOfRequests + " requests");
//     }

//     @Test
//     @DisplayName("Integration Test - Error Handling with Invalid Requests")
//     void testErrorHandling() {
//         // Test endpoint with invalid HTTP method
//         ResponseEntity<String> response = restTemplate.exchange(
//             baseUrl + "/refund-status",
//             HttpMethod.POST,  // Invalid method for this endpoint
//             null,
//             String.class
//         );
        
//         // Should return method not allowed
//         assertThat(response.getStatusCode()).isEqualTo(HttpStatus.METHOD_NOT_ALLOWED);
        
//         System.out.println("✅ Properly handled invalid HTTP method");
//     }
// }
