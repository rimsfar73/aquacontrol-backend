package com.example.aquacontrol_backend.foto.controller;

import com.example.aquacontrol_backend.foto.dto.FotoDTO;
import com.example.aquacontrol_backend.foto.entity.FotoEntity;
import com.example.aquacontrol_backend.foto.service.FotoService;
import jakarta.validation.constraints.Positive;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

// Asociamos la dirección de la foto con el flushing al que pertenece.
@RestController
@Validated
@RequestMapping("/api/v1/flushing/{flushingId}/foto")
public class FotoController {

    private final FotoService service;

    // Recibimos el servicio que valida y guarda las imágenes.
    public FotoController(FotoService service) {
        this.service = service;
    }

    // Recibimos un formulario con un archivo llamado "foto".
    // Guardamos la evidencia o reemplazamos la existente.
    @PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public FotoDTO guardar(
            @PathVariable("flushingId") @Positive Long flushingId,
            @RequestPart("foto") MultipartFile foto
    ) {
        return service.guardar(flushingId, foto);
    }

    // Devolvemos la imagen para visualizarla en el detalle de Android.
    // La entidad y sus relaciones no se envían como JSON.
    @GetMapping
    public ResponseEntity<byte[]> obtener(
            @PathVariable("flushingId") @Positive Long flushingId
    ) {
        FotoEntity foto = service.obtener(flushingId);
        byte[] contenido = foto.getContenido();

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(foto.getTipoContenido()))
                .contentLength(contenido.length)
                // Evitamos reutilizar una imagen antigua después de reemplazarla.
                .cacheControl(CacheControl.noStore())
                .body(contenido);
    }
}