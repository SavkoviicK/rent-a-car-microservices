package com.rzk.payment_service.service;

import com.rzk.payment_service.model.Account;
import com.rzk.payment_service.model.PaymentTransaction;
import com.rzk.payment_service.dto.ChargeRequest;
import com.rzk.payment_service.dto.ChargeResponse;
import com.rzk.payment_service.repository.AccountRepository;
import com.rzk.payment_service.repository.PaymentTransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import com.rzk.payment_service.model.PaymentAudit;
import com.rzk.payment_service.repository.PaymentAuditRepository;

@Service
public class PaymentService {

    private final AccountRepository accountRepository;
    private final PaymentTransactionRepository txRepository;
    private final PaymentAuditRepository auditRepository;

    public PaymentService(AccountRepository accountRepository,
                          PaymentTransactionRepository txRepository,
                          PaymentAuditRepository auditRepository) {
        this.accountRepository = accountRepository;
        this.txRepository = txRepository;
        this.auditRepository = auditRepository;
    }

    @Transactional
    public ChargeResponse charge(ChargeRequest req) {

        // “fake payment”: ako amount <= 1000 => SUCCESS, inace FAILED
        boolean success = req.getAmount().compareTo(new BigDecimal("1000.00")) <= 0;

        Account account = accountRepository.findByUserId(req.getUserId())
                .orElseGet(() -> {
                    Account a = new Account();
                    a.setUserId(req.getUserId());
                    a.setBalance(new BigDecimal("0.00"));
                    return accountRepository.save(a);
                });

        PaymentTransaction tx = new PaymentTransaction();
        tx.setUserId(req.getUserId());
        tx.setReservationId(req.getReservationId());
        tx.setAmount(req.getAmount());

        if (success) {
            // samo “evidencija” – dodaj na balance da vidis efekat, kao uspesna uplata u bazi
            account.setBalance(account.getBalance().add(req.getAmount()));
            tx.setStatus(PaymentTransaction.Status.SUCCESS);
            tx.setDescription("Fake payment SUCCESS");
        } else {
            tx.setStatus(PaymentTransaction.Status.FAILED);
            tx.setDescription("Fake payment FAILED (amount too high)");
        }

        accountRepository.save(account);
        PaymentTransaction savedTx = txRepository.save(tx);

        PaymentAudit audit = new PaymentAudit();
        audit.setTransactionId(savedTx.getId());

        if (success) {
            audit.setMessage("Payment SUCCESS");
        } else {
            audit.setMessage("Payment FAILED");
        }

        auditRepository.save(audit);

        return new ChargeResponse(savedTx.getId(), savedTx.getStatus().name(), account.getBalance());
    }

    @Transactional(readOnly = true)
    public Account getAccount(Long userId) {
        return accountRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found for userId=" + userId));
    }
}
