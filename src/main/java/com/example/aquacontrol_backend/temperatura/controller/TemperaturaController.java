package com.example.aquacontrol_backend.temperatura.controller;

import com.example.aquacontrol_backend.temperatura.dto.TemperaturaDTO;
import com.example.aquacontrol_backend.temperatura.service.TemperaturaService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
import java.util.List;

@RestController
@Validated
@RequestMapping("/api/v1/temperatura")
public class TemperaturaController {
    private final TemperaturaService service;

    public TemperaturaController(TemperaturaService service) {
        this.service = service;
    }

    @GetMapping
    public List<TemperaturaDTO> listar(@RequestParam(required = false) @Positive Long lineaId) {
        return service.listar(lineaId);
    }

    @GetMapping("/{id}")
    public TemperaturaDTO obtener(@PathVariable @Positive Long id) {
        return service.obtener(id);
    }

    @PostMapping
    public ResponseEntity<TemperaturaDTO> crear(@Valid @RequestBody TemperaturaDTO dto) {
        TemperaturaDTO creado = service.crear(dto);
        return ResponseEntity.created(URI.create("/api/v1/temperatura/" + creado.id())).body(creado);
    }

    @PutMapping("/{id}")
    public TemperaturaDTO actualizar(@PathVariable @Positive Long id,
                            @Valid @RequestBody TemperaturaDTO dto) {
        return service.actualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable @Positive Long id) {
        service.eliminar(id);
    }
}
