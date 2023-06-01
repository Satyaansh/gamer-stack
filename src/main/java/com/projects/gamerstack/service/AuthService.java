package com.projects.gamerstack.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.authentication.AuthenticationManager;

import com.projects.gamerstack.dto.AuthenticationResponse;
import com.projects.gamerstack.dto.LoginRequest;
import com.projects.gamerstack.dto.RefreshTokenRequest;
import com.projects.gamerstack.dto.RegisterRequest;
import com.projects.gamerstack.exception.GamerStackException;
import com.projects.gamerstack.model.NotificationEmail;
import com.projects.gamerstack.model.User;
import com.projects.gamerstack.model.VerificationToken;
import com.projects.gamerstack.repository.UserRepository;
import com.projects.gamerstack.repository.VerificationTokenRepository;
import com.projects.gamerstack.security.JwtProvider;
import com.projects.gamerstack.util.Constants;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final VerificationTokenRepository verificationTokenRepository;
    private final MailContentBuilder mailContentBuilder;
    private final MailService mailService;
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;
    private final RefreshTokenService refreshTokenService;

    @Transactional
    public void signup(RegisterRequest registerRequest) {
        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(encodePassword(registerRequest.getPassword()));
        user.setCreated(Instant.now());
        user.setEnabled(false);

        userRepository.save(user);

        String token  = generateVerificationToken(user);
        String message = mailContentBuilder.build("Thank You for signing up for GamerStack! Please click on this URL to activate your account : "
                     + Constants.ACTIVATION_EMAIL + "/" + token);

        mailService.sendMail(new NotificationEmail("Please Activate your GamerStack Account", user.getEmail(), message));
        
    }

    private String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }

    private String generateVerificationToken(User user) {
        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(token);
        verificationToken.setUser(user);
        verificationTokenRepository.save(verificationToken);
        return token;
    }

    public void verifyAccount(String token) {
        Optional<VerificationToken> verificationTokenOptional = verificationTokenRepository.findByToken(token);
        verificationTokenOptional.orElseThrow(() -> new GamerStackException("Invalid Token"));
        fetchUserAndEnable(verificationTokenOptional.get());
    }

    @Transactional
    private void fetchUserAndEnable(VerificationToken verificationToken) {
        Long userid = verificationToken.getUser().getUserId();
        User user = userRepository.findById(userid).orElseThrow(() -> new GamerStackException("User not found with id : " + userid));
        user.setEnabled(true);
        userRepository.save(user);
    }

    public AuthenticationResponse login(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String authenticationToken = jwtProvider.generateToken(authentication);
        return AuthenticationResponse.builder()
                        .authenticationToken(authenticationToken)
                        .username(loginRequest.getUsername())
                        .refreshToken(refreshTokenService.generaRefreshToken().getToken())
                        .expiresAt(Instant.now().plusMillis(jwtProvider.getJwtExpirationInMillis()))
                        .build();
    }

    @Transactional(readOnly = true) 
    User getCurrentUser() {
        org.springframework.security.core.userdetails.User principal = (org.springframework.security.core.userdetails.User) SecurityContextHolder.
                getContext().getAuthentication().getPrincipal();

        return userRepository.findByUsername(principal.getUsername())
                        .orElseThrow(() -> new UsernameNotFoundException("Username not found - " + principal.getUsername()));
    }

    public AuthenticationResponse refreshToken(RefreshTokenRequest refreshTokenRequest) {
        
        refreshTokenService.validateRefreshToken(refreshTokenRequest.getRefreshToken());
        String token = jwtProvider.generateTokenWithUserName(refreshTokenRequest.getUsername());
        return AuthenticationResponse.builder()
                        .authenticationToken(token)
                        .username(refreshTokenRequest.getUsername())
                        .refreshToken(refreshTokenRequest.getRefreshToken())
                        .expiresAt(Instant.now().plusMillis(jwtProvider.getJwtExpirationInMillis()))
                        .build();
    }
    
}
