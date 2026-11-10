
package com.example.aquacontrol_backend.granja.repository;

import com.example.aquacontrol_backend.granja.entity.GranjaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GranjaRepository
        extends JpaRepository<GranjaEntity, Long> {
}

