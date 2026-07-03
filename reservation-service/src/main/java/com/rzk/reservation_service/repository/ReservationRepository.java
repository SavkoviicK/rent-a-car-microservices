package com.rzk.reservation_service.repository;

import com.rzk.reservation_service.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

import com.rzk.reservation_service.model.ReservationStatus;

import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByUserId(Long userId);

    boolean existsByVehicleIdAndStatusAndFromDateLessThanAndToDateGreaterThan(
            Long vehicleId,
            ReservationStatus status,
            java.time.LocalDate toDate,
            java.time.LocalDate fromDate
    );

}
