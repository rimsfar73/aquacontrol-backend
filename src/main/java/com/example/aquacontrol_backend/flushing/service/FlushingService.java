package com.example.aquacontrol_backend.flushing.service;

import com.example.aquacontrol_backend.common.exception.ResourceNotFoundException;
import com.example.aquacontrol_backend.flushing.dto.FlushingDTO;
import com.example.aquacontrol_backend.flushing.entity.FlushingEntity;
import com.example.aquacontrol_backend.flushing.repository.FlushingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

// Coordinamos las consultas y los cambios de flushing.
// Conectamos el Controller con el Repository que accede a PostgreSQL.
@Service
@Transactional(readOnly = true)
public class FlushingService {

    private final FlushingRepository repository;

    // Recibimos el repositorio mediante Spring, sin crearlo manualmente.
    public FlushingService(FlushingRepository repository) {
        this.repository = repository;
    }

    // Consultamos todos los registros o solo los de una línea.
    public List<FlushingDTO> listar(Long lineaId) {
        List<FlushingEntity> entities = lineaId == null
                ? repository.findAll()
                : repository.findByLineaIdOrderByFechaHoraDesc(lineaId);

        return entities.stream()
                .map(this::toDto)
                .toList();
    }

    // Recuperamos un registro concreto para mostrar su detalle.
    public FlushingDTO obtener(Long id) {
        return toDto(buscar(id));
    }

    // Guardamos un registro nuevo y devolvemos su ID y datos persistidos.
    @Transactional
    public FlushingDTO crear(FlushingDTO dto) {
        FlushingEntity entity = new FlushingEntity();
        copiar(dto, entity);
        return toDto(repository.save(entity));
    }

    // Modificamos el registro existente conservando su identificador.
    @Transactional
    public FlushingDTO actualizar(Long id, FlushingDTO dto) {
        FlushingEntity entity = buscar(id);
        copiar(dto, entity);
        return toDto(repository.save(entity));
    }

    // Comprobamos que el registro exista antes de eliminarlo.
    @Transactional
    public void eliminar(Long id) {
        FlushingEntity entity = buscar(id);
        repository.delete(entity);
    }

    // Informamos cuando el ID solicitado no corresponde a un registro.
    private FlushingEntity buscar(Long id) {
        return repository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Flushing no encontrado: " + id));
    }

    // Pasamos los datos recibidos por la API a la entidad de PostgreSQL.
    private void copiar(FlushingDTO dto, FlushingEntity entity) {
        validarTemperatura(dto.temperaturaAntes(), "anterior");
        validarTemperatura(dto.temperaturaDespues(), "posterior");

        entity.setLineaId(dto.lineaId());
        entity.setFechaHora(dto.fechaHora());
        entity.setDuracionSegundos(dto.duracionSegundos());
        entity.setObservacion(dto.observacion());
        entity.setTemperaturaAntes(dto.temperaturaAntes());
        entity.setTemperaturaDespues(dto.temperaturaDespues());
    }

    // Permitimos datos antiguos sin temperatura, pero rechazamos NaN e infinito.
    private void validarTemperatura(Double temperatura, String momento) {
        if (temperatura != null && !Double.isFinite(temperatura)) {
            throw new IllegalArgumentException(
                    "La temperatura " + momento + " debe ser un número válido."
            );
        }
    }

    // Devolvemos también las temperaturas al consultar, crear o actualizar.
    private FlushingDTO toDto(FlushingEntity entity) {
        return new FlushingDTO(
                entity.getId(),
                entity.getLineaId(),
                entity.getFechaHora(),
                entity.getDuracionSegundos(),
                entity.getObservacion(),
                entity.getTemperaturaAntes(),
                entity.getTemperaturaDespues()
        );
    }
}