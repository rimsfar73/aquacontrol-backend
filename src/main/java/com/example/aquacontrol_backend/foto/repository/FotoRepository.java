
package com.example.aquacontrol_backend.foto.repository;

import com.example.aquacontrol_backend.foto.entity.FotoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FotoRepository
        extends JpaRepository<FotoEntity, Long> {

    List<FotoEntity> findByFlushingIdOrderByIdDesc(Long flushingId);

    boolean existsByFlushingId(Long flushingId);
}
