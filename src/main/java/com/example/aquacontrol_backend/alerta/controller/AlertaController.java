package com.example.aquacontrol_backend.alerta.controller;

import com.example.aquacontrol_backend.alerta.dto.AlertaDTO;
import com.example.aquacontrol_backend.alerta.service.AlertaService;
import com.example.aquacontrol_backend.temperatura.controller.TemperaturaController;
import jakarta.validation.constraints.Positive;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@Validated
@RequestMapping("/api/v1/alertas")
public class AlertaController {

    private final AlertaService service;

    public AlertaController(AlertaService service) {
        this.service = service;
    }

    @GetMapping
    public CollectionModel<EntityModel<AlertaDTO>> listar(
            @RequestParam(required = false) @Positive Long lineaId
    ) {
        List<EntityModel<AlertaDTO>> alertas = service.listar(lineaId)
                .stream()
                .map(this::crearModelo)
                .toList();

        return CollectionModel.of(
                alertas,
                linkTo(methodOn(AlertaController.class).listar(lineaId))
                        .withSelfRel()
        );
    }

    @GetMapping("/{medicionId}")
    public EntityModel<AlertaDTO> obtener(
            @PathVariable @Positive Long medicionId
    ) {
        return crearModelo(service.obtener(medicionId));
    }

    private EntityModel<AlertaDTO> crearModelo(AlertaDTO alerta) {
        return EntityModel.of(
                alerta,
                linkTo(AlertaController.class)
                        .slash(alerta.medicionId())
                        .withSelfRel(),
                linkTo(TemperaturaController.class)
                        .slash(alerta.medicionId())
                        .withRel("medicion")
        );
    }
}