package com.rzk.vehicle_service.controller;

import com.rzk.vehicle_service.dto.VehicleCreateRequest;
import com.rzk.vehicle_service.model.Vehicle;
import com.rzk.vehicle_service.model.VehicleStatus;
import com.rzk.vehicle_service.security.RoleGuard;
import com.rzk.vehicle_service.service.VehicleService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/vehicles")
@RequiredArgsConstructor
@Validated
public class VehicleController {

    private final VehicleService vehicleService;
    private final RoleGuard roleGuard;

    // ADMIN
    @PostMapping
    public Vehicle addVehicle(@Valid @RequestBody VehicleCreateRequest request, HttpServletRequest httpRequest) {
        roleGuard.requireAdmin(httpRequest);
        return vehicleService.addVehicle(request);
    }

    // PUBLIC/USER
    @GetMapping
    public List<Vehicle> getAllVehicles() {
        return vehicleService.getAllVehicles();
    }

    // PUBLIC/USER
    @GetMapping("/available")
    public List<Vehicle> getAvailableVehicles() {
        return vehicleService.getAvailableVehicles();
    }

    // ADMIN
    @PutMapping("/{id}/status")
    public Vehicle updateStatus(@PathVariable Long id,
                                @RequestParam VehicleStatus status,
                                HttpServletRequest request) {

        if (status == VehicleStatus.RESERVED || status == VehicleStatus.AVAILABLE) {
            roleGuard.requireUserOrAdmin(request);
        } else {
            roleGuard.requireAdmin(request);
        }

        return vehicleService.updateStatus(id, status);
    }


    // PUBLIC/USER
    @GetMapping("/{id}")
    public Vehicle getById(@PathVariable Long id) {
        return vehicleService.getById(id);
    }
}
