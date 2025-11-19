./gradlew build
./gradlew test
./gradlew bootRun

## Curl Commands
* curl -H "X-USER-ID: 123" localhost:7001/filings -s | jq .
* curl -H "X-USER-ID: 123" localhost:7001/filings/202510001 -s | jq .
* curl -H "X-USER-ID: 123" localhost:7002/aggregate-status/202510001 -s | jq .
* curl -H "X-USER-ID: 123" localhost:7003/refund-eta/202510001 -s | jq .
* curl -H "X-USER-ID: 123" localhost:8001/return-status -s | jq .
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