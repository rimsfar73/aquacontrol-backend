
package com.example.aquacontrol_backend;

import com.example.aquacontrol_backend.common.exception.ResourceNotFoundException;

import com.example.aquacontrol_backend.linea.repository.LineaRepository;

import com.example.aquacontrol_backend.temperatura.dto.TemperaturaDTO;
import com.example.aquacontrol_backend.temperatura.entity.TemperaturaEntity;
import com.example.aquacontrol_backend.temperatura.repository.TemperaturaRepository;
import com.example.aquacontrol_backend.temperatura.service.TemperaturaService;

import com.example.aquacontrol_backend.flushing.dto.FlushingDTO;
import com.example.aquacontrol_backend.flushing.entity.FlushingEntity;
import com.example.aquacontrol_backend.flushing.repository.FlushingRepository;
import com.example.aquacontrol_backend.flushing.service.FlushingService;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AquacontrolBackendApplicationTests {

    // ---------------------------------------------------------
    // TEMPERATURA
    // ---------------------------------------------------------

    @Test
    void crearTemperatura() {

        TemperaturaRepository repository =
                mock(TemperaturaRepository.class);

        LineaRepository lineaRepository =
                mock(LineaRepository.class);

        // Simulamos que la línea 2 existe.
        when(lineaRepository.existsById(2L))
                .thenReturn(true);

        when(repository.save(any(TemperaturaEntity.class)))
                .thenAnswer(invocation -> {
                    TemperaturaEntity entity =
                            invocation.getArgument(0);

                    entity.setId(1L);
                    return entity;
                });

        TemperaturaDTO dto = new TemperaturaDTO(
                null,
                2L,
                26.5,
                LocalDateTime.of(2026, 11, 3, 10, 0),
                "Normal"
        );

        TemperaturaService service =
                new TemperaturaService(
                        repository,
                        lineaRepository
                );

        TemperaturaDTO saved = service.crear(dto);

        assertEquals(1L, saved.id());
        assertEquals(26.5, saved.temperatura());
        assertEquals(2L, saved.lineaId());

        verify(lineaRepository).existsById(2L);
        verify(repository).save(any(TemperaturaEntity.class));
    }

    @Test
    void temperaturaNoEncontrada() {

        TemperaturaRepository repository =
                mock(TemperaturaRepository.class);

        LineaRepository lineaRepository =
                mock(LineaRepository.class);

        when(repository.findById(99L))
                .thenReturn(Optional.empty());

        TemperaturaService service =
                new TemperaturaService(
                        repository,
                        lineaRepository
                );

        assertThrows(
                ResourceNotFoundException.class,
                () -> service.obtener(99L)
        );
    }

    @Test
    void listarTemperaturasPorLinea() {

        TemperaturaRepository repository =
                mock(TemperaturaRepository.class);

        LineaRepository lineaRepository =
                mock(LineaRepository.class);

        when(repository.findByLineaIdOrderByFechaHoraDesc(2L))
                .thenReturn(List.of());

        TemperaturaService service =
                new TemperaturaService(
                        repository,
                        lineaRepository
                );

        assertTrue(service.listar(2L).isEmpty());

        verify(repository)
                .findByLineaIdOrderByFechaHoraDesc(2L);
    }

    @Test
    void crearTemperaturaConLineaInexistente() {

        TemperaturaRepository repository =
                mock(TemperaturaRepository.class);

        LineaRepository lineaRepository =
                mock(LineaRepository.class);

        // La línea 9999 no existe.
        when(lineaRepository.existsById(9999L))
                .thenReturn(false);

        TemperaturaDTO dto = new TemperaturaDTO(
                null,
                9999L,
                26.5,
                LocalDateTime.of(2026, 11, 3, 10, 0),
                "Prueba de integridad"
        );

        TemperaturaService service =
                new TemperaturaService(
                        repository,
                        lineaRepository
                );

        assertThrows(
                ResourceNotFoundException.class,
                () -> service.crear(dto)
        );

        verify(lineaRepository).existsById(9999L);

        // No debe llegar a guardar el registro.
        verify(repository, never())
                .save(any(TemperaturaEntity.class));
    }

    // ---------------------------------------------------------
    // FLUSHING
    // ---------------------------------------------------------

    @Test
    void crearFlushing() {

        FlushingRepository repository =
                mock(FlushingRepository.class);

        LineaRepository lineaRepository =
                mock(LineaRepository.class);

        // Simulamos que la línea 2 existe.
        when(lineaRepository.existsById(2L))
                .thenReturn(true);

        when(repository.save(any(FlushingEntity.class)))
                .thenAnswer(invocation -> {
                    FlushingEntity entity =
                            invocation.getArgument(0);

                    entity.setId(4L);
                    return entity;
                });

        FlushingDTO dto = new FlushingDTO(
                null,
                2L,
                LocalDateTime.of(2026, 11, 3, 10, 0),
                120,
                "Limpieza",
                null,
                null
        );

        FlushingService service =
                new FlushingService(
                        repository,
                        lineaRepository
                );

        FlushingDTO saved = service.crear(dto);

        assertEquals(4L, saved.id());
        assertEquals(120, saved.duracionSegundos());
        assertEquals(2L, saved.lineaId());

        verify(lineaRepository).existsById(2L);
        verify(repository).save(any(FlushingEntity.class));
    }

    @Test
    void flushingNoEncontrado() {

        FlushingRepository repository =
                mock(FlushingRepository.class);

        LineaRepository lineaRepository =
                mock(LineaRepository.class);

        when(repository.findById(99L))
                .thenReturn(Optional.empty());

        FlushingService service =
                new FlushingService(
                        repository,
                        lineaRepository
                );

        assertThrows(
                ResourceNotFoundException.class,
                () -> service.eliminar(99L)
        );
    }

    @Test
    void crearFlushingConLineaInexistente() {

        FlushingRepository repository =
                mock(FlushingRepository.class);

        LineaRepository lineaRepository =
                mock(LineaRepository.class);

        // La línea 9999 no existe.
        when(lineaRepository.existsById(9999L))
                .thenReturn(false);

        FlushingDTO dto = new FlushingDTO(
                null,
                9999L,
                LocalDateTime.of(2026, 11, 3, 10, 0),
                120,
                "Prueba de integridad",
                null,
                null
        );

        FlushingService service =
                new FlushingService(
                        repository,
                        lineaRepository
                );

        assertThrows(
                ResourceNotFoundException.class,
                () -> service.crear(dto)
        );

        verify(lineaRepository).existsById(9999L);

        // No debe llegar a guardar el registro.
        verify(repository, never())
                .save(any(FlushingEntity.class));
    }
}
