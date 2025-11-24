# TurboTax Refund Aggregation Service

A Spring Boot service that aggregates and provides refund status information from multiple external sources (IRS, State Tax Services, and Money Movement systems).

## Overview

This service acts as an aggregation layer in the TurboTax microservices architecture, collecting refund status data from various external systems and providing a unified API for querying refund statuses. It includes background job processing to keep status information current and implements caching for improved performance.

## Features

- **Multi-Source Aggregation**: Collects data from IRS, State Tax, and Banking systems
- **Background Processing**: Scheduled jobs to update refund statuses
- **Caching**: Performance optimization for frequently accessed data
- **Validation**: Input validation and business rule enforcement
- **Comprehensive Testing**: Unit, integration, and controller tests
- **Health Monitoring**: Spring Boot Actuator endpoints
- **Resilient Design**: Error handling and recovery mechanisms

## Architecture

```
┌─────────────────┐    ┌──────────────────┐    ┌─────────────────┐
│   Controller    │───▶│   Service        │───▶│   Repository    │
│                 │    │   (Business      │    │   (Data Access) │
│ - REST API      │    │    Logic)        │    │                 │
│ - Validation    │    │ - Caching        │    │ - In-Memory DB  │
└─────────────────┘    └──────────────────┘    └─────────────────┘
         │                       │                       │
         ▼                       ▼                       ▼
┌─────────────────┐    ┌──────────────────┐    ┌─────────────────┐
│   Job Layer     │    │   Processors     │    │   External      │
│   (Scheduling)  │    │   (IRS/State)    │    │   Clients       │
└─────────────────┘    └──────────────────┘    └─────────────────┘
```

## API Endpoints

### GET /api/v1/aggregate-status/filings/{filingId}
Retrieves aggregated refund status for a specific filing.

**Parameters:**
- `filingId` (path): The unique filing identifier

**Example:**
```bash
curl "http://localhost:7002/api/v1/aggregate-status/filings/12345"
```

**Response:**
```json
{
  "filingId": 12345,
  "status": "PROCESSING",
  "message": "Your refund is being processed",
  "lastUpdated": "2024-01-15T10:30:00Z",
  "amount": 1500.00
}
```

### GET /actuator/health
Health check endpoint.

## Technology Stack

- **Java 24**: Latest Java version with advanced features
- **Spring Boot 3.5.0**: Reactive web framework
- **Spring WebFlux**: Reactive programming support
- **Spring Cache**: Caching abstraction
- **Spring Scheduling**: Background job processing
- **Lombok**: Code generation
- **JUnit 5**: Comprehensive testing
- **JaCoCo**: Code coverage reporting
- **Gradle**: Build automation

## Prerequisites

- Java 24 JDK
- Gradle 8.10.2+

## Building and Running

### Local Development

1. **Build the service:**
   ```bash
   ./gradlew build
   ```

2. **Run the service:**
   ```bash
   ./gradlew bootRun
   ```

3. **Run tests:**
   ```bash
   ./gradlew test
   ```

4. **Generate coverage report:**
   ```bash
   ./gradlew jacocoTestReport
   ```

### Multi-Service Setup

To run with other TurboTax services:

```bash
# From project root
./gradlew startRefundAggregationService
```

## Configuration

### Application Properties

Key configuration in `application.yml`:

```yaml
refund:
  aggregation:
    update-interval: PT30M      # Status update frequency
    max-retry-attempts: 3       # External service retry attempts
    retry-delay: PT5S          # Delay between retries
    enable-circuit-breaker: true
    external-service-timeout: PT30S

spring:
  cache:
    type: simple               # In-memory cache
```

### External Service Configuration

The service integrates with:
- **IRS Client**: Federal tax refund status
- **State Tax Client**: State-specific refund status
- **Money Movement Client**: Banking transaction status

## Testing

### Test Categories

```bash
# Run all tests
./gradlew test

# Run specific test categories
./gradlew test --tests "*ControllerTest*"
./gradlew test --tests "*ServiceTest*"
./gradlew test --tests "*RepositoryTest*"
./gradlew test --tests "*IntegrationTest*"
```

### Code Coverage

- **Target Coverage**: 80% minimum
- **Reports**: `build/reports/jacoco/test/html/index.html`

### Test Structure

```
src/test/java/
├── controller/           # REST API tests
├── service/             # Business logic tests
├── repository/          # Data access tests
├── validation/          # Input validation tests
├── job/                 # Background job tests
└── integration/         # End-to-end tests
```

## Background Processing

### Status Update Job

- **Frequency**: Configurable (default: 30 minutes)
- **Scope**: Processes all active filings (non-final status)
- **Sources**: IRS, State Tax, and Banking systems
- **Error Handling**: Continues processing other filings on individual failures

### Processing Flow

1. Retrieve active filing IDs from repository
2. For each filing, determine jurisdiction
3. Call appropriate external service (IRS/State/Bank)
4. Update local repository with new status
5. Generate user-friendly status messages

## Monitoring

### Health Checks

- **Database Connectivity**: Repository access
- **External Services**: Client availability
- **Cache Health**: Cache manager status
- **Job Status**: Background processing health

### Metrics

- **Request Metrics**: API call statistics
- **Cache Hit/Miss**: Caching performance
- **Job Execution**: Background processing metrics
- **Error Rates**: Failure monitoring

## Development

### Code Organization

```
src/main/java/com/intuit/turbotax/refund/aggregation/
├── controller/          # REST controllers
├── service/            # Business logic services
├── repository/         # Data access layer
├── validation/         # Input validation
├── exception/          # Custom exceptions
├── job/                # Background jobs
│   └── processors/     # Job processors
├── client/             # External service clients
├── config/             # Configuration classes
└── mapper/             # Data mappers
```

### Adding New Features

1. **External Integration**: Add new client in `client/` package
2. **Business Logic**: Add service in `service/` package
3. **Data Access**: Extend repository interface and implementation
4. **Validation**: Add rules in `validation/` package
5. **Testing**: Add comprehensive tests for all layers

### Error Handling

- **Validation Errors**: `InvalidFilingIdException`
- **External Service Errors**: Logged and handled gracefully
- **Data Access Errors**: Repository-level error handling
- **Job Processing Errors**: Individual filing failures don't stop batch

## Deployment

### Containerization

```dockerfile
FROM openjdk:24-jdk-slim
COPY build/libs/*.jar app.jar
EXPOSE 7002
ENTRYPOINT ["java","-jar","/app.jar"]
```

### Configuration Profiles

- **default**: Development configuration
- **production**: Production-optimized settings
- **test**: Test-specific configuration

## Contributing

1. Follow existing code structure and naming conventions
2. Add comprehensive tests for new features
3. Update documentation for API changes
4. Ensure code coverage remains above 80%
5. Run full test suite before submitting

## Troubleshooting

### Common Issues

1. **External Service Timeouts**: Check network connectivity and service availability
2. **Cache Issues**: Clear cache with `POST /actuator/caches/refundStatus`
3. **Job Not Running**: Verify scheduling configuration
4. **High Memory Usage**: Monitor cache size and consider distributed caching

### Logs

Key log patterns:
```
INFO  - Starting scheduled refund status update job
DEBUG - Getting refund status for filingId=12345
ERROR - Error fetching status for filingId=12345
```

## License

[Add license information]</content>
<parameter name="filePath">/home/mgarg/projects/intuit/turbotax-refund-aggregation-service/README.md