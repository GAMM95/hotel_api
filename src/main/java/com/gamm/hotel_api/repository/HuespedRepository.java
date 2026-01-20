package com.gamm.hotel_api.repository;

import com.gamm.hotel_api.model.entity.Huesped;
import com.gamm.hotel_api.model.enums.TipoDocumento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HuespedRepository extends JpaRepository<Huesped, Integer> {

  // Por nombre
  List<Huesped> findByPersona_NombreContainingIgnoreCase(String nombre);

  // Por apellidos
  List<Huesped> findByPersona_ApellidosContainingIgnoreCase(String apellidos);

  // Por número de documento
  Optional<Huesped> findByPersona_NumeroDocumento(String numeroDocumento);

  // Por tipo y número de documento
  Optional<Huesped> findByPersona_TipoDocumentoAndPersona_NumeroDocumento(
      TipoDocumento tipoDocumento,
      String numeroDocumento
  );

  // Por email
  Optional<Huesped> findByPersona_Email(String email);

  // Por teléfono
  List<Huesped> findByPersona_TelefonoContaining(String telefono);

  // Por nombre completo
  List<Huesped> findByPersona_NombreContainingIgnoreCaseAndPersona_ApellidosContainingIgnoreCase(
      String nombre,
      String apellidos
  );

  // Por nacionalidad
  List<Huesped> findByNacionalidadIgnoreCase(String nacionalidad);
}
