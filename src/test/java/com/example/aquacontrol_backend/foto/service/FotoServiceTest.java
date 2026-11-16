package com.example.aquacontrol_backend.foto.service;
import com.example.aquacontrol_backend.foto.entity.FotoEntity;
import com.example.aquacontrol_backend.foto.repository.FotoRepository;
import com.example.aquacontrol_backend.flushing.entity.FlushingEntity;
import com.example.aquacontrol_backend.flushing.repository.FlushingRepository;
import com.example.aquacontrol_backend.common.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;
@ExtendWith(MockitoExtension.class)
class FotoServiceTest {
 @Mock FotoRepository fotos;
 @Mock FlushingRepository flushings;
 @InjectMocks FotoService service;
 @Test void obtenerFotoExistente(){var foto=new FotoEntity();when(fotos.findById(1L)).thenReturn(Optional.of(foto));assertSame(foto,service.obtener(1L));}
 @Test void obtenerFotoInexistente(){assertThrows(ResourceNotFoundException.class,()->service.obtener(9L));}
 @Test void guardarSinFlushing(){assertThrows(ResourceNotFoundException.class,()->service.guardar(9L,new MockMultipartFile("archivo","foto.png","image/png",new byte[]{1})));}
 @Test void guardarArchivoVacio(){when(flushings.findById(1L)).thenReturn(Optional.of(new FlushingEntity()));assertThrows(IllegalArgumentException.class,()->service.guardar(1L,new MockMultipartFile("archivo",new byte[0])));verify(fotos,never()).save(any());}
 @Test void guardarArchivoNulo(){when(flushings.findById(1L)).thenReturn(Optional.of(new FlushingEntity()));assertThrows(IllegalArgumentException.class,()->service.guardar(1L,null));}
 @Test void rechazarArchivoQueNoEsImagen(){when(flushings.findById(1L)).thenReturn(Optional.of(new FlushingEntity()));var archivo=new MockMultipartFile("archivo","falso.png","image/png","texto".getBytes());assertThrows(IllegalArgumentException.class,()->service.guardar(1L,archivo));verify(fotos,never()).save(any());}
 @Test void rechazarArchivoMuyGrande(){when(flushings.findById(1L)).thenReturn(Optional.of(new FlushingEntity()));var archivo=new MockMultipartFile("archivo","grande.png","image/png",new byte[5*1024*1024+1]);assertThrows(IllegalArgumentException.class,()->service.guardar(1L,archivo));verify(fotos,never()).save(any());}
}
