package com.example.aquacontrol_backend.temperatura.repository;

import com.example.aquacontrol_backend.temperatura.entity.TemperaturaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TemperaturaRepository extends JpaRepository<TemperaturaEntity, Long> {
    List<TemperaturaEntity> findByLineaIdOrderByFechaHoraDesc(Long lineaId);
}
