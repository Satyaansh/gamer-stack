package com.projects.gamerstack.security;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.time.Instant;
import java.sql.Date;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import com.projects.gamerstack.exception.GamerStackException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import static java.util.Date.from;

@Service
public class JwtProvider {
    
    private KeyStore keyStore;

    @Value("${jwt.expiration.time}")
    private long jwtExpirationInMillis;

    @PostConstruct
    public void init() {
        try {
            keyStore = KeyStore.getInstance("JKS");
            InputStream resourceAsStream = getClass().getResourceAsStream("/gamerstack.jks");
            keyStore.load(resourceAsStream, "password".toCharArray());
        }
        catch (KeyStoreException | CertificateException | NoSuchAlgorithmException | IOException e) {
            throw new GamerStackException("Exception occured while loading keystore");
        }
    }

    public String generateToken(Authentication authentication) {
        User principal = (User)authentication.getPrincipal();
        return Jwts.builder()
                    .setSubject(principal.getUsername())
                    .setIssuedAt(from(Instant.now()))
                    .signWith(getPrivateKey())
                    .setExpiration(Date.from(Instant.now().plusMillis(jwtExpirationInMillis)))
                    .compact();
    }

    public String generateTokenWithUserName(String username) {
        return Jwts.builder()
                    .setSubject(username)
                    .setIssuedAt(from(Instant.now()))
                    .signWith(getPrivateKey())
                    .setExpiration(Date.from(Instant.now().plusMillis(jwtExpirationInMillis)))
                    .compact();
    }

    private PrivateKey getPrivateKey() {
        try {
            return (PrivateKey) keyStore.getKey("gamerstack", "password".toCharArray());
        }
        catch (KeyStoreException | NoSuchAlgorithmException | UnrecoverableKeyException e) {
            throw new GamerStackException("Exception occured while retrieving Private Key from Keystore");
        }
    }

    public String getUsernameFromJWT(String token) {
        Claims claims = Jwts.parser()
                    .setSigningKey(getPublicKey())
                    .parseClaimsJws(token)
                    .getBody();

        return claims.getSubject();
    }

    public boolean validateToken(String jwt) {
        Jwts.parser().setSigningKey(getPublicKey()).parseClaimsJws(jwt);
        return true;
    }

    private PublicKey getPublicKey() {
        try {
            return keyStore.getCertificate("gamerstack").getPublicKey();
        }
        catch (KeyStoreException e) {
            throw new GamerStackException("Exception occured while retrieving Public Key from Keystore");
        }
    }

    public Long getJwtExpirationInMillis() {
        return jwtExpirationInMillis;
    }
}
