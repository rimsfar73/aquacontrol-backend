
package com.example.aquacontrol_backend.granja.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record GranjaDTO(

        Long id,

        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 120)
        String nombre,

        @Size(max = 250)
        String ubicacion
) {
}
