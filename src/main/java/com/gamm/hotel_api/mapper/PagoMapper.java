package com.gamm.hotel_api.mapper;

import com.gamm.hotel_api.dto.PagoDTO;
import com.gamm.hotel_api.model.entity.Pago;
import com.gamm.hotel_api.model.entity.Reserva;

public class PagoMapper {

  public static PagoDTO toDTO(Pago pago) {
    if (pago == null) return null;

    return PagoDTO.builder()
        .id(pago.getId())
        .idReserva(pago.getReserva().getId())
        .monto(pago.getMonto())
        .metodo(pago.getMetodo().name())
        .estado(pago.getEstado().name())
        .fechaPago(pago.getFechaPago())
        .build();
  }

  public static Pago toEntity(PagoDTO dto, Reserva reserva) {
    if (dto == null) return null;

    Pago pago = new Pago();
    pago.setReserva(reserva);
    pago.setMonto(dto.getMonto());
    pago.setMetodo(dto.getMetodo() != null ? com.gamm.hotel_api.model.enums.MetodoPago.valueOf(dto.getMetodo()) : null);
    pago.setEstado(dto.getEstado() != null ? com.gamm.hotel_api.model.enums.EstadoPago.valueOf(dto.getEstado()) : null);
    return pago;
  }
}
