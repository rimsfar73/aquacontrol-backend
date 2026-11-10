package com.example.aquacontrol_backend.usuario.service;

import com.example.aquacontrol_backend.usuario.dto.UsuarioDTO;
import com.example.aquacontrol_backend.usuario.entity.Rol;
import com.example.aquacontrol_backend.usuario.entity.UsuarioEntity;
import com.example.aquacontrol_backend.usuario.repository.UsuarioRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UsuarioServiceTest {

    private UsuarioRepository repository;
    private UsuarioService usuarioService;

    @BeforeEach
    void setUp() {
        repository = mock(UsuarioRepository.class);
        usuarioService = new UsuarioService(repository);
    }

    @Test
    void cargarOperarioActivo() {

        UsuarioEntity usuario = new UsuarioEntity(
                "operario",
                "hash-bcrypt-simulado",
                Rol.OPERARIO
        );

        when(repository.findByUsername("operario"))
                .thenReturn(Optional.of(usuario));

        UserDetails resultado =
                usuarioService.loadUserByUsername("operario");

        assertEquals("operario", resultado.getUsername());
        assertEquals("hash-bcrypt-simulado", resultado.getPassword());
        assertTrue(resultado.isEnabled());

        assertTrue(resultado.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority()
                        .equals("ROLE_OPERARIO")));

        verify(repository).findByUsername("operario");
    }

    @Test
    void cargarSupervisorActivo() {

        UsuarioEntity usuario = new UsuarioEntity(
                "supervisor",
                "hash-supervisor-simulado",
                Rol.SUPERVISOR
        );

        when(repository.findByUsername("supervisor"))
                .thenReturn(Optional.of(usuario));

        UserDetails resultado =
                usuarioService.loadUserByUsername("supervisor");

        assertEquals("supervisor", resultado.getUsername());
        assertTrue(resultado.isEnabled());

        assertTrue(resultado.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority()
                        .equals("ROLE_SUPERVISOR")));

        verify(repository).findByUsername("supervisor");
    }

    @Test
    void cargarUsuarioInexistente() {

        when(repository.findByUsername("desconocido"))
                .thenReturn(Optional.empty());

        UsernameNotFoundException exception =
                assertThrows(
                        UsernameNotFoundException.class,
                        () -> usuarioService
                                .loadUserByUsername("desconocido")
                );

        assertEquals(
                "Credenciales invalidas",
                exception.getMessage()
        );

        verify(repository).findByUsername("desconocido");
    }

    @Test
    void cargarUsuarioInactivo() {

        UsuarioEntity usuario = mock(UsuarioEntity.class);

        when(usuario.getUsername()).thenReturn("operario");
        when(usuario.getPasswordHash())
                .thenReturn("hash-simulado");
        when(usuario.getRol()).thenReturn(Rol.OPERARIO);
        when(usuario.isActivo()).thenReturn(false);

        when(repository.findByUsername("operario"))
                .thenReturn(Optional.of(usuario));

        UserDetails resultado =
                usuarioService.loadUserByUsername("operario");

        assertFalse(resultado.isEnabled());

        verify(repository).findByUsername("operario");
    }

    @Test
    void obtenerPerfilExistente() {

        UsuarioEntity usuario = mock(UsuarioEntity.class);

        when(usuario.getId()).thenReturn(2L);
        when(usuario.getUsername()).thenReturn("supervisor");
        when(usuario.getRol()).thenReturn(Rol.SUPERVISOR);
        when(usuario.isActivo()).thenReturn(true);

        when(repository.findByUsername("supervisor"))
                .thenReturn(Optional.of(usuario));

        UsuarioDTO resultado =
                usuarioService.obtenerPerfil("supervisor");

        assertEquals(2L, resultado.id());
        assertEquals("supervisor", resultado.username());
        assertEquals("SUPERVISOR", resultado.rol());
        assertTrue(resultado.activo());

        verify(repository).findByUsername("supervisor");
    }

    @Test
    void obtenerPerfilInexistente() {

        when(repository.findByUsername("fantasma"))
                .thenReturn(Optional.empty());

        UsernameNotFoundException exception =
                assertThrows(
                        UsernameNotFoundException.class,
                        () -> usuarioService.obtenerPerfil("fantasma")
                );

        assertEquals(
                "Usuario no encontrado",
                exception.getMessage()
        );

        verify(repository).findByUsername("fantasma");
    }
}
