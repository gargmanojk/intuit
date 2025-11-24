# TurboTax Filing Query Service

A Spring Boot WebFlux service for querying tax filing information in the TurboTax microservices architecture.

## Overview

This service provides REST API endpoints to retrieve tax filing data for users. It implements a reactive architecture using Spring WebFlux and includes features like caching, validation, and comprehensive testing.

## Features

- **Reactive WebFlux API**: Non-blocking, reactive endpoints
- **Caching**: Built-in caching for improved performance
- **Validation**: Input validation with custom validators
- **Health Checks**: Spring Boot Actuator health endpoints
- **Comprehensive Testing**: Unit and integration tests with JaCoCo coverage
- **Docker Support**: Container-ready with Gradle build

## Architecture

```
┌─────────────────┐    ┌──────────────────┐    ┌─────────────────┐
│   Controller    │───▶│   Service        │───▶│   Repository    │
│                 │    │   (Business      │    │   (Data Access) │
│ - REST API      │    │    Logic)        │    │                 │
│ - Validation    │    │ - Caching        │    │ - In-Memory DB  │
└─────────────────┘    └──────────────────┘    └─────────────────┘
```

## API Endpoints

### GET /filings
Retrieves all tax filings for a specific user.

**Parameters:**
- `userId` (query parameter): The unique identifier of the user

**Example:**
```bash
curl "http://localhost:7001/filings?userId=user123"
```

Or use the provided `requests.http` file in your IDE for testing.

**Response:**
```json
[
  {
    "filingId": 202410001,
    "userId": "user123",
    "jurisdiction": "FEDERAL",
    "taxYear": 2024,
    "filingDate": "2024-04-15",
    "refundAmount": 2500.00,
    "trackingId": "TRACK-001",
    "disbursementMethod": "ACH",
    "isPaperless": true
  }
]
```

### GET /actuator/health
Health check endpoint provided by Spring Boot Actuator.

## Technology Stack

- **Java 24**: Latest Java version with preview features
- **Spring Boot 3.5.0**: Reactive web framework
- **Spring WebFlux**: Reactive web programming support
- **Spring Cache**: Caching abstraction
- **Spring Validation**: Bean validation
- **Lombok**: Code generation library
- **JUnit 5**: Testing framework
- **JaCoCo**: Code coverage reporting
- **Gradle**: Build tool

## Prerequisites

- Java 24 JDK
- Gradle 8.10.2+ (or use included wrapper)

## Building and Running

### Local Development

1. **Clone the repository:**
   ```bash
   git clone <repository-url>
   cd turbotax-filing-query-service
   ```

2. **Build the project:**
   ```bash
   ./gradlew build
   ```

3. **Run the service:**
   ```bash
   ./gradlew bootRun
   ```

4. **Run tests:**
   ```bash
   ./gradlew test
   ```

5. **Generate test coverage report:**
   ```bash
   ./gradlew jacocoTestReport
   ```

### Using Gradle Wrapper (Recommended)

The project includes Gradle wrapper scripts for consistent builds:

```bash
# On Unix-like systems
./gradlew build

# On Windows
gradlew.bat build
```

### Multi-Service Setup

To run with other TurboTax services:

```bash
# From project root
./gradlew startFilingQueryService
```

## Configuration

### Application Properties

Key configuration in `src/main/resources/application.yml`:

```yaml
server:
  port: 7001

spring:
  application:
    name: turbotax-filing-query-service

logging:
  level:
    com.intuit.turbotax: INFO
```

### Environment Variables

- `JAVA_HOME`: Path to Java 24 JDK
- `GRADLE_USER_HOME`: Custom Gradle home directory (optional)

## Testing

### Running Tests

```bash
# Run all tests
./gradlew test

# Run with coverage
./gradlew test jacocoTestReport

# Run specific test class
./gradlew test --tests "*FilingQueryServiceTest*"
```

### Test Coverage

- Minimum coverage: 80%
- Reports location: `build/reports/jacoco/test/html/index.html`

### Test Categories

- **Unit Tests**: Individual component testing
- **Integration Tests**: End-to-end service testing
- **Controller Tests**: REST API endpoint testing

## Development

### Code Style

- Follow Java coding conventions
- Use Lombok for boilerplate code reduction
- Maintain test coverage above 80%

### Adding New Features

1. Create feature branch
2. Write tests first (TDD approach)
3. Implement functionality
4. Update documentation
5. Ensure all tests pass

### Project Structure

```
src/
├── main/
│   ├── java/com/intuit/turbotax/filing/query/
│   │   ├── controller/          # REST controllers
│   │   ├── service/            # Business logic
│   │   ├── repository/         # Data access layer
│   │   ├── validation/         # Input validation
│   │   └── exception/          # Custom exceptions
│   └── resources/              # Configuration files
└── test/                       # Test sources
    └── java/com/intuit/turbotax/filing/query/
```

## Monitoring

### Health Checks

- **Endpoint**: `GET /actuator/health`
- **Status**: UP/DOWN with details

### Metrics

Spring Boot Actuator provides:
- Application metrics
- JVM information
- Cache statistics
- HTTP request metrics

## Deployment

### Docker

```dockerfile
FROM openjdk:24-jdk-slim
COPY build/libs/*.jar app.jar
EXPOSE 7001
ENTRYPOINT ["java","-jar","/app.jar"]
```

### Kubernetes

The service is designed to run in containerized environments with:
- Health check probes
- Configurable resource limits
- Service discovery integration

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests for new functionality
5. Ensure all tests pass
6. Submit a pull request

## Troubleshooting

### Common Issues

1. **Port already in use**: Change `server.port` in `application.yml`
2. **Java version mismatch**: Ensure Java 24 is installed and `JAVA_HOME` is set
3. **Dependency resolution**: Run `./gradlew clean build` to refresh dependencies

### Logs

Check application logs for detailed error information:
```bash
tail -f logs/application.log
```

## License

[Add license information here]

## Contact

For questions or support, please contact the development team.</content>
<parameter name="filePath">/home/mgarg/projects/intuit/turbotax-filing-query-service/README.md