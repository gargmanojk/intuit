# TurboTax Refund Query Service

A Spring Boot microservice that provides refund status query functionality for the TurboTax application. This service aggregates refund data from multiple sources and provides a unified API for refund status information.

## Architecture

This service follows Clean Architecture principles with the following layers:
- **Presentation Layer**: REST controllers handling HTTP requests
- **Application Layer**: Service adapters coordinating business logic
- **Domain Layer**: Core business entities and interfaces
- **Infrastructure Layer**: External service clients and data access

## Features

- **Reactive WebFlux**: Built with Spring WebFlux for non-blocking I/O
- **Caching**: Spring Cache integration for performance optimization
- **Health Monitoring**: Spring Boot Actuator for service health checks
- **External Integration**: Clients for Filing Query Service, Refund Aggregation Service, and ML prediction service
- **Comprehensive Testing**: Integration tests with WebTestClient

## Prerequisites

- Java 24
- Gradle 8.10.2+
- Access to dependent services:
  - Filing Query Service (port 7001)
  - Refund Aggregation Service (port 7002)
  - Refund Prediction Service (Azure ML endpoint)

## Configuration

The service is configured via `application.yml`:

```yaml
spring:
  application:
    name: turbotax-refund-query-service

server:
  port: 7000

app:
  filing-query-service:
    host: localhost
    port: 7001
  refund-aggregation-service:
    host: localhost
    port: 7002
  refund-prediction-service:
    url: https://refundprediction-ldyuo.eastus2.inference.ml.azure.com/score
```

### Environment Variables

Currently, the service uses hardcoded configuration values. For production deployment, consider externalizing the following:

- `FILING_QUERY_HOST`: Filing Query Service host
- `FILING_QUERY_PORT`: Filing Query Service port  
- `REFUND_AGGREGATION_HOST`: Refund Aggregation Service host
- `REFUND_AGGREGATION_PORT`: Refund Aggregation Service port
- `REFUND_PREDICTION_URL`: ML prediction service URL

## API Documentation

### Get Refund Status

Retrieves the latest refund status for a user by aggregating data from multiple services.

**Endpoint:** `GET /api/v1/refund-status`

**Headers:**
- `X-USER-ID`: Required. The user identifier

**Response:** `List<RefundSummary>`

**Example Request:**
```bash
curl -H "X-User-Id: user123" http://localhost:7000/api/v1/refund-status
```

**Example Response:**
```json
[
  {
    "filingId": 12345,
    "trackingId": "ABC123XYZ",
    "taxYear": 2024,
    "filingDate": "2024-03-15",
    "jurisdiction": "FEDERAL",
    "amount": 1250.00,
    "status": "APPROVED",
    "disbursementMethod": "DIRECT_DEPOSIT",
    "lastUpdatedAt": "2024-03-15T10:30:00Z",
    "etaDate": "2024-04-15",
    "etaConfidence": 0.85,
    "etaWindowDays": 14
  }
]
```

## Running the Service

### Development Mode

1. Ensure dependent services are running
2. Run the service:
   ```bash
   ./gradlew bootRun
   ```

### Production Build

1. Build the JAR:
   ```bash
   ./gradlew build
   ```

2. Run the JAR:
   ```bash
   java -jar build/libs/turbotax-refund-query-service-1.0.0-SNAPSHOT.jar
   ```

### Using Gradle Tasks (from root)

```bash
# Start service in background with debug
./gradlew startRefundQueryService

# Check service status
./gradlew statusRefundQueryService

# Stop service
./gradlew stopRefundQueryService

# Restart service
./gradlew restartRefundQueryService
```

## Testing

### Unit Tests

Run unit tests:
```bash
./gradlew test
```

### Integration Tests

The service includes comprehensive integration and unit tests that:
- Test complete refund query workflows
- Verify error handling for service failures
- Test performance with concurrent requests
- Validate partial data scenarios
- Test ML prediction client functionality

**Total Tests**: 9 (6 integration tests + 3 unit tests)

### Code Coverage

Generate coverage reports:
```bash
./gradlew jacocoTestReport
```

Coverage reports are available in `build/reports/jacoco/`

## Health Checks

### Service Health

Check overall service health:
```bash
curl http://localhost:7000/actuator/health
```

Response:
```json
{
  "status": "UP"
}
```

## Monitoring

The service exposes the following actuator endpoints:

- `/actuator/health` - Health status
- `/actuator/health/{*path}` - Health status for specific components

**Note**: Additional endpoints (info, metrics, caches) can be enabled by adding management configuration to `application.yml`.

## Logging

Logs are configured to output to both console and file:

- **Console**: INFO level for all, DEBUG for `com.intuit.turbotax`
- **File**: `/home/mgarg/projects/intuit/logs/turbotax-refund-query-service.log`

## Development

### Project Structure

```
src/
├── main/
│   ├── java/com/intuit/turbotax/refund/query/
│   │   ├── RefundQueryServiceApplication.java
│   │   ├── controller/
│   │   │   └── RefundQueryController.java
│   │   ├── service/
│   │   │   ├── RefundQueryService.java
│   │   │   └── RefundQueryServiceAdapter.java
│   │   ├── client/
│   │   │   ├── FilingQueryServiceClient.java
│   │   │   ├── RefundDataAggregatorClient.java
│   │   │   └── RefundPredictorClient.java
│   │   ├── config/
│   │   │   ├── CacheConfig.java
│   │   │   ├── GlobalExceptionHandler.java
│   │   │   ├── RefundQueryHealthIndicator.java
│   │   │   ├── RefundQueryProperties.java
│   │   │   └── RestClientConfig.java
│   │   ├── exception/
│   │   ├── mapper/
│   │   └── validation/
│   └── resources/
│       └── application.yml
└── test/
    └── java/com/intuit/turbotax/refund/query/
        ├── RefundQueryServiceApplicationTests.java
        ├── IntegrationTest.java
        ├── client/
        ├── controller/
        ├── exception/
        ├── repository/
        └── validation/
```

### Dependencies

- **Spring Boot 3.5.0**: WebFlux, Actuator, Validation, Cache
- **Project Lombok**: Code generation
- **JUnit 5**: Testing framework
- **Reactor Test**: Reactive testing utilities

### Building

The project uses Gradle with the following key plugins:
- Spring Boot Gradle Plugin
- JaCoCo for code coverage
- JUnit Platform for testing

## Troubleshooting

### Common Issues

1. **Port already in use**: Ensure no other service is running on port 7000
2. **Dependent services unavailable**: Verify Filing Query (7001) and Refund Aggregation (7002) services are running
3. **ML service connection**: Check Azure ML endpoint accessibility

### Debug Mode

Run with debug enabled:
```bash
./gradlew bootRun --debug-jvm
```

Debug port: 5007

## Contributing

1. Follow the existing code structure and Clean Architecture principles
2. Add comprehensive tests for new features
3. Update this README for API changes
4. Ensure code coverage remains above 80%

## License

This project is part of the TurboTax microservices suite.