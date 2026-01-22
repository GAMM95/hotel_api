package com.gamm.hotel_api.service;

import com.gamm.hotel_api.dto.ReservaDTO;

import java.time.LocalDate;
import java.util.List;

public interface ReservaService {

  // CRUD básico
  ReservaDTO registrarReserva(ReservaDTO dto);

  ReservaDTO actualizarReserva(Integer id, ReservaDTO dto);

  ReservaDTO obtenerPorId(Integer id);

  List<ReservaDTO> listar();

  void eliminar(Integer id);

  // ================= Flujo de estados =================
  // PENDIENTE → CONFIRMADA (cuando el pago cubre el total)
  ReservaDTO confirmarReserva(Integer id);

  // CONFIRMADA → OCUPADA (cuando el huésped hace check-in)
  ReservaDTO checkIn(Integer id);

  // CONFIRMADA → CANCELADA
  ReservaDTO cancelarReserva(Integer id);

  // OCUPADA → FINALIZADA (cuando el huésped hace check-out)
  ReservaDTO checkOut(Integer id);

  // Búsquedas
  List<ReservaDTO> buscarPorEstado(String estado);

  List<ReservaDTO> buscarPorHuesped(Integer idHuesped);

  List<ReservaDTO> buscarPorHabitacion(Integer idHabitacion);

  List<ReservaDTO> buscarPorRangoFechas(LocalDate inicio, LocalDate fin);
}
