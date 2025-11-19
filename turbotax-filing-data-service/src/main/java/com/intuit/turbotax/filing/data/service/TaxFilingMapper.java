package com.intuit.turbotax.filing.data.service;

import org.springframework.stereotype.Component;
import com.intuit.turbotax.api.model.TaxFiling;
import com.intuit.turbotax.filing.data.repository.TaxFilingEntity;

@Component
public class TaxFilingMapper {
    public TaxFiling entityToApi(TaxFilingEntity entity) {  
        if (entity == null) {
            return null;
        }

        return new TaxFiling(
                entity.getFilingId(),
                entity.getTrackingId(),
                entity.getJurisdiction(),
                entity.getUserId(),
                entity.getTaxYear(),
                entity.getFilingDate(),
                entity.getRefundAmount(),
                entity.getDisbursementMethod()
        );
    }
}
