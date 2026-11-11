package com.example.aquacontrol_backend.auth.security;

import com.example.aquacontrol_backend.flushing.controller.FlushingController;
import com.example.aquacontrol_backend.flushing.service.FlushingService;
import com.example.aquacontrol_backend.foto.controller.FotoController;
import com.example.aquacontrol_backend.foto.dto.FotoDTO;
import com.example.aquacontrol_backend.foto.entity.FotoEntity;
import com.example.aquacontrol_backend.foto.service.FotoService;
import com.example.aquacontrol_backend.usuario.service.UsuarioService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// Probamos las reglas HTTP con servicios simulados.
// La validación real de imágenes se comprueba en pruebas separadas.
@WebMvcTest(controllers = {
        FotoController.class,
        FlushingController.class
})
@Import(SecurityConfig.class)
class FotoSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FotoService fotoService;

    @MockitoBean
    private FlushingService flushingService;

    @MockitoBean
    private UsuarioService usuarioService;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    // Estos bytes solo sirven para probar el transporte y los permisos.
    private MockMultipartFile archivoDePrueba() {
        return new MockMultipartFile(
                "foto", "evidencia.jpg", "image/jpeg",
                new byte[]{1, 2, 3}
        );
    }

    @Test
    void fotoSinTokenDevuelve401() throws Exception {
        mockMvc.perform(get("/api/v1/flushing/2/foto"))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(fotoService);
    }

    @Test
    void operarioPuedeSubirFoto() throws Exception {
        when(fotoService.guardar(eq(2L), any()))
                .thenReturn(new FotoDTO(2L, "image/jpeg", 3));

        mockMvc.perform(
                        multipart("/api/v1/flushing/2/foto")
                                .file(archivoDePrueba())
                                .with(request -> {
                                    request.setMethod("PUT");
                                    return request;
                                })
                                .with(jwt().authorities(
                                        new SimpleGrantedAuthority("ROLE_OPERARIO")
                                ))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.flushingId").value(2))
                .andExpect(jsonPath("$.tipoContenido").value("image/jpeg"));

        verify(fotoService).guardar(eq(2L), any());
    }

    @Test
    void supervisorNoPuedeSubirFoto() throws Exception {
        mockMvc.perform(
                multipart("/api/v1/flushing/2/foto")
                        .file(archivoDePrueba())
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        })
                        .with(jwt().authorities(
                                new SimpleGrantedAuthority("ROLE_SUPERVISOR")
                        ))
        ).andExpect(status().isForbidden());

        // La solicitud debe detenerse antes de modificar la evidencia.
        verifyNoInteractions(fotoService);
    }

    @Test
    void ambosRolesPuedenConsultarFoto() throws Exception {
        FotoEntity foto = new FotoEntity();
        foto.setTipoContenido("image/jpeg");
        foto.setContenido(new byte[]{1, 2, 3});
        when(fotoService.obtener(2L)).thenReturn(foto);

        for (String rol : new String[]{"ROLE_OPERARIO", "ROLE_SUPERVISOR"}) {
            mockMvc.perform(
                            get("/api/v1/flushing/2/foto")
                                    .with(jwt().authorities(
                                            new SimpleGrantedAuthority(rol)
                                    ))
                    )
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("image/jpeg"))
                    .andExpect(content().bytes(new byte[]{1, 2, 3}));
        }

        verify(fotoService, times(2)).obtener(2L);
    }

    @Test
    void supervisorNoPuedeCrearFlushing() throws Exception {
        mockMvc.perform(
                post("/api/v1/flushing")
                        .with(jwt().authorities(
                                new SimpleGrantedAuthority("ROLE_SUPERVISOR")
                        ))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "lineaId": 1,
                                  "fechaHora": "2026-11-06T20:04:27",
                                  "duracionSegundos": 120,
                                  "observacion": "Prueba de permisos",
                                  "temperaturaAntes": 28.5,
                                  "temperaturaDespues": 19.5
                                }
                                """)
        ).andExpect(status().isForbidden());

        verifyNoInteractions(flushingService);
    }
}