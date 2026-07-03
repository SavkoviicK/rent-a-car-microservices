package com.rzk.reservation_service.feign;

import java.math.BigDecimal;

public class VehicleResponse {

    private Long id;
    private BigDecimal pricePerDay;
    private String status;

    public Long getId() {
        return id;
    }

    public BigDecimal getPricePerDay() {
        return pricePerDay;
    }

    public String getStatus() {
        return status;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setPricePerDay(BigDecimal pricePerDay) {
        this.pricePerDay = pricePerDay;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
