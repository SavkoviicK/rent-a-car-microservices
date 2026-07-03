package com.rzk.vehicle_service.service;

import com.rzk.vehicle_service.dto.VehicleCreateRequest;
import com.rzk.vehicle_service.model.Category;
import com.rzk.vehicle_service.model.Location;
import com.rzk.vehicle_service.model.Vehicle;
import com.rzk.vehicle_service.model.VehicleStatus;
import com.rzk.vehicle_service.repository.CategoryRepository;
import com.rzk.vehicle_service.repository.LocationRepository;
import com.rzk.vehicle_service.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VehicleService {

    private final VehicleRepository vehicleRepository;
    private final CategoryRepository categoryRepository;
    private final LocationRepository locationRepository;

    public Vehicle addVehicle(VehicleCreateRequest request) {

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));

        Location location = locationRepository.findById(request.getLocationId())
                .orElseThrow(() -> new IllegalArgumentException("Location not found"));

        Vehicle vehicle = new Vehicle();
        vehicle.setBrand(request.getBrand());
        vehicle.setModel(request.getModel());
        vehicle.setPricePerDay(request.getPricePerDay());
        vehicle.setStatus(VehicleStatus.AVAILABLE);
        vehicle.setCategory(category);
        vehicle.setLocation(location);

        return vehicleRepository.save(vehicle);
    }

    public List<Vehicle> getAllVehicles() {
        return vehicleRepository.findAll();
    }

    public List<Vehicle> getAvailableVehicles() {
        return vehicleRepository.findAllByStatus(VehicleStatus.AVAILABLE);
    }

    public Vehicle updateStatus(Long id, VehicleStatus status) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Vehicle not found" + id));

        vehicle.setStatus(status);
        return vehicleRepository.save(vehicle);
    }

    public Vehicle getById(Long id) {
        return vehicleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Vehicle not found: " + id));
    }
}
