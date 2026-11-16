package com.example.aquacontrol_backend.granja.service;
import com.example.aquacontrol_backend.granja.dto.GranjaDTO;
import com.example.aquacontrol_backend.granja.entity.GranjaEntity;
import com.example.aquacontrol_backend.granja.repository.GranjaRepository;
import com.example.aquacontrol_backend.common.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;
@ExtendWith(MockitoExtension.class)
class GranjaServiceTest {
 @Mock GranjaRepository repository;
 @InjectMocks GranjaService service;
 private GranjaEntity entidad() { var e=new GranjaEntity();e.setId(1L);e.setNombre("Granja Norte");e.setUbicacion("Melipilla");return e; }
 @Test void listarConvierteEntidades() {when(repository.findAll()).thenReturn(List.of(entidad()));assertEquals("Granja Norte",service.listar().get(0).nombre());}
 @Test void obtenerExistente() {when(repository.findById(1L)).thenReturn(Optional.of(entidad()));assertEquals(1L,service.obtener(1L).id());}
 @Test void obtenerInexistente() {when(repository.findById(99L)).thenReturn(Optional.empty());assertThrows(ResourceNotFoundException.class,()->service.obtener(99L));}
 @Test void crearRecortaNombre() {when(repository.save(any(GranjaEntity.class))).thenAnswer(i->{GranjaEntity e=i.getArgument(0);e.setId(2L);return e;});var r=service.crear(new GranjaDTO(null," Nueva ","Sur"));assertEquals("Nueva",r.nombre());assertEquals(2L,r.id());}
 @Test void actualizarExistente() {when(repository.findById(1L)).thenReturn(Optional.of(entidad()));when(repository.save(any(GranjaEntity.class))).thenAnswer(i->i.getArgument(0));assertEquals("Actualizada",service.actualizar(1L,new GranjaDTO(null," Actualizada ","Centro")).nombre());}
 @Test void eliminarExistente() {var e=entidad();when(repository.findById(1L)).thenReturn(Optional.of(e));service.eliminar(1L);verify(repository).delete(e);}
 @Test void eliminarInexistente() {assertThrows(ResourceNotFoundException.class,()->service.eliminar(9L));verify(repository,never()).delete(any());}
}
