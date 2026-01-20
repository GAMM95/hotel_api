package com.gamm.hotel_api.service;

import com.gamm.hotel_api.dto.ReservaDTO;

import java.time.LocalDate;
import java.util.List;

public interface ReservaService {

  // CRUD básico
  ReservaDTO crearReserva(ReservaDTO dto);
  ReservaDTO obtenerPorId(Integer id);
  List<ReservaDTO> listar();
  void eliminar(Integer id);

  // Cambios de estado controlados por el sistema
  ReservaDTO confirmarReserva(Integer id);
  ReservaDTO cancelarReserva(Integer id);
  ReservaDTO finalizarReserva(Integer id);

  // Búsquedas
  List<ReservaDTO> buscarPorEstado(String estado);
  List<ReservaDTO> buscarPorHuesped(Integer idHuesped);
  List<ReservaDTO> buscarPorHabitacion(Integer idHabitacion);
  List<ReservaDTO> buscarPorRangoFechas(LocalDate inicio, LocalDate fin);
}
