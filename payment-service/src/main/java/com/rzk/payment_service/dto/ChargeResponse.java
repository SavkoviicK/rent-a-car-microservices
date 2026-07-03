package com.rzk.payment_service.dto;

import java.math.BigDecimal;

public class ChargeResponse {
    private Long transactionId;
    private String status;
    private BigDecimal newBalance;

    public ChargeResponse(Long transactionId, String status, BigDecimal newBalance) {
        this.transactionId = transactionId;
        this.status = status;
        this.newBalance = newBalance;
    }

    public Long getTransactionId() { return transactionId; }
    public String getStatus() { return status; }
    public BigDecimal getNewBalance() { return newBalance; }
}
