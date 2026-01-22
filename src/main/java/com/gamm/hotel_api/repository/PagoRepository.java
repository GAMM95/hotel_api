package com.gamm.hotel_api.repository;

import com.gamm.hotel_api.model.entity.Pago;
import com.gamm.hotel_api.model.enums.EstadoPago;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PagoRepository extends JpaRepository<Pago, Integer> {

  // Saber si una reserva ya tiene un pago válido (para confirmar reserva)
  boolean existsByReserva_IdAndEstado(Integer idReserva, EstadoPago estado);

  // Obtener todos los pagos de una reserva
  List<Pago> findByReserva_Id(Integer idReserva);

  // Obtener pagos por estado (PAGADO, PENDIENTE, FALLIDO)
  List<Pago> findByEstado(EstadoPago estado);
}
