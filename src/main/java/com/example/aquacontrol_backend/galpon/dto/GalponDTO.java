
package com.example.aquacontrol_backend.galpon.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record GalponDTO(

        Long id,

        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 120)
        String nombre,

        @NotNull(message = "La granja es obligatoria")
        @Positive(message = "El ID de la granja debe ser positivo")
        Long granjaId
) {
}
