package com.example.aquacontrol_backend.usuario.controller;

import com.example.aquacontrol_backend.usuario.dto.UsuarioDTO;
import com.example.aquacontrol_backend.usuario.service.UsuarioService;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/usuarios")
public class UsuarioController {

    private final UsuarioService service;

    public UsuarioController(UsuarioService service) {
        this.service = service;
    }

    @GetMapping("/me")
    public UsuarioDTO miPerfil(Authentication authentication) {
        return service.obtenerPerfil(authentication.getName());
    }
}
