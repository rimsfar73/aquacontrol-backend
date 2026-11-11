package com.example.aquacontrol_backend.flushing.service;

import com.example.aquacontrol_backend.common.exception.ResourceNotFoundException;
import com.example.aquacontrol_backend.flushing.dto.FlushingDTO;
import com.example.aquacontrol_backend.flushing.entity.FlushingEntity;
import com.example.aquacontrol_backend.flushing.repository.FlushingRepository;
import com.example.aquacontrol_backend.foto.repository.FotoRepository;
import com.example.aquacontrol_backend.linea.repository.LineaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

// Coordinamos los registros de flushing y su evidencia fotográfica.
@Service
@Transactional(readOnly = true)
public class FlushingService {

    private final FlushingRepository repository;
    private final LineaRepository lineaRepository;
    private final FotoRepository fotoRepository;

    // Spring nos entrega los repositorios necesarios.
    public FlushingService(
            FlushingRepository repository,
            LineaRepository lineaRepository,
            FotoRepository fotoRepository
    ) {
        this.repository = repository;
        this.lineaRepository = lineaRepository;
        this.fotoRepository = fotoRepository;
    }

    // Consultamos todos los registros o filtramos por una línea.
    public List<FlushingDTO> listar(Long lineaId) {
        List<FlushingEntity> entities = lineaId == null
                ? repository.findAll()
                : repository.findByLineaIdOrderByFechaHoraDesc(lineaId);

        return entities.stream()
                .map(this::toDto)
                .toList();
    }

    // Recuperamos un flushing concreto para mostrar su detalle.
    public FlushingDTO obtener(Long id) {
        return toDto(buscar(id));
    }

    // Validamos y guardamos un nuevo registro.
    @Transactional
    public FlushingDTO crear(FlushingDTO dto) {
        FlushingEntity entity = new FlushingEntity();
        copiar(dto, entity);
        return toDto(repository.save(entity));
    }

    // Actualizamos los datos conservando el identificador y la foto asociada.
    @Transactional
    public FlushingDTO actualizar(Long id, FlushingDTO dto) {
        FlushingEntity entity = buscar(id);
        copiar(dto, entity);
        return toDto(repository.save(entity));
    }

    // Eliminamos primero la evidencia y después el flushing.
    // La transacción revierte ambos cambios si alguna operación falla.
    @Transactional
    public void eliminar(Long id) {
        FlushingEntity entity = buscar(id);
        fotoRepository.eliminarPorFlushingId(id);
        repository.delete(entity);
    }

    // Informamos cuando el registro solicitado no existe.
    private FlushingEntity buscar(Long id) {
        return repository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException(
                        "Flushing no encontrado: " + id
                )
        );
    }

    // Conservamos la validación de Rimsky: la línea debe existir.
    private void validarLinea(Long lineaId) {
        if (lineaId == null || !lineaRepository.existsById(lineaId)) {
            throw new ResourceNotFoundException(
                    "Línea no encontrada: " + lineaId
            );
        }
    }

    // Pasamos los datos recibidos por la API a la entidad de PostgreSQL.
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

    // Admitimos registros antiguos sin temperatura y rechazamos NaN e infinito.
    private void validarTemperatura(Double temperatura, String momento) {
        if (temperatura != null && !Double.isFinite(temperatura)) {
            throw new IllegalArgumentException(
                    "La temperatura " + momento
                            + " debe ser un número válido."
            );
        }
    }

    // Devolvemos los datos del registro; la foto se consulta por separado.
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