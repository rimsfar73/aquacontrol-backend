
package com.example.aquacontrol_backend.auth.security;

import com.example.aquacontrol_backend.EstadoController;
import com.example.aquacontrol_backend.flushing.controller.FlushingController;
import com.example.aquacontrol_backend.flushing.service.FlushingService;
import com.example.aquacontrol_backend.usuario.controller.UsuarioController;
import com.example.aquacontrol_backend.usuario.dto.UsuarioDTO;
import com.example.aquacontrol_backend.usuario.service.UsuarioService;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = {
        EstadoController.class,
        UsuarioController.class,
        FlushingController.class
})
@Import(SecurityConfig.class)
class SecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UsuarioService usuarioService;

    @MockitoBean
    private FlushingService flushingService;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    private org.springframework.security.test.web.servlet.request
            .SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor
    tokenConRol(String username, String rol) {

        return jwt()
                .jwt(builder -> builder
                        .subject(username)
                        .issuer("aquacontrol-backend")
                        .claim("rol", rol)
                )
                .authorities(
                        new org.springframework.security.core.authority
                                .SimpleGrantedAuthority(rol)
                );
    }

    @Test
    void estadoPublicoDevuelve200() throws Exception {

        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("OPERATIVO"));
    }

    @Test
    void perfilSinTokenDevuelve401() throws Exception {

        mockMvc.perform(get("/api/v1/usuarios/me"))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(usuarioService);
    }

    @Test
    void perfilConOperarioDevuelve200() throws Exception {

        when(usuarioService.obtenerPerfil("operario"))
                .thenReturn(new UsuarioDTO(
                        1L, "operario", "OPERARIO", true
                ));

        mockMvc.perform(
                        get("/api/v1/usuarios/me")
                                .with(tokenConRol("operario", "ROLE_OPERARIO"))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("operario"))
                .andExpect(jsonPath("$.rol").value("OPERARIO"));

        verify(usuarioService).obtenerPerfil("operario");
    }

    @Test
    void operarioNoPuedeEliminarFlushing() throws Exception {

        mockMvc.perform(
                        delete("/api/v1/flushing/999999999")
                                .with(tokenConRol("operario", "ROLE_OPERARIO"))
                )
                .andExpect(status().isForbidden());

        verifyNoInteractions(flushingService);
    }

    @Test
    void supervisorPuedeEliminarFlushing() throws Exception {

        mockMvc.perform(
                        delete("/api/v1/flushing/999999999")
                                .with(tokenConRol("supervisor", "ROLE_SUPERVISOR"))
                )
                .andExpect(status().isNoContent());

        verify(flushingService).eliminar(999999999L);
    }

    @Test
    void endpointDesconocidoSeDeniega() throws Exception {

        mockMvc.perform(
                        get("/api/v1/ruta-no-permitida")
                                .with(tokenConRol("operario", "ROLE_OPERARIO"))
                )
                .andExpect(status().isForbidden());
    }
}

