package com.gamm.hotel_api.mapper;

import com.gamm.hotel_api.dto.DetalleReservaDTO;
import com.gamm.hotel_api.dto.ReservaDTO;
import com.gamm.hotel_api.model.entity.Reserva;
import com.gamm.hotel_api.model.entity.Huesped;
import com.gamm.hotel_api.model.entity.Habitacion;
import com.gamm.hotel_api.model.entity.DetalleReserva;

import java.util.List;
import java.util.stream.Collectors;

public class ReservaMapper {

  public static ReservaDTO toDTO(Reserva reserva) {
    if (reserva == null) return null;

    List<DetalleReservaDTO> detalles = reserva.getServicios().stream()
        .map(DetalleReservaMapper::toDTO)
        .collect(Collectors.toList());

    double total = detalles.stream()
        .mapToDouble(DetalleReservaDTO::getSubtotal)
        .sum();

    return ReservaDTO.builder()
        .id(reserva.getId())
        .idHuesped(reserva.getHuesped().getId())
        .nombreHuesped(reserva.getHuesped().getPersona().getNombre() + " " +
            reserva.getHuesped().getPersona().getApellidos())
        .idHabitacion(reserva.getHabitacion().getId())
        .numeroHabitacion(reserva.getHabitacion().getNumero())
        .estado(reserva.getEstado().name())
        .fechaInicio(reserva.getFechaInicio())
        .fechaFin(reserva.getFechaFin())
        .servicios(detalles)
        .total(total)
        .build();
  }

  public static Reserva toEntity(ReservaDTO dto, Huesped huesped, Habitacion habitacion, List<DetalleReserva> detalles) {
    if (dto == null) return null;

    Reserva reserva = new Reserva();
    reserva.setHuesped(huesped);
    reserva.setHabitacion(habitacion);
    reserva.setEstado(dto.getEstado() != null ? com.gamm.hotel_api.model.enums.EstadoReserva.valueOf(dto.getEstado()) : null);
    reserva.setFechaInicio(dto.getFechaInicio());
    reserva.setFechaFin(dto.getFechaFin());
    reserva.setServicios(detalles);
    return reserva;
  }
}
