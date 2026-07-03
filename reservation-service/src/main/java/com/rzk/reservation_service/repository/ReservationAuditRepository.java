package com.rzk.reservation_service.repository;

import com.rzk.reservation_service.model.ReservationAudit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationAuditRepository extends JpaRepository<ReservationAudit, Long> {
}