package com.example.aquacontrol_backend.foto.service;

import com.example.aquacontrol_backend.common.exception.ResourceNotFoundException;
import com.example.aquacontrol_backend.flushing.entity.FlushingEntity;
import com.example.aquacontrol_backend.flushing.repository.FlushingRepository;
import com.example.aquacontrol_backend.foto.dto.FotoDTO;
import com.example.aquacontrol_backend.foto.entity.FotoEntity;
import com.example.aquacontrol_backend.foto.repository.FotoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Locale;

@Service
@Transactional(readOnly = true)
public class FotoService {

    // Limitamos el peso y las dimensiones para controlar los recursos utilizados.
    private static final long MAX_BYTES = 5L * 1024 * 1024;
    private static final long MAX_PIXELES = 25_000_000L;

    private final FotoRepository fotoRepository;
    private final FlushingRepository flushingRepository;

    // Recibimos los repositorios mediante Spring.
    public FotoService(
            FotoRepository fotoRepository,
            FlushingRepository flushingRepository
    ) {
        this.fotoRepository = fotoRepository;
        this.flushingRepository = flushingRepository;
    }

    // Conservamos la comprobación de que el flushing exista.
    // Guardamos la imagen o reemplazamos la evidencia anterior.
    @Transactional
    public FotoDTO guardar(Long flushingId, MultipartFile archivo) {
        FlushingEntity flushing = flushingRepository.findById(flushingId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Flushing no encontrado: " + flushingId
                ));

        if (archivo == null || archivo.isEmpty()) {
            throw new IllegalArgumentException("Debes seleccionar una foto.");
        }

        if (archivo.getSize() > MAX_BYTES) {
            throw new IllegalArgumentException(
                    "La foto no puede superar los 5 MiB."
            );
        }

        byte[] contenido;
        try {
            contenido = archivo.getBytes();
        } catch (IOException ex) {
            throw new IllegalArgumentException(
                    "No se pudo leer la foto enviada.", ex
            );
        }

        // Validamos la imagen antes de modificar la evidencia existente.
        String tipoContenido = validarImagen(contenido);

        FotoEntity foto = fotoRepository.findById(flushingId)
                .orElseGet(() -> {
                    FotoEntity nueva = new FotoEntity();
                    nueva.setFlushing(flushing);
                    return nueva;
                });

        foto.setTipoContenido(tipoContenido);
        foto.setContenido(contenido);
        fotoRepository.save(foto);

        return new FotoDTO(flushingId, tipoContenido, contenido.length);
    }

    // Recuperamos los bytes y su formato para responder desde el Controller.
    public FotoEntity obtener(Long flushingId) {
        return fotoRepository.findById(flushingId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Este flushing no tiene foto de evidencia."
                ));
    }

    // Comprobamos el contenido real, aunque el archivo tenga otra extensión.
    private String validarImagen(byte[] contenido) {
        try (
                ByteArrayInputStream bytes = new ByteArrayInputStream(contenido);
                ImageInputStream entrada = ImageIO.createImageInputStream(bytes)
        ) {
            if (entrada == null) {
                throw new IllegalArgumentException("La imagen no es válida.");
            }

            Iterator<ImageReader> lectores = ImageIO.getImageReaders(entrada);
            if (!lectores.hasNext()) {
                throw new IllegalArgumentException(
                        "El archivo debe ser una imagen JPEG o PNG."
                );
            }

            ImageReader lector = lectores.next();
            try {
                lector.setInput(entrada, true, true);

                String formato = lector.getFormatName()
                        .toLowerCase(Locale.ROOT);

                String tipoContenido = switch (formato) {
                    case "jpeg", "jpg" -> "image/jpeg";
                    case "png" -> "image/png";
                    default -> throw new IllegalArgumentException(
                            "Solo admitimos imágenes JPEG o PNG."
                    );
                };

                // Revisamos las dimensiones antes de decodificar la imagen.
                int ancho = lector.getWidth(0);
                int alto = lector.getHeight(0);

                if (ancho <= 0 || alto <= 0
                        || (long) ancho * alto > MAX_PIXELES) {
                    throw new IllegalArgumentException(
                            "Las dimensiones no son válidas "
                                    + "o superan los 25 megapíxeles."
                    );
                }

                var imagen = lector.read(0);
                if (imagen == null) {
                    throw new IllegalArgumentException(
                            "No se pudo interpretar la imagen."
                    );
                }

                // Liberamos la imagen decodificada; guardamos los bytes originales.
                imagen.flush();
                return tipoContenido;
            } finally {
                lector.dispose();
            }
        } catch (IOException ex) {
            throw new IllegalArgumentException(
                    "La imagen está dañada o no se puede leer.", ex
            );
        }
    }
}