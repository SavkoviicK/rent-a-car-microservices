package com.rzk.reservation_service.feign;

import com.rzk.reservation_service.dto.ChargeRequest;
import com.rzk.reservation_service.dto.ChargeResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "payment-service")
public interface PaymentProxy {

    @PostMapping("/api/payments/charge")
    ChargeResponse charge(@RequestBody ChargeRequest request);
}
