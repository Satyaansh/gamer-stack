package com.projects.gamerstack.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.projects.gamerstack.dto.RegisterRequest;
import com.projects.gamerstack.service.AuthService;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("api/auth")
@AllArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity signup(@RequestBody RegisterRequest registerRequest) {
        authService.signup(registerRequest);
        return new ResponseEntity<>("Account Registration Successful",HttpStatus.OK);
    }

    @PostMapping("/login")
    public AuthenticationResonse login(@RequestBody LoginRequest loginRequest) {
        return authService.login(loginRequest);
    }

    @GetMapping("/accountVerification/{token}")
    public ResponseEntity verifyAccount(@PathVariable String token) {
        authService.verifyAccount(token);
        return new ResponseEntity<>("Account Activated Successfully", HttpStatus.OK);
    }
    
}
