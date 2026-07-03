package com.rzk.reservation_service.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
public class ReservationStatusHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "reservation_id")
    private Reservation reservation;

    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    private ReservationStatus oldStatus;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReservationStatus newStatus;

    @Column(nullable = false)
    private LocalDateTime changedAt;
}
