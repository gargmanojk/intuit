package com.intuit.turbotax.domainmodel.dto;

import java.util.Collections;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefundStatusDto {
    private Integer taxYear;
    private List<RefundDetailsDto> refunds;
    private boolean filingFound;

    public static RefundStatusDto noFilingFound() {
        return RefundStatusDto.builder()
                .taxYear(null)
                .refunds(Collections.emptyList())
                .filingFound(false)
                .build();
    }

    public static RefundStatusDto withRefunds(Integer taxYear, List<RefundDetailsDto> refunds) {
        return RefundStatusDto.builder()
                .taxYear(taxYear)
                .refunds(refunds != null ? refunds : Collections.emptyList())
                .filingFound(true)
                .build();
    }

    public boolean isFilingFound() {
        return filingFound;
    }
}

