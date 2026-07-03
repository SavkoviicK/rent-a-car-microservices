package com.rzk.vehicle_service.security;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

@Component
public class RoleGuard {

    private boolean isInternalCall(HttpServletRequest request) {
        String internal = request.getHeader("X-Internal-Call");
        return "true".equalsIgnoreCase(internal);
    }

    public void requireAdmin(HttpServletRequest request) {
        //dozvoli servis-servis pozive
        if (isInternalCall(request)) {
            return;
        }

        String roles = request.getHeader("X-User-Roles");

        if (roles == null || !roles.contains("ADMIN")) {
            throw new ForbiddenException("ADMIN role required");
        }
    }

    public void requireUserOrAdmin(HttpServletRequest request) {
        if (isInternalCall(request)) {
            return;
        }

        String roles = request.getHeader("X-User-Roles");

        if (roles == null || (!roles.contains("ADMIN") && !roles.contains("USER"))) {
            throw new ForbiddenException("USER or ADMIN role required");
        }
    }
}
