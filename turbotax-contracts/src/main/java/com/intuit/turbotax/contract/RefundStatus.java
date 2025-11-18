package com.intuit.turbotax.contract.data;

public enum RefundStatus {
    NO_FILING(false),
    FILED(false),
    ACCEPTED(false),
    PROCESSING(false),
    SENT_TO_BANK(false),
    DEPOSITED(true),
    DELAYED(false),
    ERROR(false);

    private final boolean isFinal;

    RefundStatus(boolean isFinal) {
        this.isFinal = isFinal;
    }

    public boolean isFinal() {
        return isFinal;
    }
}
