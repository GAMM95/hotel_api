package com.gamm.hotel_api.mapper;

import com.gamm.hotel_api.dto.DetalleReservaDTO;
import com.gamm.hotel_api.model.entity.DetalleReserva;
import com.gamm.hotel_api.model.entity.Reserva;
import com.gamm.hotel_api.model.entity.Servicio;

import java.math.BigDecimal;

public class DetalleReservaMapper {

  public static DetalleReservaDTO toDTO(DetalleReserva detalle) {
    if (detalle == null) return null;

    BigDecimal subtotal = detalle.getServicio().getPrecio()
        .multiply(BigDecimal.valueOf(detalle.getCantidad()));

    return DetalleReservaDTO.builder()
        .id(detalle.getId())
        .idServicio(detalle.getServicio().getId())
        .nombreServicio(detalle.getServicio().getNombre())
        .cantidad(detalle.getCantidad())
        .precio(detalle.getServicio().getPrecio())
        .subtotal(subtotal)
        .build();
  }

  public static DetalleReserva toEntity(DetalleReservaDTO dto, Reserva reserva, Servicio servicio) {
    if (dto == null) return null;

    DetalleReserva detalle = new DetalleReserva();
    detalle.setReserva(reserva);
    detalle.setServicio(servicio);
    detalle.setCantidad(dto.getCantidad() != null ? dto.getCantidad() : 1);
    return detalle;
  }
}
