
package com.example.aquacontrol_backend.foto.controller;

import com.example.aquacontrol_backend.foto.dto.FotoDTO;
import com.example.aquacontrol_backend.foto.service.FotoService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/fotos")
public class FotoController {

    private final FotoService service;

    public FotoController(FotoService service) {
        this.service = service;
    }

    @GetMapping
    public List<FotoDTO> listar(
            @RequestParam(required = false) Long flushingId
    ) {
        return service.listar(flushingId);
    }

    @GetMapping("/{id}")
    public FotoDTO obtener(@PathVariable Long id) {
        return service.obtener(id);
    }

    @GetMapping("/por-flushing/{flushingId}")
    public List<FotoDTO> listarPorFlushing(
            @PathVariable Long flushingId
    ) {
        return service.listar(flushingId);
    }

    @PostMapping
    public ResponseEntity<FotoDTO> crear(
            @RequestBody FotoDTO dto
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(service.crear(dto));
    }

    @PutMapping("/{id}")
    public FotoDTO actualizar(
            @PathVariable Long id,
            @RequestBody FotoDTO dto
    ) {
        return service.actualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @PathVariable Long id
    ) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
