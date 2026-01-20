package com.gamm.hotel_api.repository;

import com.gamm.hotel_api.model.entity.DetalleReserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DetalleReservaRepository extends JpaRepository<DetalleReserva, Integer> {

  // Traer todos los servicios de una reserva
  List<DetalleReserva> findByReserva_Id(Integer reservaId);

  // Eliminar todos los servicios de una reserva (útil al actualizar)
  void deleteByReserva_Id(Integer reservaId);
}
