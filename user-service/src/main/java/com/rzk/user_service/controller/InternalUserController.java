package com.rzk.user_service.controller;

import com.rzk.user_service.model.User;
import com.rzk.user_service.repository.UserRepository;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/internal/users")
public class InternalUserController {

    private final UserRepository userRepository;

    public InternalUserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // GET /internal/users/id-by-email?email=primer@test.com port 8082 i token
    @GetMapping("/id-by-email")
    public Long getIdByEmail(@RequestParam String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found for email: " + email));
        return user.getId();
    }
}
