package com.rzk.reservation_service.controller;

import com.rzk.reservation_service.dto.CreateReservationRequest;
import com.rzk.reservation_service.model.Reservation;
import com.rzk.reservation_service.service.ReservationService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @PostMapping
    public Reservation create(
            @RequestHeader(value = "X-User-Email", required = false) String email,
            @RequestHeader(value = "X-User-Roles", required = false) String roles,
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @Valid @RequestBody CreateReservationRequest req
    ) {
        return reservationService.create(email, roles, userId, req);
    }

    @GetMapping("/{id}")
    public Reservation getById(@PathVariable Long id) {
        return reservationService.getById(id);
    }

    @GetMapping("/user/{userId}")
    public List<Reservation> getByUserId(@PathVariable Long userId) {
        return reservationService.getByUserId(userId);
    }

    @PutMapping("/{id}/cancel")
    public Reservation cancel(
            @RequestHeader(value = "X-User-Email", required = false) String email,
            @RequestHeader(value = "X-User-Roles", required = false) String roles,
            @PathVariable Long id
    ) {
        return reservationService.cancel(email, roles, id);
    }

}