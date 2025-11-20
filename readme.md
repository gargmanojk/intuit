# TurboTax Microservices

## Recent Changes (November 20, 2025)
- **Cache Layer Removed**: Removed TTL-based caching from both `turbotax-refund-prediction-service` and `turbotax-refund-aggregation-service`
  - Simplified architecture for real-time processing without caching overhead
  - Services now provide fresh data on every request
  - Removed `CacheConfig.java` files and cache dependencies
  - Updated service implementations to remove caching logic

## Quick Start
```bash
./gradlew build
./gradlew test
./gradlew bootRun
```

## Curl Commands
* curl -H "X-USER-ID: 123" localhost:7001/filings -s | jq .
* curl -H "X-USER-ID: 123" localhost:7001/filings/202510001 -s | jq .
* curl -H "X-USER-ID: 123" localhost:7002/aggregate-status/202510001 -s | jq .
* curl -H "X-USER-ID: 123" localhost:7003/refund-eta/202510001 -s | jq .
* curl -H "X-USER-ID: 123" localhost:8001/return-status -s | jq .
* 

## DOTO
* Publish tax-return-filed-event
* tax-return-filed-event -> refund-aggregation-service (update its DB)
* Publish refund-aggregation-service -> refund-status-update-event (publish)
* refund-status-update-event -> refund-prediction-service (subscribes to update training data)
* refund-status-update-event -> refund-query-service (subscribes to update its Cache)
* refund-status-update-event -> customer-notification-service (subscribes to notify custmomer)
* model-training-job (nightly batch to retrain prediction model)
*

<details>
<summary>Gradle Service Tasks</summary>

## Individual Service Tasks

### Start Individual Services (Background)
```bash
./gradlew startFilingDataService      # Port 7001  
./gradlew startRefundAggregationService # Port 7002
./gradlew startRefundPredictionService  # Port 7003
./gradlew startRefundQueryService      # Port 8001
```

### Start All Services at Once
```bash
./gradlew startAllServices
```

### Stop All Services
```bash
./gradlew stopAllServices
```

### Check Service Health
```bash
./gradlew checkAllServices
```

### Service URLs
- Filing Data: http://localhost:7001
- Refund Aggregation: http://localhost:7002
- Refund Prediction: http://localhost:7003
- Refund Query: http://localhost:8001

### Service Logs
All service logs are stored in the `logs/` directory:
- Filing Query: `logs/filing-query-service.log` 
- Refund Aggregation: `logs/refund-aggregation-service.log`
- Refund Prediction: `logs/refund-prediction-service.log`
- Refund Query: `logs/refund-query-service.log`

```bash
# View logs
tail -f logs/filing-query-service.log
tail -f logs/refund-query-service.log

# View all service logs
tail -f logs/*.log
```

</details>