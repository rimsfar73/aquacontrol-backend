
package com.example.aquacontrol_backend.granja.controller;

import com.example.aquacontrol_backend.granja.dto.GranjaDTO;
import com.example.aquacontrol_backend.granja.service.GranjaService;

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
@RequestMapping("/api/v1/granjas")
public class GranjaController {

    private final GranjaService service;

    public GranjaController(GranjaService service) {
        this.service = service;
    }

    @GetMapping
    public List<GranjaDTO> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public GranjaDTO obtener(@PathVariable @Positive Long id) {
        return service.obtener(id);
    }

    @PostMapping
    public ResponseEntity<GranjaDTO> crear(
            @Valid @RequestBody GranjaDTO dto
    ) {
        GranjaDTO creado = service.crear(dto);

        return ResponseEntity
                .created(URI.create(
                        "/api/v1/granjas/" + creado.id()
                ))
                .body(creado);
    }

    @PutMapping("/{id}")
    public GranjaDTO actualizar(
            @PathVariable @Positive Long id,
            @Valid @RequestBody GranjaDTO dto
    ) {
        return service.actualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable @Positive Long id) {
        service.eliminar(id);
    }
}

