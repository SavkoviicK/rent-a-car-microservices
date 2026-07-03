package com.rzk.reservation_service.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "user-service")
public interface UserProxy {

    @GetMapping("/internal/users/id-by-email")
    Long getIdByEmail(
            @RequestHeader("X-Internal-Call") String internalCall,
            @RequestParam("email") String email
    );
}
