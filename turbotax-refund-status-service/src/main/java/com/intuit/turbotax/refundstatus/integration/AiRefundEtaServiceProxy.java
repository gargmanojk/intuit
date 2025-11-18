package com.intuit.turbotax.refundstatus.integration;

import com.intuit.turbotax.contract.AiFeatures;
import com.intuit.turbotax.contract.EtaRefundInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Optional;

@Component
public class AiRefundEtaServiceProxy implements AiRefundEtaService {
    private static final Logger LOG = LoggerFactory.getLogger(AiRefundEtaServiceProxy.class);

    private final RestTemplate restTemplate;
    private final String serviceUrl;
  
    public AiRefundEtaServiceProxy (RestTemplate restTemplate,
            @Value("${app.ai-refund-eta-service.host}") String serviceHost,
            @Value("${app.ai-refund-eta-service.port}") int servicePort) {
        this.restTemplate = restTemplate;
        this.serviceUrl = "http://" + serviceHost + ":" + servicePort + "/refund-eta";
    }

    @Override
    public Optional<EtaRefundInfo> predictEta(AiFeatures aiFeatures) {
        try {
            UriComponentsBuilder ub = UriComponentsBuilder.fromHttpUrl(serviceUrl)
                    .queryParam("taxYear", aiFeatures.getTaxYear());

            if (aiFeatures.getFilingDate() != null) {
                ub.queryParam("filingDate", aiFeatures.getFilingDate().toString());
            }
            if (aiFeatures.getRefundAmount() != null) {
                ub.queryParam("refundAmount", aiFeatures.getRefundAmount().toString());
            }
            if (aiFeatures.getReturnStatus() != null) {
                ub.queryParam("returnStatus", aiFeatures.getReturnStatus().name());
            }
            if (aiFeatures.getDisbursementMethod() != null) {
                ub.queryParam("disbursementMethod", aiFeatures.getDisbursementMethod());
            }
            if (aiFeatures.getJurisdiction() != null) {
                ub.queryParam("jurisdiction", aiFeatures.getJurisdiction().name());
            }   

            String uri = ub.build().toUriString();
            EtaRefundInfo resp = restTemplate.getForObject(uri, EtaRefundInfo.class);
            return Optional.ofNullable(resp);
        } catch (Exception e) {
            LOG.warn("Failed to GET AI refund ETA service at {}: {}", serviceUrl, e.getMessage());
            return Optional.empty();
        }
    }
}
