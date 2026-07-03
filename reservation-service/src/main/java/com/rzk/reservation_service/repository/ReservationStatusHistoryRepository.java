package com.rzk.reservation_service.repository;

import com.rzk.reservation_service.model.ReservationStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReservationStatusHistoryRepository
        extends JpaRepository<ReservationStatusHistory, Long> {

    List<ReservationStatusHistory> findByReservationId(Long reservationId);
    List<ReservationStatusHistory> findByReservationIdOrderByChangedAtAsc(Long reservationId);

}
