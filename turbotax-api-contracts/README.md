# TurboTax API Contracts

This module contains the shared API contracts and interfaces for the TurboTax microservices platform. It defines the common interfaces, models, and contracts that are used across all services to ensure consistency and maintainability.

## Overview

The API contracts module serves as the central repository for:
- Service interfaces and contracts
- Shared data models and DTOs
- Common types and enumerations
- API versioning definitions

## Architecture

### Package Structure

```
com.intuit.turbotax.api.v1/
├── common/          # Shared common interfaces and models
├── external/        # External service integration contracts
├── filing/          # Tax filing related interfaces and models
└── refund/          # Refund processing interfaces and models
```

### Key Components

#### Common Package (`v1.common`)
- Shared utilities and base interfaces
- Common data types used across domains

#### External Package (`v1.external`)
- External service client interfaces
- Third-party integration contracts (IRS, banking, etc.)

#### Filing Package (`v1.filing`)
- Tax filing query interfaces
- Filing data models and DTOs

#### Refund Package (`v1.refund`)
- Refund status and aggregation interfaces
- Refund data models and processing contracts

## Versioning

This module uses semantic versioning with API versioning:
- **v1**: Current stable API version
- Future versions will be added as `v2`, `v3`, etc. for breaking changes

## Dependencies

This module has minimal dependencies and is designed to be lightweight:
- Java 24
- Spring Boot 3.5.0 (for basic webflux support)
- JUnit Platform (for testing)

## Usage

### Adding to Service Dependencies

All TurboTax microservices depend on this contracts module to ensure API consistency:

```gradle
dependencies {
    implementation project(':turbotax-api-contracts')
}
```

### Implementing Contracts

Services implement the interfaces defined in this module:

```java
@Service
public class RefundStatusQueryServiceImpl implements RefundStatusQueryService {
    // Implementation here
}
```

## Development Guidelines

### Interface Design
- Keep interfaces focused on single responsibilities
- Use clear, descriptive method names
- Document all public methods with JavaDoc
- Avoid implementation details in interfaces

### Model Design
- Use immutable objects where possible
- Include proper validation annotations
- Follow consistent naming conventions
- Document all fields and their purposes

### Testing
- Include comprehensive unit tests for all contracts
- Test serialization/deserialization of models
- Validate interface contracts with integration tests

## Build and Test

```bash
# Build the contracts module
./gradlew :turbotax-api-contracts:build

# Run tests
./gradlew :turbotax-api-contracts:test

# Publish to local repository (if needed)
./gradlew :turbotax-api-contracts:publishToMavenLocal
```

## Related Modules

- **turbotax-filing-query-service**: Implements filing-related contracts
- **turbotax-refund-aggregation-service**: Implements refund aggregation contracts
- **turbotax-refund-query-service**: Implements refund query contracts
- **turbotax-ml-training**: Provides ML service contracts (separate module)

## Contributing

When adding new contracts:

1. Follow the domain-driven package structure
2. Add comprehensive JavaDoc documentation
3. Include unit tests for new interfaces
4. Update this README if adding new packages
5. Ensure backward compatibility within major versions

## API Evolution

- **Patch versions** (1.0.x): Bug fixes, documentation updates
- **Minor versions** (1.x.0): New features, backward compatible
- **Major versions** (x.0.0): Breaking changes, new API versions

For breaking changes, create new versioned packages (e.g., `v2`) rather than modifying existing `v1` interfaces.