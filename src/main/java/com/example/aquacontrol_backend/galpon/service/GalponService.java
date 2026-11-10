
package com.example.aquacontrol_backend.galpon.service;

import com.example.aquacontrol_backend.common.exception.ResourceNotFoundException;
import com.example.aquacontrol_backend.galpon.dto.GalponDTO;
import com.example.aquacontrol_backend.galpon.entity.GalponEntity;
import com.example.aquacontrol_backend.galpon.repository.GalponRepository;
import com.example.aquacontrol_backend.granja.entity.GranjaEntity;
import com.example.aquacontrol_backend.granja.repository.GranjaRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class GalponService {

    private final GalponRepository galponRepository;
    private final GranjaRepository granjaRepository;

    public GalponService(
            GalponRepository galponRepository,
            GranjaRepository granjaRepository
    ) {
        this.galponRepository = galponRepository;
        this.granjaRepository = granjaRepository;
    }

    public List<GalponDTO> listar() {
        return galponRepository.findAll()
                .stream()
                .map(this::toDto)
                .toList();
    }

    public List<GalponDTO> listarPorGranja(Long granjaId) {
        buscarGranja(granjaId);

        return galponRepository.findByGranja_Id(granjaId)
                .stream()
                .map(this::toDto)
                .toList();
    }

    public GalponDTO obtener(Long id) {
        return toDto(buscarGalpon(id));
    }

    @Transactional
    public GalponDTO crear(GalponDTO dto) {

        GranjaEntity granja = buscarGranja(dto.granjaId());

        GalponEntity galpon = new GalponEntity();
        galpon.setNombre(dto.nombre().trim());
        galpon.setGranja(granja);

        return toDto(galponRepository.save(galpon));
    }

    @Transactional
    public GalponDTO actualizar(Long id, GalponDTO dto) {

        GalponEntity galpon = buscarGalpon(id);
        GranjaEntity granja = buscarGranja(dto.granjaId());

        galpon.setNombre(dto.nombre().trim());
        galpon.setGranja(granja);

        return toDto(galponRepository.save(galpon));
    }

    @Transactional
    public void eliminar(Long id) {
        GalponEntity galpon = buscarGalpon(id);
        galponRepository.delete(galpon);
    }

    private GalponEntity buscarGalpon(Long id) {
        return galponRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Galpón no encontrado: " + id
                        )
                );
    }

    private GranjaEntity buscarGranja(Long id) {
        return granjaRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Granja no encontrada: " + id
                        )
                );
    }

    private GalponDTO toDto(GalponEntity galpon) {
        return new GalponDTO(
                galpon.getId(),
                galpon.getNombre(),
                galpon.getGranja().getId()
        );
    }
}
