package com.intuit.turbotax.refund.aggregation.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;

import com.intuit.turbotax.refund.aggregation.repository.RefundStatusRepository;

@ExtendWith(MockitoExtension.class)
class RefundAggregationHealthIndicatorTest {

    @Mock
    private RefundStatusRepository repository;

    @Test
    void health_ShouldReturnUp_WhenAllComponentsAreHealthy() {
        // Given
        RefundAggregationHealthIndicator healthIndicator = new RefundAggregationHealthIndicator(repository);
        when(repository.findByFilingId(999999999)).thenReturn(Optional.empty()); // Repository is accessible

        // When
        Health health = healthIndicator.health();

        // Then
        assertThat(health.getStatus()).isEqualTo(Status.UP);
        assertThat(health.getDetails()).containsKey("repository");
        assertThat(health.getDetails()).containsKey("externalServices");
        assertThat(health.getDetails()).containsKey("status");
        assertThat(health.getDetails().get("repository")).isEqualTo("UP");
        assertThat(health.getDetails().get("externalServices")).isEqualTo("UP");
    }

    @Test
    void health_ShouldReturnDown_WhenRepositoryFails() {
        // Given
        RefundAggregationHealthIndicator healthIndicator = new RefundAggregationHealthIndicator(repository);
        when(repository.findByFilingId(999999999)).thenThrow(new RuntimeException("Database connection failed"));

        // When
        Health health = healthIndicator.health();

        // Then
        assertThat(health.getStatus()).isEqualTo(Status.DOWN);
        assertThat(health.getDetails()).containsKey("repository");
        assertThat(health.getDetails()).containsKey("externalServices");
        assertThat(health.getDetails()).containsKey("status");
        assertThat(health.getDetails().get("repository")).isEqualTo("DOWN");
        assertThat(health.getDetails().get("externalServices")).isEqualTo("UP");
        assertThat(health.getDetails().get("status")).isEqualTo("Some components are unhealthy");
    }

    @Test
    void health_ShouldReturnDown_WhenExceptionOccurs() {
        // Given
        RefundAggregationHealthIndicator healthIndicator = new RefundAggregationHealthIndicator(repository);
        when(repository.findByFilingId(999999999)).thenThrow(new RuntimeException("Unexpected error"));

        // When
        Health health = healthIndicator.health();

        // Then
        assertThat(health.getStatus()).isEqualTo(Status.DOWN);
        assertThat(health.getDetails()).containsKey("repository");
        assertThat(health.getDetails()).containsKey("externalServices");
        assertThat(health.getDetails()).containsKey("status");
        assertThat(health.getDetails().get("repository")).isEqualTo("DOWN");
        assertThat(health.getDetails().get("externalServices")).isEqualTo("UP");
        assertThat(health.getDetails().get("status")).isEqualTo("Some components are unhealthy");
    }
}