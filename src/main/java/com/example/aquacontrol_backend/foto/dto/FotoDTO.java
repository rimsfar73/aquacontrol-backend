package com.example.aquacontrol_backend.foto.dto;

// Informamos a Android qué evidencia quedó guardada.
// Entregamos la imagen por separado cuando se solicita visualizarla.
public record FotoDTO(

        // Identificamos el flushing al que pertenece la evidencia.
        Long flushingId,

        // Indicamos el formato real de la imagen: JPEG o PNG.
        String tipoContenido,

        // Informamos el tamaño de la imagen guardada, en bytes.
        long tamanioBytes
) {
}