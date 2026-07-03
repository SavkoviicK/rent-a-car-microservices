package com.rzk.vehicle_service.repository;

import com.rzk.vehicle_service.model.Location;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LocationRepository extends JpaRepository<Location, Long> {
}
