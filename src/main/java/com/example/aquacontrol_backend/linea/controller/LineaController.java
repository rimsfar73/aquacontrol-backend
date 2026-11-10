
package com.example.aquacontrol_backend.linea.controller;

import com.example.aquacontrol_backend.linea.dto.LineaDTO;
import com.example.aquacontrol_backend.linea.service.LineaService;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/lineas")
public class LineaController {

    private final LineaService lineaService;

    public LineaController(LineaService lineaService) {
        this.lineaService = lineaService;
    }

    // GET /api/v1/lineas
    @GetMapping
    public ResponseEntity<List<LineaDTO>> listar() {

        return ResponseEntity.ok(
                lineaService.listar()
        );
    }

    // GET /api/v1/lineas/{id}
    @GetMapping("/{id}")
    public ResponseEntity<LineaDTO> obtenerPorId(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                lineaService.obtenerPorId(id)
        );
    }

    // GET /api/v1/lineas/por-galpon/{galponId}
    @GetMapping("/por-galpon/{galponId}")
    public ResponseEntity<List<LineaDTO>> listarPorGalpon(
            @PathVariable Long galponId
    ) {

        return ResponseEntity.ok(
                lineaService.listarPorGalpon(galponId)
        );
    }

    // POST /api/v1/lineas
    @PostMapping
    public ResponseEntity<LineaDTO> crear(
            @Valid @RequestBody LineaDTO dto
    ) {

        LineaDTO creada = lineaService.crear(dto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(creada);
    }

    // PUT /api/v1/lineas/{id}
    @PutMapping("/{id}")
    public ResponseEntity<LineaDTO> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody LineaDTO dto
    ) {

        return ResponseEntity.ok(
                lineaService.actualizar(id, dto)
        );
    }

    // DELETE /api/v1/lineas/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @PathVariable Long id
    ) {

        lineaService.eliminar(id);

        return ResponseEntity.noContent().build();
    }
}
