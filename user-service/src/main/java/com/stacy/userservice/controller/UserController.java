package com.stacy.userservice.controller;

import com.stacy.userservice.dto.LoginRequest;
import com.stacy.userservice.dto.UserRequest;
import com.stacy.userservice.dto.UserResponse;
import com.stacy.userservice.entity.User;
import com.stacy.userservice.security.JwtService;
import com.stacy.userservice.service.UserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final JwtService jwtService;

    public UserController(UserService userService,JwtService jwtService) {
        this.userService = userService;
        this.jwtService = jwtService;
    }

    @GetMapping("/test")
    public String test() {
        return "User Service is working";
    }

    @PostMapping("/register")
    public UserResponse register(
            @Valid @RequestBody UserRequest request) {

        return userService.register(request);
    }

    @PostMapping("/login")
    public Map<String,String> login(
            @Valid @RequestBody LoginRequest request) {

        User user = userService.authenticate(request);

        String token = jwtService.generateToken(user.getUsername(), user.getRole());

        return Map.of("token",token);
    }
}
