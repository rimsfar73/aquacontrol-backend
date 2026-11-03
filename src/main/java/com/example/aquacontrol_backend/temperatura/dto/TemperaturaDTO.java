package com.example.aquacontrol_backend.temperatura.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

public record TemperaturaDTO(
        Long id,
        @NotNull @Positive Long lineaId,
        @NotNull @DecimalMin("-30.0") @DecimalMax("100.0") Double temperatura,
        @NotNull LocalDateTime fechaHora,
        @Size(max = 500) String observacion
) {}
