package com.rzk.reservation_service.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class CreateReservationRequest {

    private Long userId;

    @NotNull
    private Long vehicleId;

    @NotNull
    private LocalDate from;

    @NotNull
    private LocalDate to;

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getVehicleId() { return vehicleId; }
    public void setVehicleId(Long vehicleId) { this.vehicleId = vehicleId; }

    public LocalDate getFrom() { return from; }
    public void setFrom(LocalDate from) { this.from = from; }

    public LocalDate getTo() { return to; }
    public void setTo(LocalDate to) { this.to = to; }
}
