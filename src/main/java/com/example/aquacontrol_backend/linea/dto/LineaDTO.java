
package com.example.aquacontrol_backend.linea.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record LineaDTO(

        Long id,

        @NotBlank(message = "El nombre de la línea es obligatorio")
        @Size(
                max = 120,
                message = "El nombre no puede superar 120 caracteres"
        )
        String nombre,

        @NotNull(message = "El ID del galpón es obligatorio")
        @Positive(message = "El ID del galpón debe ser positivo")
        Long galponId

) {
}

