package com.example.aquacontrol_backend.alerta.service;
import com.example.aquacontrol_backend.temperatura.entity.TemperaturaEntity;
import com.example.aquacontrol_backend.temperatura.repository.TemperaturaRepository;
import com.example.aquacontrol_backend.common.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDateTime;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class AlertaServiceTest {
 @Mock TemperaturaRepository repository;
 @InjectMocks AlertaService service;
 private TemperaturaEntity medicion(long id,long linea,double temp){var m=new TemperaturaEntity();m.setId(id);m.setLineaId(linea);m.setTemperatura(temp);m.setFechaHora(LocalDateTime.of(2026,11,16,12,0));return m;}
 @Test void temperaturaNormalNoGeneraAlerta(){when(repository.findUltimasPorLinea()).thenReturn(List.of(medicion(1,1,20)));assertTrue(service.listar(null).isEmpty());}
 @Test void temperaturaAltaEsCritica(){when(repository.findUltimasPorLinea()).thenReturn(List.of(medicion(1,1,30)));assertEquals("CRITICO",service.listar(1L).get(0).nivel());}
 @Test void temperaturaMuyBajaEsCritica(){when(repository.findUltimasPorLinea()).thenReturn(List.of(medicion(1,1,4.9)));assertEquals("CRITICO",service.listar(1L).get(0).nivel());}
 @Test void temperaturaIntermediaEsAdvertencia(){when(repository.findUltimasPorLinea()).thenReturn(List.of(medicion(1,1,22)));assertEquals("ADVERTENCIA",service.listar(1L).get(0).nivel());}
 @Test void filtraPorLinea(){when(repository.findUltimasPorLinea()).thenReturn(List.of(medicion(1,1,30),medicion(2,2,31)));assertEquals(1,service.listar(2L).size());}
 @Test void obtenerAlerta(){when(repository.findUltimasPorLinea()).thenReturn(List.of(medicion(7,1,31)));assertEquals(7L,service.obtener(7L).medicionId());}
 @Test void obtenerMedicionNormalFalla(){when(repository.findUltimasPorLinea()).thenReturn(List.of(medicion(7,1,20)));assertThrows(ResourceNotFoundException.class,()->service.obtener(7L));}
 @Test void obtenerInexistenteFalla(){when(repository.findUltimasPorLinea()).thenReturn(List.of());assertThrows(ResourceNotFoundException.class,()->service.obtener(99L));}
}
