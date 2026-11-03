package com.example.aquacontrol_backend.flushing.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record FlushingDTO(
        Long id,
        @NotNull @Positive Long lineaId,
        @NotNull LocalDateTime fechaHora,
        @NotNull @Positive Integer duracionSegundos,
        @Size(max = 500) String observacion
) {}
