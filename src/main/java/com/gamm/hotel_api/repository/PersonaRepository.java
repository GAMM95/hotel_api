package com.gamm.hotel_api.repository;

import com.gamm.hotel_api.model.entity.Persona;
import com.gamm.hotel_api.model.enums.TipoDocumento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PersonaRepository extends JpaRepository<Persona, Integer> {
  boolean existsByTipoDocumentoAndNumeroDocumento(TipoDocumento tipoDocumento, String numeroDocumento);

  Optional<Persona> findByTipoDocumentoAndNumeroDocumento(TipoDocumento tipoDocumento, String numeroDocumento);

  Optional<Persona> findByEmail(String email);

  boolean existsByEmail(String email);
}
