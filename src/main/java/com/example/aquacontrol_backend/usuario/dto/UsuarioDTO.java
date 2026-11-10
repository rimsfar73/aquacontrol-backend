package com.example.aquacontrol_backend.usuario.dto;

public record UsuarioDTO(
        Long id,
        String username,
        String rol,
        boolean activo
) {
}
