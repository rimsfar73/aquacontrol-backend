package com.example.aquacontrol_backend.flushing.controller;

import com.example.aquacontrol_backend.flushing.dto.FlushingDTO;
import com.example.aquacontrol_backend.flushing.hateoas.FlushingResource;
import com.example.aquacontrol_backend.flushing.service.FlushingService;
import com.example.aquacontrol_backend.foto.controller.FotoController;
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
@RequestMapping("/api/v1/flushing")
public class FlushingController {
    private final FlushingService service;

    public FlushingController(FlushingService service) {
        this.service = service;
    }

    @GetMapping
    public List<FlushingResource> listar(@RequestParam(required = false) @Positive Long lineaId) {
        return service.listar(lineaId).stream()
                .map(this::crearResource)
                .toList();
    }

    @GetMapping("/{id}")
    public FlushingResource obtener(@PathVariable @Positive Long id) {
        return crearResource(service.obtener(id));
    }

    @PostMapping
    public ResponseEntity<FlushingResource> crear(@Valid @RequestBody FlushingDTO dto) {
        FlushingDTO creado = service.crear(dto);
        return ResponseEntity.created(URI.create("/api/v1/flushing/" + creado.id()))
                .body(crearResource(creado));
    }

    @PutMapping("/{id}")
    public FlushingResource actualizar(@PathVariable @Positive Long id,
                                       @Valid @RequestBody FlushingDTO dto) {
        return crearResource(service.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable @Positive Long id) {
        service.eliminar(id);
    }

    private FlushingResource crearResource(FlushingDTO dto) {
        FlushingResource resource = new FlushingResource(dto);
        resource.add(
                linkTo(methodOn(FlushingController.class).obtener(dto.id()))
                        .withSelfRel(),
                linkTo(methodOn(LineaController.class).obtenerPorId(dto.lineaId()))
                        .withRel("linea"),
                linkTo(methodOn(FotoController.class).obtener(dto.id()))
                        .withRel("foto"),
                linkTo(methodOn(FlushingController.class).listar(dto.lineaId()))
                        .withRel("collection")
        );
        return resource;
    }
}
