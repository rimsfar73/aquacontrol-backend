
package com.example.aquacontrol_backend.galpon.controller;

import com.example.aquacontrol_backend.galpon.dto.GalponDTO;
import com.example.aquacontrol_backend.galpon.service.GalponService;

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
@RequestMapping("/api/v1/galpones")
public class GalponController {

    private final GalponService service;

    public GalponController(GalponService service) {
        this.service = service;
    }

    @GetMapping
    public List<GalponDTO> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public GalponDTO obtener(
            @PathVariable @Positive Long id
    ) {
        return service.obtener(id);
    }

    @GetMapping("/por-granja/{granjaId}")
    public List<GalponDTO> listarPorGranja(
            @PathVariable @Positive Long granjaId
    ) {
        return service.listarPorGranja(granjaId);
    }

    @PostMapping
    public ResponseEntity<GalponDTO> crear(
            @Valid @RequestBody GalponDTO dto
    ) {
        GalponDTO creado = service.crear(dto);

        return ResponseEntity
                .created(URI.create(
                        "/api/v1/galpones/" + creado.id()
                ))
                .body(creado);
    }

    @PutMapping("/{id}")
    public GalponDTO actualizar(
            @PathVariable @Positive Long id,
            @Valid @RequestBody GalponDTO dto
    ) {
        return service.actualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(
            @PathVariable @Positive Long id
    ) {
        service.eliminar(id);
    }
}
