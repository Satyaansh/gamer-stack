package com.projects.gamerstack.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.projects.gamerstack.dto.RegisterRequest;
import com.projects.gamerstack.exception.GamerStackException;
import com.projects.gamerstack.model.NotificationEmail;
import com.projects.gamerstack.model.User;
import com.projects.gamerstack.model.VerificationToken;
import com.projects.gamerstack.repository.UserRepository;
import com.projects.gamerstack.repository.VerificationTokenRepository;
import com.projects.gamerstack.util.Constants;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final VerificationTokenRepository verificationTokenRepository;
    private final MailContentBuilder mailContentBuilder;
    private final MailService mailService;

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
    
}
