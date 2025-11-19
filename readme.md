./gradlew build
./gradlew test
./gradlew bootRun

curl localhost:8080/refunds/latest-status -s | jq .

<details>
<summary>Gradle Service Tasks</summary>

## Individual Service Tasks

### Start Individual Services (Background)
```bash
./gradlew startFilingMetadataService    # Port 7001  
./gradlew startRefundAggregateService   # Port 7002
./gradlew startAiRefundEtaService       # Port 7003
./gradlew startRefundStatusService      # Port 8001
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
- Filing Metadata: http://localhost:7001
- Refund Aggregate: http://localhost:7002
- AI Refund ETA: http://localhost:7003
- Refund Status: http://localhost:8001

### Service Logs
All service logs are stored in the `logs/` directory:
- Filing Metadata: `logs/filing-metadata-service.log` 
- Refund Aggregate: `logs/refund-aggregate-service.log`
- AI Refund ETA: `logs/ai-refund-eta-service.log`
- Refund Status: `logs/refund-status-service.log`

```bash
# View logs
tail -f logs/filing-metadata-service.log
tail -f logs/refund-status-service.log

# View all service logs
tail -f logs/*.log
```

</details>