package com.intuit.turbotax.refundstatus.domain.refund;

public enum RefundCanonicalStatus {

    FILED(false),
    ACCEPTED(false),
    PROCESSING(false),
    SENT_TO_BANK(false),
    DEPOSITED(true),
    DELAYED(false),
    ERROR(false);

    private final boolean isFinal;

    RefundCanonicalStatus(boolean isFinal) {
        this.isFinal = isFinal;
    }

    public boolean isFinal() {
        return isFinal;
    }
}
