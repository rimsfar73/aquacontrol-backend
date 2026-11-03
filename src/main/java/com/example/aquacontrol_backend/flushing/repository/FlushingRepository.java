package com.example.aquacontrol_backend.flushing.repository;

import com.example.aquacontrol_backend.flushing.entity.FlushingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FlushingRepository extends JpaRepository<FlushingEntity, Long> {
    List<FlushingEntity> findByLineaIdOrderByFechaHoraDesc(Long lineaId);
}
