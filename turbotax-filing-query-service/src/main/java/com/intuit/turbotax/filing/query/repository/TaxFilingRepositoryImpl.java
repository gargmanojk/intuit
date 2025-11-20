package com.intuit.turbotax.filing.query.repository;

import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.time.LocalDate;
import java.util.stream.Stream;

import com.intuit.turbotax.api.model.Jurisdiction;
import com.intuit.turbotax.api.model.PaymentMethod;

@Repository
public class TaxFilingRepositoryImpl implements TaxFilingRepository {
    
    // In-memory storage using concurrent map for thread safety
    private final Map<Integer, TaxFilingEntity> filingStore = new ConcurrentHashMap<>();
    
    public TaxFilingRepositoryImpl() {
        // Initialize with sample data
        initializeData();
    }

    @Override
    public Stream<TaxFilingEntity> findLatestByUserId(String userId) {
        // Return all filings for the specified user
        return filingStore.values().stream()
            .filter(filing -> userId.equals(filing.getUserId()));
    }

    @Override
    public Optional<TaxFilingEntity> findByFilingId(int filingId) {
        TaxFilingEntity filing = filingStore.get(filingId);
        return Optional.ofNullable(filing);
    }
    
    /**
     * Adds a new filing to the in-memory store
     */
    public void save(TaxFilingEntity filing) {
        filingStore.put(filing.getFilingId(), filing);
        System.out.println("Saved filing for filingId: " + filing.getFilingId() + ", user: " + filing.getUserId());
    }
    
    private void initializeData() {
        // Initialize with sample tax filing data
        TaxFilingEntity federal1 = TaxFilingEntity.builder()
            .filingId(202410001)
            .userId("user123")
            .jurisdiction(Jurisdiction.FEDERAL)
            .taxYear(2024)
            .filingDate(LocalDate.of(2024, 4, 15))
            .refundAmount(BigDecimal.valueOf(2500.00))
            .trackingId("IRS-TRACK-202410001")
            .disbursementMethod(PaymentMethod.DIRECT_DEPOSIT)
            .build();
            
        TaxFilingEntity state1 = TaxFilingEntity.builder()
            .filingId(202410002)
            .userId("user123")
            .jurisdiction(Jurisdiction.STATE_CA)
            .taxYear(2024)
            .filingDate(LocalDate.of(2024, 4, 15))
            .refundAmount(BigDecimal.valueOf(350.00))
            .trackingId("CA-TRACK-202410002")
            .disbursementMethod(PaymentMethod.CHECK)
            .build();
            
        TaxFilingEntity federal2 = TaxFilingEntity.builder()
            .filingId(202410003)
            .userId("user456")
            .jurisdiction(Jurisdiction.FEDERAL)
            .taxYear(2024)
            .filingDate(LocalDate.of(2024, 3, 30))
            .refundAmount(BigDecimal.valueOf(4200.75))
            .trackingId("IRS-TRACK-202410003")
            .disbursementMethod(PaymentMethod.DIRECT_DEPOSIT)
            .build();
            
        TaxFilingEntity state2 = TaxFilingEntity.builder()
            .filingId(202410004)
            .userId("user456")
            .jurisdiction(Jurisdiction.STATE_NY)
            .taxYear(2024)
            .filingDate(LocalDate.of(2024, 3, 30))
            .refundAmount(BigDecimal.valueOf(650.25))
            .trackingId("NY-TRACK-202410004")
            .disbursementMethod(PaymentMethod.CARD)
            .build();
            
        TaxFilingEntity federal3 = TaxFilingEntity.builder()
            .filingId(202410005)
            .userId("user789")
            .jurisdiction(Jurisdiction.FEDERAL)
            .taxYear(2024)
            .filingDate(LocalDate.of(2024, 4, 10))
            .refundAmount(BigDecimal.valueOf(1850.50))
            .trackingId("IRS-TRACK-202410005")
            .disbursementMethod(PaymentMethod.CHECK)
            .build();
            
        TaxFilingEntity state3 = TaxFilingEntity.builder()
            .filingId(202410006)
            .userId("user789")
            .jurisdiction(Jurisdiction.STATE_NJ)
            .taxYear(2024)
            .filingDate(LocalDate.of(2024, 4, 10))
            .refundAmount(BigDecimal.valueOf(0.00))
            .trackingId("NJ-TRACK-202410006")
            .disbursementMethod(PaymentMethod.CHECK)
            .build();
        
        // Store all sample data
        filingStore.put(federal1.getFilingId(), federal1);
        filingStore.put(state1.getFilingId(), state1);
        filingStore.put(federal2.getFilingId(), federal2);
        filingStore.put(state2.getFilingId(), state2);
        filingStore.put(federal3.getFilingId(), federal3);
        filingStore.put(state3.getFilingId(), state3);
        
        System.out.println("Initialized " + filingStore.size() + " tax filing records in memory");
    }
}
