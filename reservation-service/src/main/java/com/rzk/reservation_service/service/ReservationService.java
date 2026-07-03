package com.rzk.reservation_service.service;

import com.rzk.reservation_service.dto.ChargeRequest;
import com.rzk.reservation_service.dto.ChargeResponse;
import com.rzk.reservation_service.dto.CreateReservationRequest;
import com.rzk.reservation_service.feign.PaymentProxy;
import com.rzk.reservation_service.feign.VehicleProxy;
import com.rzk.reservation_service.feign.VehicleResponse;
import com.rzk.reservation_service.model.Reservation;
import com.rzk.reservation_service.model.ReservationStatus;
import com.rzk.reservation_service.model.ReservationStatusHistory;
import com.rzk.reservation_service.repository.ReservationRepository;
import com.rzk.reservation_service.repository.ReservationStatusHistoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import com.rzk.reservation_service.model.ReservationAudit;
import com.rzk.reservation_service.repository.ReservationAuditRepository;
import com.rzk.reservation_service.exception.ResourceNotFoundException;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final VehicleProxy vehicleProxy;
    private final ReservationStatusHistoryRepository historyRepository;
    private final PaymentProxy paymentProxy;
    private final ReservationAuditRepository auditRepository;

    public ReservationService(ReservationRepository reservationRepository,
                              VehicleProxy vehicleProxy,
                              ReservationStatusHistoryRepository historyRepository,
                              PaymentProxy paymentProxy, ReservationAuditRepository auditRepository) {
        this.reservationRepository = reservationRepository;
        this.vehicleProxy = vehicleProxy;
        this.historyRepository = historyRepository;
        this.paymentProxy = paymentProxy;
        this.auditRepository = auditRepository;
    }

    @Transactional(readOnly = true)
    public Reservation getById(Long id) {
        return reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found: " + id));
    }

    @Transactional
    public Reservation create(String email, String roles, Long headerUserId, CreateReservationRequest req) {

        // 0) dobijamo id
        Long resolvedUserId = (headerUserId != null) ? headerUserId : req.getUserId();
        if (resolvedUserId == null) {
            throw new IllegalArgumentException("Missing userId (send X-User-Id header or request.userId)");
        }

        if (req.getTo().isBefore(req.getFrom())) {
            throw new IllegalArgumentException("'to' must be after 'from'");
        }

        boolean overlapExists =
                reservationRepository.existsByVehicleIdAndStatusAndFromDateLessThanAndToDateGreaterThan(
                        req.getVehicleId(),
                        ReservationStatus.CONFIRMED,
                        req.getTo(),
                        req.getFrom()
                );

        if (overlapExists) {
            throw new IllegalStateException("Vehicle already reserved for this period");
        }

        VehicleResponse vehicle = vehicleProxy.getVehicleById(req.getVehicleId());

        if (!"AVAILABLE".equals(vehicle.getStatus())) {
            throw new IllegalStateException("Vehicle is not available");
        }

        long days = java.time.temporal.ChronoUnit.DAYS.between(req.getFrom(), req.getTo());
        if (days <= 0) {
            throw new IllegalArgumentException("Invalid reservation period");
        }

        BigDecimal totalPrice = vehicle.getPricePerDay().multiply(BigDecimal.valueOf(days));

        // 1) Kreiramo rezervaciju -> josuvek nije reserved
        Reservation r = new Reservation();
        r.setUserId(resolvedUserId);
        r.setVehicleId(req.getVehicleId());
        r.setFromDate(req.getFrom());
        r.setToDate(req.getTo());
        r.setPrice(totalPrice);
        r.setStatus(ReservationStatus.CONFIRMED);

        Reservation saved = reservationRepository.save(r);

        ReservationStatusHistory history = new ReservationStatusHistory();
        history.setReservation(saved);
        history.setOldStatus(null);
        history.setNewStatus(ReservationStatus.CONFIRMED);
        history.setChangedAt(LocalDateTime.now());
        historyRepository.save(history);

        ReservationAudit audit = new ReservationAudit();
        audit.setReservationId(saved.getId());
        audit.setAction("CREATED");
        audit.setCreatedAt(LocalDateTime.now());
        auditRepository.save(audit);

        // 2) payment posle save-a (dobijam reservationId)
        ChargeRequest chargeReq = new ChargeRequest();
        chargeReq.setUserId(resolvedUserId);
        chargeReq.setReservationId(saved.getId());
        chargeReq.setAmount(totalPrice);

        ChargeResponse pay = paymentProxy.charge(chargeReq);

        if (pay == null || pay.getStatus() == null || !"SUCCESS".equalsIgnoreCase(pay.getStatus())) {
            throw new IllegalStateException("Payment failed");
        }

        // 3) Tek kad je payment SUCCESS, zakljucamo vozilo (RESERVED)
        vehicleProxy.updateStatus(email, roles, req.getVehicleId(), "RESERVED");

        return saved;
    }

    @Transactional(readOnly = true)
    public List<Reservation> getByUserId(Long userId) {
        return reservationRepository.findByUserId(userId);
    }

    @Transactional
    public Reservation cancel(String email, String roles, Long id) {

        Reservation r = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found: " + id));

        if (r.getStatus() == ReservationStatus.CANCELED) {
            return r;
        }

        ReservationStatus old = r.getStatus();
        r.setStatus(ReservationStatus.CANCELED);

        Reservation saved = reservationRepository.save(r);

        ReservationStatusHistory history = new ReservationStatusHistory();
        history.setReservation(saved);
        history.setOldStatus(old);
        history.setNewStatus(ReservationStatus.CANCELED);
        history.setChangedAt(LocalDateTime.now());
        historyRepository.save(history);

        ReservationAudit audit = new ReservationAudit();
        audit.setReservationId(saved.getId());
        audit.setAction("CANCELED");
        audit.setCreatedAt(LocalDateTime.now());
        auditRepository.save(audit);

        vehicleProxy.updateStatus(email, roles, r.getVehicleId(), "AVAILABLE");

        return saved;
    }

    @Transactional(readOnly = true)
    public List<ReservationStatusHistory> getHistory(Long reservationId) {
        return historyRepository.findByReservationIdOrderByChangedAtAsc(reservationId);
    }

}