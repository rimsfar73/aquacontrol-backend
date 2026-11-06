package com.example.aquacontrol_backend.flushing.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

// Definimos los datos que intercambiamos entre Android y el backend.
// El Controller recibe este formato y el Service lo convierte en una entidad.
public record FlushingDTO(

        // El servidor genera el ID cuando creamos un registro.
        Long id,

        @NotNull
        @Positive
        Long lineaId,

        @NotNull
        LocalDateTime fechaHora,

        @NotNull
        @Positive
        Integer duracionSegundos,

        @Size(max = 500)
        String observacion,

        // Incorporamos las temperaturas que ya registra nuestra app.
        // Admitimos null para mantener compatibles los registros anteriores.
        Double temperaturaAntes,
        Double temperaturaDespues
) {

    // Conservamos las llamadas existentes que todavía utilizan cinco datos.
    // En esos casos dejamos las temperaturas sin informar, nunca inventamos un cero.
    public FlushingDTO(
            Long id,
            Long lineaId,
            LocalDateTime fechaHora,
            Integer duracionSegundos,
            String observacion
    ) {
        this(
                id,
                lineaId,
                fechaHora,
                duracionSegundos,
                observacion,
                null,
                null
        );
    }
}