
package com.example.aquacontrol_backend.foto.service;

import com.example.aquacontrol_backend.common.exception.ResourceNotFoundException;
import com.example.aquacontrol_backend.flushing.entity.FlushingEntity;
import com.example.aquacontrol_backend.flushing.repository.FlushingRepository;
import com.example.aquacontrol_backend.foto.dto.FotoDTO;
import com.example.aquacontrol_backend.foto.entity.FotoEntity;
import com.example.aquacontrol_backend.foto.repository.FotoRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FotoServiceCoberturaExtraTest {

    @Mock
    FotoRepository fotoRepository;

    @Mock
    FlushingRepository flushingRepository;

    @InjectMocks
    FotoService servicio;

    private final long id = 7L;
    private FlushingEntity flushing;

    @BeforeEach
    void preparar() {
        flushing = new FlushingEntity();
    }

    private byte[] imagen(String formato) throws IOException {
        BufferedImage imagen = new BufferedImage(
                3, 2, BufferedImage.TYPE_INT_RGB
        );

        ByteArrayOutputStream salida = new ByteArrayOutputStream();

        assertTrue(ImageIO.write(imagen, formato, salida));

        return salida.toByteArray();
    }

    private MockMultipartFile archivo(byte[] bytes) {
        return new MockMultipartFile(
                "archivo",
                "foto.bin",
                "application/octet-stream",
                bytes
        );
    }

    private void flushingExiste() {
        when(flushingRepository.findById(id))
                .thenReturn(Optional.of(flushing));
    }

    @Test
    void guardaPngYDetectaFormatoRealAunqueMimeSeaIncorrecto()
            throws Exception {

        flushingExiste();

        byte[] bytes = imagen("png");

        FotoDTO resultado = servicio.guardar(id, archivo(bytes));

        assertEquals(id, resultado.flushingId());
        assertEquals("image/png", resultado.tipoContenido());
        assertEquals(bytes.length, resultado.tamanioBytes());

        ArgumentCaptor<FotoEntity> captor =
                ArgumentCaptor.forClass(FotoEntity.class);

        verify(fotoRepository).save(captor.capture());

        assertSame(
                flushing,
                captor.getValue().getFlushing()
        );

        assertArrayEquals(
                bytes,
                captor.getValue().getContenido()
        );
    }

    @Test
    void guardaJpegYDetectaFormatoReal() throws Exception {

        flushingExiste();

        byte[] bytes = imagen("jpeg");

        FotoDTO resultado = servicio.guardar(id, archivo(bytes));

        assertEquals("image/jpeg", resultado.tipoContenido());

        verify(fotoRepository).save(any(FotoEntity.class));
    }

    @Test
    void reemplazaFotoExistenteSinCrearOtra() throws Exception {

        flushingExiste();

        FotoEntity existente = new FotoEntity();
        existente.setTipoContenido("image/jpeg");
        existente.setContenido(new byte[]{1, 2});

        when(fotoRepository.findById(id))
                .thenReturn(Optional.of(existente));

        byte[] bytes = imagen("png");

        servicio.guardar(id, archivo(bytes));

        verify(fotoRepository).save(same(existente));

        assertEquals(
                "image/png",
                existente.getTipoContenido()
        );

        assertArrayEquals(
                bytes,
                existente.getContenido()
        );
    }

    @Test
    void noGuardaSiFlushingNoExiste() throws Exception {

        when(flushingRepository.findById(id))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> servicio.guardar(id, archivo(imagen("png")))
        );

        verifyNoInteractions(fotoRepository);
    }

    @Test
    void rechazaArchivoNulo() {

        flushingExiste();

        assertThrows(
                IllegalArgumentException.class,
                () -> servicio.guardar(id, null)
        );

        verifyNoInteractions(fotoRepository);
    }

    @Test
    void rechazaArchivoVacio() {

        flushingExiste();

        assertThrows(
                IllegalArgumentException.class,
                () -> servicio.guardar(id, archivo(new byte[0]))
        );

        verifyNoInteractions(fotoRepository);
    }

    @Test
    void rechazaArchivoSuperiorACincoMiB() throws IOException {

        flushingExiste();

        MultipartFile archivo = mock(MultipartFile.class);

        when(archivo.isEmpty()).thenReturn(false);

        when(archivo.getSize())
                .thenReturn(5L * 1024 * 1024 + 1);

        assertThrows(
                IllegalArgumentException.class,
                () -> servicio.guardar(id, archivo)
        );

        verify(archivo, never()).getBytes();

        verifyNoInteractions(fotoRepository);
    }

    @Test
    void rechazaErrorDeLecturaSinGuardar() throws Exception {

        flushingExiste();

        MultipartFile archivo = mock(MultipartFile.class);

        when(archivo.isEmpty()).thenReturn(false);
        when(archivo.getSize()).thenReturn(10L);

        when(archivo.getBytes())
                .thenThrow(new IOException("lectura fallida"));

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> servicio.guardar(id, archivo)
        );

        assertInstanceOf(IOException.class, ex.getCause());

        verifyNoInteractions(fotoRepository);
    }

    @Test
    void rechazaTextoDisfrazadoDeImagen() {

        flushingExiste();

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> servicio.guardar(
                        id,
                        archivo("esto no es una imagen".getBytes())
                )
        );

        assertTrue(
                ex.getMessage().contains("JPEG o PNG")
                        || ex.getMessage().contains("válida")
        );

        verifyNoInteractions(fotoRepository);
    }

    @Test
    void rechazaFormatoGifAunqueSeaImagenValida()
            throws Exception {

        flushingExiste();

        byte[] bytes = imagen("gif");

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> servicio.guardar(id, archivo(bytes))
        );

        assertTrue(
                ex.getMessage().contains("JPEG o PNG")
        );

        verifyNoInteractions(fotoRepository);
    }

    @Test
    void obtieneFotoExistente() {

        FotoEntity foto = new FotoEntity();

        when(fotoRepository.findById(id))
                .thenReturn(Optional.of(foto));

        assertSame(foto, servicio.obtener(id));
    }

    @Test
    void obtenerFotoInexistenteLanza404Logico() {

        when(fotoRepository.findById(id))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> servicio.obtener(id)
        );
    }
}
