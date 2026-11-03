package com.example.aquacontrol_backend;

import com.example.aquacontrol_backend.common.exception.ResourceNotFoundException;
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
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;

class AquacontrolBackendApplicationTests {
    @Test
    void crearTemperatura() {
        TemperaturaRepository repository = mock(TemperaturaRepository.class);
        when(repository.save(any(TemperaturaEntity.class))).thenAnswer(invocation -> {
            TemperaturaEntity entity = invocation.getArgument(0);
            entity.setId(1L);
            return entity;
        });
        TemperaturaDTO dto = new TemperaturaDTO(null, 2L, 26.5,
                LocalDateTime.of(2026, 11, 3, 10, 0), "Normal");
        TemperaturaDTO saved = new TemperaturaService(repository).crear(dto);
        assertEquals(1L, saved.id());
        assertEquals(26.5, saved.temperatura());
        verify(repository).save(any(TemperaturaEntity.class));
    }

    @Test
    void temperaturaNoEncontrada() {
        TemperaturaRepository repository = mock(TemperaturaRepository.class);
        when(repository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class,
                () -> new TemperaturaService(repository).obtener(99L));
    }

    @Test
    void listarTemperaturasPorLinea() {
        TemperaturaRepository repository = mock(TemperaturaRepository.class);
        when(repository.findByLineaIdOrderByFechaHoraDesc(2L)).thenReturn(List.of());
        assertTrue(new TemperaturaService(repository).listar(2L).isEmpty());
        verify(repository).findByLineaIdOrderByFechaHoraDesc(2L);
    }

    @Test
    void crearFlushing() {
        FlushingRepository repository = mock(FlushingRepository.class);
        when(repository.save(any(FlushingEntity.class))).thenAnswer(invocation -> {
            FlushingEntity entity = invocation.getArgument(0);
            entity.setId(4L);
            return entity;
        });
        FlushingDTO dto = new FlushingDTO(null, 2L,
                LocalDateTime.of(2026, 11, 3, 10, 0), 120, "Limpieza");
        FlushingDTO saved = new FlushingService(repository).crear(dto);
        assertEquals(4L, saved.id());
        assertEquals(120, saved.duracionSegundos());
    }

    @Test
    void flushingNoEncontrado() {
        FlushingRepository repository = mock(FlushingRepository.class);
        when(repository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class,
                () -> new FlushingService(repository).eliminar(99L));
    }
}
