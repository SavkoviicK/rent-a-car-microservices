package com.rzk.vehicle_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VehicleCreateRequest {

    @NotBlank(message = "Brand is required")
    private String brand;

    @NotBlank(message = "Model is required")
    private String model;

    @NotNull(message = "Price per day is required")
    @Positive(message = "Price per day must be positive")
    private Double pricePerDay;

    @NotNull(message = "Category id is required")
    private Long categoryId;

    @NotNull(message = "Location id is required")
    private Long locationId;

}

