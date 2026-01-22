package com.gamm.hotel_api.mapper;

import com.gamm.hotel_api.dto.PagoDTO;
import com.gamm.hotel_api.model.entity.Pago;
import com.gamm.hotel_api.model.entity.Reserva;

import java.math.BigDecimal;
public class PagoMapper {

  public static PagoDTO toDTO(
      Pago pago,
      BigDecimal totalPagado,
      BigDecimal totalReserva) {

    if (pago == null) return null;

    BigDecimal restante = totalReserva.subtract(totalPagado);

    return PagoDTO.builder()
        .id(pago.getId())
        .idReserva(pago.getReserva().getId())
        .monto(pago.getMonto())
        .metodo(pago.getMetodo().name())
        .estado(pago.getEstado().name())
        .fechaPago(pago.getFechaPago())
        .totalPagado(totalPagado)
        .restante(restante.max(BigDecimal.ZERO))
        .build();
  }

  public static PagoDTO toDTO(Pago pago) {
    if (pago == null) return null;

    Reserva reserva = pago.getReserva();
    BigDecimal totalReserva = reserva.getTotal();

    BigDecimal totalPagado = reserva.getPagos().stream()
        .filter(p -> p.getEstado() == com.gamm.hotel_api.model.enums.EstadoPago.PAGADO)
        .map(Pago::getMonto)
        .reduce(BigDecimal.ZERO, BigDecimal::add);

    return toDTO(pago, totalPagado, totalReserva);
  }

  public static Pago toEntity(PagoDTO dto, Reserva reserva) {
    if (dto == null) return null;

    Pago pago = new Pago();
    pago.setReserva(reserva);
    pago.setMonto(dto.getMonto());

    if (dto.getMetodo() != null) {
      pago.setMetodo(
          com.gamm.hotel_api.model.enums.MetodoPago.valueOf(dto.getMetodo())
      );
    }

    if (dto.getEstado() != null) {
      pago.setEstado(
          com.gamm.hotel_api.model.enums.EstadoPago.valueOf(dto.getEstado())
      );
    }

    return pago;
  }

  public static void updateEntity(Pago pago, PagoDTO dto) {
    if (pago == null || dto == null) return;

    if (dto.getMonto() != null) {
      pago.setMonto(dto.getMonto());
    }

    if (dto.getMetodo() != null) {
      pago.setMetodo(
          com.gamm.hotel_api.model.enums.MetodoPago.valueOf(dto.getMetodo())
      );
    }

    if (dto.getEstado() != null) {
      pago.setEstado(
          com.gamm.hotel_api.model.enums.EstadoPago.valueOf(dto.getEstado())
      );
    }

    if (dto.getFechaPago() != null) {
      pago.setFechaPago(dto.getFechaPago());
    }
  }
}

