package com.rzk.reservation_service.dto;

import java.math.BigDecimal;

public class ChargeResponse {
    private Long transactionId;
    private String status;
    private BigDecimal newBalance;

    public Long getTransactionId() { return transactionId; }
    public void setTransactionId(Long transactionId) { this.transactionId = transactionId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public BigDecimal getNewBalance() { return newBalance; }
    public void setNewBalance(BigDecimal newBalance) { this.newBalance = newBalance; }
}
