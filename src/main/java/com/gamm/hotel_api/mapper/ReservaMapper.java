package com.gamm.hotel_api.mapper;

import com.gamm.hotel_api.dto.DetalleReservaDTO;
import com.gamm.hotel_api.dto.ReservaDTO;
import com.gamm.hotel_api.model.entity.Reserva;
import com.gamm.hotel_api.model.entity.Huesped;
import com.gamm.hotel_api.model.entity.Habitacion;
import com.gamm.hotel_api.model.entity.DetalleReserva;
import com.gamm.hotel_api.model.enums.EstadoReserva;

import java.util.List;
import java.util.stream.Collectors;

public class ReservaMapper {

  public static ReservaDTO toDTO(Reserva reserva) {
    if (reserva == null) return null;

    List<DetalleReservaDTO> detalles = reserva.getServicios() != null
        ? reserva.getServicios().stream()
        .map(DetalleReservaMapper::toDTO)
        .collect(Collectors.toList())
        : List.of();

    return ReservaDTO.builder()
        .id(reserva.getId())
        .idHuesped(reserva.getHuesped() != null ? reserva.getHuesped().getId() : null)
        .nombreHuesped(
            reserva.getHuesped() != null && reserva.getHuesped().getPersona() != null
                ? reserva.getHuesped().getPersona().getNombre() + " " +
                reserva.getHuesped().getPersona().getApellidos()
                : null
        )
        .idHabitacion(reserva.getHabitacion() != null ? reserva.getHabitacion().getId() : null)
        .numeroHabitacion(reserva.getHabitacion() != null ? reserva.getHabitacion().getNumero() : null)
        .estado(reserva.getEstado() != null ? reserva.getEstado().name() : null)
        .fechaInicio(reserva.getFechaInicio())
        .fechaFin(reserva.getFechaFin())
        .servicios(detalles)
        .total(reserva.getTotal())
        .build();
  }

  public static Reserva toEntity(
      ReservaDTO dto,
      Huesped huesped,
      Habitacion habitacion,
      List<DetalleReserva> detalles) {

    if (dto == null) return null;

    Reserva reserva = new Reserva();
    reserva.setHuesped(huesped);
    reserva.setHabitacion(habitacion);
    reserva.setEstado(EstadoReserva.PENDIENTE);
    reserva.setFechaInicio(dto.getFechaInicio());
    reserva.setFechaFin(dto.getFechaFin());

    if (detalles != null) {
      detalles.forEach(d -> d.setReserva(reserva));
      reserva.setServicios(detalles);
    }

    return reserva;
  }

  public static void updateEntity(
      Reserva reserva,
      ReservaDTO dto,
      Huesped huesped,
      Habitacion habitacion,
      List<DetalleReserva> detalles) {

    if (reserva == null || dto == null) return;
    if (huesped != null) {
      reserva.setHuesped(huesped);
    }
    if (habitacion != null) {
      reserva.setHabitacion(habitacion);
    }

    if (dto.getEstado() != null) {
      reserva.setEstado(EstadoReserva.valueOf(dto.getEstado()));
    }
    if (dto.getFechaInicio() != null) {
      reserva.setFechaInicio(dto.getFechaInicio());
    }
    if (dto.getFechaFin() != null) {
      reserva.setFechaFin(dto.getFechaFin());
    }
    if (detalles != null) {
      detalles.forEach(d -> d.setReserva(reserva));
      reserva.setServicios(detalles);
    }
  }
}
