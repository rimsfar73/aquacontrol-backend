package com.example.aquacontrol_backend.auth.dto;

public record LoginResponse(

        String accessToken,
        String tokenType,
        long expiresIn,
        String rol

) {
}