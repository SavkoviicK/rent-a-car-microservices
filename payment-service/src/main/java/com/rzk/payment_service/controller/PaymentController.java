package com.rzk.payment_service.controller;

import com.rzk.payment_service.model.Account;
import com.rzk.payment_service.dto.ChargeRequest;
import com.rzk.payment_service.dto.ChargeResponse;
import com.rzk.payment_service.repository.PaymentTransactionRepository;
import com.rzk.payment_service.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;
    private final PaymentTransactionRepository txRepository;

    public PaymentController(PaymentService paymentService,
                             PaymentTransactionRepository txRepository) {
        this.paymentService = paymentService;
        this.txRepository = txRepository;
    }

    // POST /api/payments/charge - naplata
    @PostMapping("/charge")
    public ChargeResponse charge(@Valid @RequestBody ChargeRequest request) {
        return paymentService.charge(request);
    }

    // GET /api/payments/account/{userId} - racun
    @GetMapping("/account/{userId}")
    public Account account(@PathVariable Long userId) {
        return paymentService.getAccount(userId);
    }

    // GET /api/payments/transactions/{userId} - transakcije
    @GetMapping("/transactions/{userId}")
    public Object transactions(@PathVariable Long userId) {
        return txRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }
}
