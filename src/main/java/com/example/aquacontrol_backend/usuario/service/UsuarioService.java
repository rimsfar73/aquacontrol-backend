package com.example.aquacontrol_backend.usuario.service;

import com.example.aquacontrol_backend.usuario.dto.UsuarioDTO;
import com.example.aquacontrol_backend.usuario.entity.UsuarioEntity;
import com.example.aquacontrol_backend.usuario.repository.UsuarioRepository;

import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService implements UserDetailsService {

    private final UsuarioRepository repository;

    public UsuarioService(UsuarioRepository repository) {
        this.repository = repository;
    }

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        UsuarioEntity usuario = repository.findByUsername(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                "Credenciales invalidas"
                        )
                );

        return User.withUsername(usuario.getUsername())
                .password(usuario.getPasswordHash())
                .roles(usuario.getRol().name())
                .disabled(!usuario.isActivo())
                .build();
    }

    public UsuarioDTO obtenerPerfil(String username) {

        UsuarioEntity usuario = repository.findByUsername(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                "Usuario no encontrado"
                        )
                );

        return new UsuarioDTO(
                usuario.getId(),
                usuario.getUsername(),
                usuario.getRol().name(),
                usuario.isActivo()
        );
    }
}