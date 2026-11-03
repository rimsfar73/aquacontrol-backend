package com.example.aquacontrol_backend.flushing.controller;

import com.example.aquacontrol_backend.flushing.dto.FlushingDTO;
import com.example.aquacontrol_backend.flushing.service.FlushingService;
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
@RequestMapping("/api/v1/flushing")
public class FlushingController {
    private final FlushingService service;

    public FlushingController(FlushingService service) {
        this.service = service;
    }

    @GetMapping
    public List<FlushingDTO> listar(@RequestParam(required = false) @Positive Long lineaId) {
        return service.listar(lineaId);
    }

    @GetMapping("/{id}")
    public FlushingDTO obtener(@PathVariable @Positive Long id) {
        return service.obtener(id);
    }

    @PostMapping
    public ResponseEntity<FlushingDTO> crear(@Valid @RequestBody FlushingDTO dto) {
        FlushingDTO creado = service.crear(dto);
        return ResponseEntity.created(URI.create("/api/v1/flushing/" + creado.id())).body(creado);
    }

    @PutMapping("/{id}")
    public FlushingDTO actualizar(@PathVariable @Positive Long id,
                            @Valid @RequestBody FlushingDTO dto) {
        return service.actualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable @Positive Long id) {
        service.eliminar(id);
    }
}
