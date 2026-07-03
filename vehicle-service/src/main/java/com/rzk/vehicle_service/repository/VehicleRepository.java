package com.rzk.vehicle_service.repository;

import com.rzk.vehicle_service.model.Vehicle;
import com.rzk.vehicle_service.model.VehicleStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    List<Vehicle> findAllByStatus(VehicleStatus status);
}
