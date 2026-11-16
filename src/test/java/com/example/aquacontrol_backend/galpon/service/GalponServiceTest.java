package com.example.aquacontrol_backend.galpon.service;

import com.example.aquacontrol_backend.common.exception.ResourceNotFoundException;
import com.example.aquacontrol_backend.galpon.dto.GalponDTO;
import com.example.aquacontrol_backend.galpon.entity.GalponEntity;
import com.example.aquacontrol_backend.galpon.repository.GalponRepository;
import com.example.aquacontrol_backend.granja.entity.GranjaEntity;
import com.example.aquacontrol_backend.granja.repository.GranjaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class GalponServiceTest {
    private GalponRepository galpones;
    private GranjaRepository granjas;
    private GalponService servicio;

    @BeforeEach void preparar() {
        galpones = mock(GalponRepository.class);
        granjas = mock(GranjaRepository.class);
        servicio = new GalponService(galpones, granjas);
    }
    private GranjaEntity granja() {
        GranjaEntity g = new GranjaEntity(); g.setId(2L); g.setNombre("Granja A"); return g;
    }
    private GalponEntity galpon() {
        GalponEntity g = new GalponEntity(); g.setId(7L); g.setNombre("Galpon A"); g.setGranja(granja()); return g;
    }
    @Test void listarConvierteEntidadesEnDtos() {
        when(galpones.findAll()).thenReturn(List.of(galpon()));
        List<GalponDTO> lista = servicio.listar();
        assertEquals(1, lista.size()); assertEquals(7L, lista.get(0).id());
        assertEquals(2L, lista.get(0).granjaId());
    }
    @Test void listarVacio() {
        when(galpones.findAll()).thenReturn(List.of());
        assertTrue(servicio.listar().isEmpty());
    }
    @Test void listarPorGranja() {
        when(granjas.findById(2L)).thenReturn(Optional.of(granja()));
        when(galpones.findByGranja_Id(2L)).thenReturn(List.of(galpon()));
        assertEquals(1, servicio.listarPorGranja(2L).size());
        verify(galpones).findByGranja_Id(2L);
    }
    @Test void listarPorGranjaInexistente() {
        assertThrows(ResourceNotFoundException.class, () -> servicio.listarPorGranja(99L));
        verify(galpones, never()).findByGranja_Id(any());
    }
    @Test void obtenerExistente() {
        when(galpones.findById(7L)).thenReturn(Optional.of(galpon()));
        assertEquals("Galpon A", servicio.obtener(7L).nombre());
    }
    @Test void obtenerInexistente() {
        assertThrows(ResourceNotFoundException.class, () -> servicio.obtener(99L));
    }
    @Test void crearRecortaNombreYAsociaGranja() {
        when(granjas.findById(2L)).thenReturn(Optional.of(granja()));
        when(galpones.save(any(GalponEntity.class))).thenAnswer(i -> {
            GalponEntity g=i.getArgument(0); g.setId(7L); return g;
        });
        GalponDTO creado = servicio.crear(new GalponDTO(null, "  Nuevo  ", 2L));
        assertEquals("Nuevo", creado.nombre()); assertEquals(2L, creado.granjaId());
        ArgumentCaptor<GalponEntity> captor = ArgumentCaptor.forClass(GalponEntity.class);
        verify(galpones).save(captor.capture());
        assertEquals("Nuevo", captor.getValue().getNombre());
    }
    @Test void crearConGranjaInexistente() {
        assertThrows(ResourceNotFoundException.class, () -> servicio.crear(new GalponDTO(null,"Nuevo",99L)));
        verify(galpones, never()).save(any());
    }
    @Test void actualizarNombreYGranja() {
        GalponEntity actual=galpon();
        GranjaEntity nueva=granja(); nueva.setId(3L);
        when(galpones.findById(7L)).thenReturn(Optional.of(actual));
        when(granjas.findById(3L)).thenReturn(Optional.of(nueva));
        when(galpones.save(any(GalponEntity.class))).thenAnswer(i -> i.getArgument(0));
        GalponDTO dto=servicio.actualizar(7L,new GalponDTO(null,"  Actualizado ",3L));
        assertEquals("Actualizado",dto.nombre()); assertEquals(3L,dto.granjaId());
    }
    @Test void actualizarGalponInexistente() {
        assertThrows(ResourceNotFoundException.class, () -> servicio.actualizar(99L,new GalponDTO(null,"A",2L)));
        verify(galpones,never()).save(any());
    }
    @Test void actualizarGranjaInexistente() {
        when(galpones.findById(7L)).thenReturn(Optional.of(galpon()));
        assertThrows(ResourceNotFoundException.class, () -> servicio.actualizar(7L,new GalponDTO(null,"A",99L)));
        verify(galpones,never()).save(any());
    }
    @Test void eliminarExistente() {
        GalponEntity g=galpon(); when(galpones.findById(7L)).thenReturn(Optional.of(g));
        servicio.eliminar(7L); verify(galpones).delete(g);
    }
    @Test void eliminarInexistente() {
        assertThrows(ResourceNotFoundException.class, () -> servicio.eliminar(99L));
        verify(galpones,never()).delete(any());
    }
}
