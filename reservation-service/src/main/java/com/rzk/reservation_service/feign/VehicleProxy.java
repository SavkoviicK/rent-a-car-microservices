package com.rzk.reservation_service.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "vehicle-service")
public interface VehicleProxy {

    @GetMapping("/vehicles/{id}")
    VehicleResponse getVehicleById(@PathVariable Long id);

    @PutMapping("/vehicles/{id}/status")
    void updateStatus(
            @RequestHeader(value = "X-User-Email", required = false) String email,
            @RequestHeader(value = "X-User-Roles", required = false) String roles,
            @PathVariable("id") Long id,
            @RequestParam("status") String status
    );
}
