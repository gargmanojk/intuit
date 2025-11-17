package com.intuit.turbotax.refundstatus.integration;

import com.intuit.turbotax.domainmodel.EtaRefundRequest;
import com.intuit.turbotax.domainmodel.EtaRefundInfo;

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
    public Optional<EtaRefundInfo> predictEta(EtaRefundRequest req) {
        try {
            UriComponentsBuilder ub = UriComponentsBuilder.fromHttpUrl(serviceUrl)
                    .queryParam("taxYear", req.getTaxYear());

            if (req.getFilingDate() != null) {
                ub.queryParam("filingDate", req.getFilingDate().toString());
            }
            if (req.getRefundAmount() != null) {
                ub.queryParam("refundAmount", req.getRefundAmount().toString());
            }
            if (req.getReturnStatus() != null) {
                ub.queryParam("returnStatus", req.getReturnStatus().name());
            }
            if (req.getDisbursementMethod() != null) {
                ub.queryParam("disbursementMethod", req.getDisbursementMethod());
            }
            if (req.getJurisdiction() != null) {
                ub.queryParam("jurisdiction", req.getJurisdiction().name());
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
