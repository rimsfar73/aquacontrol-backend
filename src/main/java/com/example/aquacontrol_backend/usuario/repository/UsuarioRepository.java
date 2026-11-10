package com.example.aquacontrol_backend.usuario.repository;

import com.example.aquacontrol_backend.usuario.entity.UsuarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository
        extends JpaRepository<UsuarioEntity, Long> {

    Optional<UsuarioEntity> findByUsername(String username);
}
