package com.example.aquacontrol_backend;

import com.example.aquacontrol_backend.common.exception.ResourceNotFoundException;
import com.example.aquacontrol_backend.flushing.dto.FlushingDTO;
import com.example.aquacontrol_backend.flushing.entity.FlushingEntity;
import com.example.aquacontrol_backend.flushing.repository.FlushingRepository;
import com.example.aquacontrol_backend.flushing.service.FlushingService;
import com.example.aquacontrol_backend.foto.repository.FotoRepository;
import com.example.aquacontrol_backend.linea.repository.LineaRepository;
import com.example.aquacontrol_backend.temperatura.dto.TemperaturaDTO;
import com.example.aquacontrol_backend.temperatura.entity.TemperaturaEntity;
import com.example.aquacontrol_backend.temperatura.repository.TemperaturaRepository;
import com.example.aquacontrol_backend.temperatura.service.TemperaturaService;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AquacontrolBackendApplicationTests {

    // Simulamos los repositorios para probar la lógica sin modificar PostgreSQL.
    // JUnit crea una instancia nueva de esta clase para cada prueba.
    private final LineaRepository lineas = mock(LineaRepository.class);
    private final TemperaturaRepository temperaturas =
            mock(TemperaturaRepository.class);
    private final FlushingRepository flushing =
            mock(FlushingRepository.class);
    private final FotoRepository fotos = mock(FotoRepository.class);

    private final TemperaturaService temperaturaService =
            new TemperaturaService(temperaturas, lineas);

    // Incorporamos la dependencia de fotos que ahora necesita el servicio.
    private final FlushingService flushingService =
            new FlushingService(flushing, lineas, fotos);

    private final LocalDateTime fecha =
            LocalDateTime.of(2026, 11, 3, 10, 0);

    @Test
    void crearTemperatura() {
        when(lineas.existsById(2L)).thenReturn(true);
        when(temperaturas.save(any(TemperaturaEntity.class)))
                .thenAnswer(invocation -> {
                    TemperaturaEntity entity = invocation.getArgument(0);
                    entity.setId(1L);
                    return entity;
                });

        TemperaturaDTO dto = new TemperaturaDTO(
                null, 2L, 26.5, fecha, "Normal"
        );

        TemperaturaDTO resultado = temperaturaService.crear(dto);

        assertEquals(1L, resultado.id());
        assertEquals(26.5, resultado.temperatura());
        assertEquals(2L, resultado.lineaId());
        verify(lineas).existsById(2L);
        verify(temperaturas).save(any(TemperaturaEntity.class));
    }

    @Test
    void temperaturaNoEncontrada() {
        when(temperaturas.findById(99L)).thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> temperaturaService.obtener(99L)
        );
    }

    @Test
    void listarTemperaturasPorLinea() {
        when(temperaturas.findByLineaIdOrderByFechaHoraDesc(2L))
                .thenReturn(List.of());

        assertTrue(temperaturaService.listar(2L).isEmpty());
        verify(temperaturas).findByLineaIdOrderByFechaHoraDesc(2L);
    }

    @Test
    void crearTemperaturaConLineaInexistente() {
        when(lineas.existsById(9999L)).thenReturn(false);
        TemperaturaDTO dto = new TemperaturaDTO(
                null, 9999L, 26.5, fecha, "Prueba de integridad"
        );

        assertThrows(
                ResourceNotFoundException.class,
                () -> temperaturaService.crear(dto)
        );

        verify(lineas).existsById(9999L);
        verify(temperaturas, never()).save(any(TemperaturaEntity.class));
    }

    @Test
    void crearFlushing() {
        when(lineas.existsById(2L)).thenReturn(true);
        when(flushing.save(any(FlushingEntity.class)))
                .thenAnswer(invocation -> {
                    FlushingEntity entity = invocation.getArgument(0);
                    entity.setId(4L);
                    return entity;
                });

        FlushingDTO dto = new FlushingDTO(
                null, 2L, fecha, 120, "Limpieza", 28.5, 19.5
        );

        FlushingDTO resultado = flushingService.crear(dto);

        assertEquals(4L, resultado.id());
        assertEquals(120, resultado.duracionSegundos());
        assertEquals(2L, resultado.lineaId());

        // Comprobamos que ambas temperaturas se conserven.
        assertEquals(Double.valueOf(28.5), resultado.temperaturaAntes());
        assertEquals(Double.valueOf(19.5), resultado.temperaturaDespues());

        verify(lineas).existsById(2L);
        verify(flushing).save(any(FlushingEntity.class));
    }

    @Test
    void flushingNoEncontrado() {
        when(flushing.findById(99L)).thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> flushingService.eliminar(99L)
        );

        // Si no existe el flushing, no intentamos eliminar ninguna evidencia.
        verifyNoInteractions(fotos);
        verify(flushing, never()).delete(any(FlushingEntity.class));
    }

    @Test
    void crearFlushingConLineaInexistente() {
        when(lineas.existsById(9999L)).thenReturn(false);
        FlushingDTO dto = new FlushingDTO(
                null, 9999L, fecha, 120,
                "Prueba de integridad", null, null
        );

        assertThrows(
                ResourceNotFoundException.class,
                () -> flushingService.crear(dto)
        );

        verify(lineas).existsById(9999L);
        verify(flushing, never()).save(any(FlushingEntity.class));
    }

    @Test
    void eliminarFlushingConFoto() {
        FlushingEntity entity = new FlushingEntity();
        entity.setId(4L);
        when(flushing.findById(4L)).thenReturn(Optional.of(entity));
        when(fotos.eliminarPorFlushingId(4L)).thenReturn(1);

        flushingService.eliminar(4L);

        // La evidencia debe eliminarse antes que el registro que la sostiene.
        var orden = inOrder(fotos, flushing);
        orden.verify(fotos).eliminarPorFlushingId(4L);
        orden.verify(flushing).delete(entity);
    }

    @Test
    void eliminarFlushingSinFoto() {
        FlushingEntity entity = new FlushingEntity();
        entity.setId(5L);
        when(flushing.findById(5L)).thenReturn(Optional.of(entity));
        when(fotos.eliminarPorFlushingId(5L)).thenReturn(0);

        // Una foto opcional ausente no debe impedir eliminar el flushing.
        assertDoesNotThrow(() -> flushingService.eliminar(5L));

        verify(fotos).eliminarPorFlushingId(5L);
        verify(flushing).delete(entity);
    }
}