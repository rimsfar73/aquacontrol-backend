package com.example.aquacontrol_backend.auth.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.*;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
public class JwtService {

    private final JwtEncoder jwtEncoder;

    public JwtService(JwtEncoder jwtEncoder) {
        this.jwtEncoder = jwtEncoder;
    }

    public String generarToken(Authentication authentication) {

        Instant ahora = Instant.now();

        String rol = authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .filter(authority -> authority.startsWith("ROLE_"))
                .findFirst()
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Usuario sin rol valido"
                        )
                );

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("aquacontrol-backend")
                .subject(authentication.getName())
                .issuedAt(ahora)
                .expiresAt(ahora.plus(1, ChronoUnit.HOURS))
                .claim("rol", rol)
                .build();

        JwsHeader header = JwsHeader
                .with(SignatureAlgorithm.RS256)
                .build();

        return jwtEncoder.encode(
                JwtEncoderParameters.from(header, claims)
        ).getTokenValue();
    }
}