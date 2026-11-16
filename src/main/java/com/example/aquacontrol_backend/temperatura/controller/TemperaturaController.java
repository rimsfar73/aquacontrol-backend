package com.example.aquacontrol_backend.temperatura.controller;

import com.example.aquacontrol_backend.temperatura.dto.TemperaturaDTO;
import com.example.aquacontrol_backend.temperatura.hateoas.TemperaturaResource;
import com.example.aquacontrol_backend.temperatura.service.TemperaturaService;
import com.example.aquacontrol_backend.linea.controller.LineaController;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@Validated
@RequestMapping("/api/v1/temperatura")
public class TemperaturaController {
    private final TemperaturaService service;

    public TemperaturaController(TemperaturaService service) {
        this.service = service;
    }

    @GetMapping
    public List<TemperaturaResource> listar(@RequestParam(required = false) @Positive Long lineaId) {
        return service.listar(lineaId).stream()
                .map(this::crearResource)
                .toList();
    }

    @GetMapping("/{id}")
    public TemperaturaResource obtener(@PathVariable @Positive Long id) {
        return crearResource(service.obtener(id));
    }

    @PostMapping
    public ResponseEntity<TemperaturaResource> crear(@Valid @RequestBody TemperaturaDTO dto) {
        TemperaturaDTO creado = service.crear(dto);
        return ResponseEntity.created(URI.create("/api/v1/temperatura/" + creado.id()))
                .body(crearResource(creado));
    }

    @PutMapping("/{id}")
    public TemperaturaResource actualizar(@PathVariable @Positive Long id,
                                          @Valid @RequestBody TemperaturaDTO dto) {
        return crearResource(service.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable @Positive Long id) {
        service.eliminar(id);
    }

    private TemperaturaResource crearResource(TemperaturaDTO dto) {
        TemperaturaResource resource = new TemperaturaResource(dto);
        resource.add(
                linkTo(methodOn(TemperaturaController.class).obtener(dto.id()))
                        .withSelfRel(),
                linkTo(methodOn(LineaController.class).obtenerPorId(dto.lineaId()))
                        .withRel("linea"),
                linkTo(methodOn(TemperaturaController.class).listar(dto.lineaId()))
                        .withRel("collection")
        );
        return resource;
    }
}
