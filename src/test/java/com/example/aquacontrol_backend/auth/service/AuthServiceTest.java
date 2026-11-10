package com.example.aquacontrol_backend.auth.service;

import com.example.aquacontrol_backend.auth.dto.LoginRequest;
import com.example.aquacontrol_backend.auth.dto.LoginResponse;
import com.example.aquacontrol_backend.auth.security.JwtService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    private AuthenticationManager authenticationManager;
    private JwtService jwtService;
    private AuthService authService;

    @BeforeEach
    void setUp() {
        authenticationManager = mock(AuthenticationManager.class);
        jwtService = mock(JwtService.class);

        authService = new AuthService(
                authenticationManager,
                jwtService
        );
    }

    @Test
    void loginOperarioValido() {

        LoginRequest request =
                new LoginRequest("operario", "demo1234");

        Authentication authentication =
                new UsernamePasswordAuthenticationToken(
                        "operario",
                        null,
                        List.of(new SimpleGrantedAuthority("ROLE_OPERARIO"))
                );

        when(authenticationManager.authenticate(any(Authentication.class)))
                .thenReturn(authentication);

        when(jwtService.generarToken(authentication))
                .thenReturn("jwt-operario-simulado");

        LoginResponse response = authService.login(request);

        assertEquals("jwt-operario-simulado", response.accessToken());
        assertEquals("Bearer", response.tokenType());
        assertEquals(3600L, response.expiresIn());
        assertEquals("ROLE_OPERARIO", response.rol());

        verify(authenticationManager).authenticate(
                argThat(auth ->
                        "operario".equals(auth.getPrincipal())
                                && "demo1234".equals(auth.getCredentials())
                )
        );

        verify(jwtService).generarToken(authentication);
    }

    @Test
    void loginSupervisorValido() {

        LoginRequest request =
                new LoginRequest("supervisor", "demo5678");

        Authentication authentication =
                new UsernamePasswordAuthenticationToken(
                        "supervisor",
                        null,
                        List.of(new SimpleGrantedAuthority("ROLE_SUPERVISOR"))
                );

        when(authenticationManager.authenticate(any(Authentication.class)))
                .thenReturn(authentication);

        when(jwtService.generarToken(authentication))
                .thenReturn("jwt-supervisor-simulado");

        LoginResponse response = authService.login(request);

        assertEquals("jwt-supervisor-simulado", response.accessToken());
        assertEquals("Bearer", response.tokenType());
        assertEquals(3600L, response.expiresIn());
        assertEquals("ROLE_SUPERVISOR", response.rol());

        verify(jwtService).generarToken(authentication);
    }

    @Test
    void loginConCredencialesIncorrectas() {

        LoginRequest request =
                new LoginRequest("operario", "password-incorrecta");

        when(authenticationManager.authenticate(any(Authentication.class)))
                .thenThrow(new BadCredentialsException(
                        "Credenciales inválidas"
                ));

        assertThrows(
                BadCredentialsException.class,
                () -> authService.login(request)
        );

        verifyNoInteractions(jwtService);
    }

    @Test
    void loginSinRolValido() {

        LoginRequest request =
                new LoginRequest("usuario", "password");

        Authentication authentication =
                new UsernamePasswordAuthenticationToken(
                        "usuario",
                        null,
                        List.of(new SimpleGrantedAuthority("PERMISO_LECTURA"))
                );

        when(authenticationManager.authenticate(any(Authentication.class)))
                .thenReturn(authentication);

        when(jwtService.generarToken(authentication))
                .thenReturn("jwt-simulado");

        assertThrows(
                NoSuchElementException.class,
                () -> authService.login(request)
        );
    }
}

