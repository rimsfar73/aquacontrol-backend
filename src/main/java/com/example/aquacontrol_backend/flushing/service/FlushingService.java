
package com.example.aquacontrol_backend.flushing.service;

import com.example.aquacontrol_backend.common.exception.ResourceNotFoundException;
import com.example.aquacontrol_backend.flushing.dto.FlushingDTO;
import com.example.aquacontrol_backend.flushing.entity.FlushingEntity;
import com.example.aquacontrol_backend.flushing.repository.FlushingRepository;
import com.example.aquacontrol_backend.linea.repository.LineaRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class FlushingService {

    private final FlushingRepository repository;
    private final LineaRepository lineaRepository;

    public FlushingService(
            FlushingRepository repository,
            LineaRepository lineaRepository
    ) {
        this.repository = repository;
        this.lineaRepository = lineaRepository;
    }

    public List<FlushingDTO> listar(Long lineaId) {
        List<FlushingEntity> entities = lineaId == null
                ? repository.findAll()
                : repository.findByLineaIdOrderByFechaHoraDesc(lineaId);

        return entities.stream()
                .map(this::toDto)
                .toList();
    }

    public FlushingDTO obtener(Long id) {
        return toDto(buscar(id));
    }

    @Transactional
    public FlushingDTO crear(FlushingDTO dto) {
        FlushingEntity entity = new FlushingEntity();
        copiar(dto, entity);
        return toDto(repository.save(entity));
    }

    @Transactional
    public FlushingDTO actualizar(Long id, FlushingDTO dto) {
        FlushingEntity entity = buscar(id);
        copiar(dto, entity);
        return toDto(repository.save(entity));
    }

    @Transactional
    public void eliminar(Long id) {
        FlushingEntity entity = buscar(id);
        repository.delete(entity);
    }

    private FlushingEntity buscar(Long id) {
        return repository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException(
                        "Flushing no encontrado: " + id
                )
        );
    }

    private void validarLinea(Long lineaId) {
        if (lineaId == null || !lineaRepository.existsById(lineaId)) {
            throw new ResourceNotFoundException(
                    "Línea no encontrada: " + lineaId
            );
        }
    }

    private void copiar(FlushingDTO dto, FlushingEntity entity) {
        validarLinea(dto.lineaId());

        validarTemperatura(dto.temperaturaAntes(), "anterior");
        validarTemperatura(dto.temperaturaDespues(), "posterior");

        entity.setLineaId(dto.lineaId());
        entity.setFechaHora(dto.fechaHora());
        entity.setDuracionSegundos(dto.duracionSegundos());
        entity.setObservacion(dto.observacion());
        entity.setTemperaturaAntes(dto.temperaturaAntes());
        entity.setTemperaturaDespues(dto.temperaturaDespues());
    }

    private void validarTemperatura(Double temperatura, String momento) {
        if (temperatura != null && !Double.isFinite(temperatura)) {
            throw new IllegalArgumentException(
                    "La temperatura " + momento
                            + " debe ser un número válido."
            );
        }
    }

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
