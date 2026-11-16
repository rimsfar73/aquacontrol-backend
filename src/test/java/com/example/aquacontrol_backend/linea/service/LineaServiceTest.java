package com.example.aquacontrol_backend.linea.service;
import com.example.aquacontrol_backend.linea.dto.LineaDTO;
import com.example.aquacontrol_backend.linea.entity.LineaEntity;
import com.example.aquacontrol_backend.linea.repository.LineaRepository;
import com.example.aquacontrol_backend.galpon.entity.GalponEntity;
import com.example.aquacontrol_backend.galpon.repository.GalponRepository;
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
class LineaServiceTest {
 @Mock LineaRepository lineas;
 @Mock GalponRepository galpones;
 @InjectMocks LineaService service;
 private GalponEntity galpon(){var g=new GalponEntity();g.setId(3L);return g;}
 private LineaEntity linea(){var l=new LineaEntity();l.setId(1L);l.setNombre("L1");l.setGalpon(galpon());return l;}
 @Test void listar(){when(lineas.findAll()).thenReturn(List.of(linea()));assertEquals(3L,service.listar().get(0).galponId());}
 @Test void obtener(){when(lineas.findById(1L)).thenReturn(Optional.of(linea()));assertEquals("L1",service.obtenerPorId(1L).nombre());}
 @Test void obtenerInexistente(){assertThrows(ResourceNotFoundException.class,()->service.obtenerPorId(99L));}
 @Test void listarPorGalpon(){when(galpones.existsById(3L)).thenReturn(true);when(lineas.findByGalpon_Id(3L)).thenReturn(List.of(linea()));assertEquals(1,service.listarPorGalpon(3L).size());}
 @Test void listarPorGalponInexistente(){assertThrows(ResourceNotFoundException.class,()->service.listarPorGalpon(99L));}
 @Test void crear(){when(galpones.findById(3L)).thenReturn(Optional.of(galpon()));when(lineas.save(any(LineaEntity.class))).thenAnswer(i->{LineaEntity l=i.getArgument(0);l.setId(2L);return l;});assertEquals(2L,service.crear(new LineaDTO(null,"Nueva",3L)).id());}
 @Test void crearSinGalpon(){assertThrows(ResourceNotFoundException.class,()->service.crear(new LineaDTO(null,"Nueva",99L)));verify(lineas,never()).save(any());}
 @Test void actualizar(){when(lineas.findById(1L)).thenReturn(Optional.of(linea()));when(galpones.findById(3L)).thenReturn(Optional.of(galpon()));when(lineas.save(any(LineaEntity.class))).thenAnswer(i->i.getArgument(0));assertEquals("Editada",service.actualizar(1L,new LineaDTO(null,"Editada",3L)).nombre());}
 @Test void eliminar(){var l=linea();when(lineas.findById(1L)).thenReturn(Optional.of(l));service.eliminar(1L);verify(lineas).delete(l);}
}
