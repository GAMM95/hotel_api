package com.gamm.hotel_api.service.impl;

import com.gamm.hotel_api.dto.PagoDTO;
import com.gamm.hotel_api.exceptions.BadRequestException;
import com.gamm.hotel_api.exceptions.NotFoundException;
import com.gamm.hotel_api.mapper.PagoMapper;
import com.gamm.hotel_api.model.entity.Pago;
import com.gamm.hotel_api.model.entity.Reserva;
import com.gamm.hotel_api.model.enums.EstadoHabitacion;
import com.gamm.hotel_api.model.enums.EstadoPago;
import com.gamm.hotel_api.model.enums.EstadoReserva;
import com.gamm.hotel_api.model.enums.MetodoPago;
import com.gamm.hotel_api.repository.HabitacionRepository;
import com.gamm.hotel_api.repository.PagoRepository;
import com.gamm.hotel_api.repository.ReservaRepository;
import com.gamm.hotel_api.service.PagoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class PagoServiceImpl implements PagoService {

  @Autowired
  private PagoRepository pagoRepository;

  @Autowired
  private ReservaRepository reservaRepository;

  @Autowired
  private HabitacionRepository habitacionRepository;

  @Override
  public PagoDTO registrarPago(PagoDTO dto) {

    // -------- Validaciones básicas --------
    if (dto.getIdReserva() == null)
      throw new BadRequestException("La reserva es obligatoria");

    if (dto.getMonto() == null || dto.getMonto().compareTo(BigDecimal.ZERO) <= 0)
      throw new BadRequestException("El monto debe ser mayor a 0");

    if (dto.getMetodo() == null)
      throw new BadRequestException("El método de pago es obligatorio");

    MetodoPago metodo;
    try {
      metodo = MetodoPago.valueOf(dto.getMetodo());
    } catch (Exception e) {
      throw new BadRequestException("Método de pago inválido");
    }

    // Buscar reserva
    Reserva reserva = reservaRepository.findById(dto.getIdReserva())
        .orElseThrow(() -> new NotFoundException("Reserva no encontrada"));

    // No se puede pagar una reserva cancelada o finalizada
    if (reserva.getEstado() == EstadoReserva.CANCELADA ||
        reserva.getEstado() == EstadoReserva.FINALIZADA) {
      throw new BadRequestException("No se puede pagar una reserva cancelada o finalizada");
    }

    // Crear pago
    Pago pago = new Pago();
    pago.setReserva(reserva);
    pago.setMonto(dto.getMonto());
    pago.setMetodo(metodo);
    pago.setEstado(EstadoPago.PAGADO);
    pago.setFechaPago(LocalDateTime.now());

    pagoRepository.save(pago);

    // Recalcular total pagado
    BigDecimal totalPagado = reserva.getPagos().stream()
        .filter(p -> p.getEstado() == EstadoPago.PAGADO)
        .map(Pago::getMonto)
        .reduce(BigDecimal.ZERO, BigDecimal::add);

    // Actualizar estado de la reserva
    if (totalPagado.compareTo(reserva.getTotal()) >= 0) {
      // Si ya se cubrió el total → reserva confirmada
      reserva.setEstado(EstadoReserva.CONFIRMADA);
      reservaRepository.save(reserva);

      // La habitación pasa a RESERVADA (no OCUPADA todavía)
      reserva.getHabitacion().setEstado(EstadoHabitacion.RESERVADA);
      habitacionRepository.save(reserva.getHabitacion());
    } else {
      // Si aún no se cubre el total, sigue pendiente
      reserva.setEstado(EstadoReserva.PENDIENTE);
      reservaRepository.save(reserva);
    }

    return PagoMapper.toDTO(pago);
  }

  @Override
  public PagoDTO actualizarPago(Integer id, PagoDTO dto) {

    Pago pago = pagoRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Pago no encontrado"));

    // No se puede modificar un pago que ya fue PAGADO o ANULADO
    if (pago.getEstado() == EstadoPago.PAGADO || pago.getEstado() == EstadoPago.ANULADO) {
      throw new BadRequestException("No se puede modificar un pago PAGADO o ANULADO");
    }

    // -------- Cambio de estado controlado --------
    if (dto.getEstado() != null) {
      EstadoPago nuevoEstado;
      try {
        nuevoEstado = EstadoPago.valueOf(dto.getEstado());
      } catch (Exception e) {
        throw new BadRequestException("Estado de pago inválido");
      }

      // Transiciones permitidas:
      // PENDIENTE -> PAGADO
      // PENDIENTE -> FALLIDO
      if (pago.getEstado() == EstadoPago.PENDIENTE &&
          (nuevoEstado == EstadoPago.PAGADO || nuevoEstado == EstadoPago.FALLIDO)) {
        pago.setEstado(nuevoEstado);
      } else {
        throw new BadRequestException("Transición de estado de pago no permitida");
      }
    }

    // Solo permitir cambios de monto y método si sigue PENDIENTE
    if (pago.getEstado() == EstadoPago.PENDIENTE) {
      if (dto.getMonto() != null) {
        if (dto.getMonto().compareTo(BigDecimal.ZERO) <= 0)
          throw new BadRequestException("El monto debe ser mayor a 0");
        pago.setMonto(dto.getMonto());
      }

      if (dto.getMetodo() != null) {
        try {
          pago.setMetodo(MetodoPago.valueOf(dto.getMetodo()));
        } catch (Exception e) {
          throw new BadRequestException("Método de pago inválido");
        }
      }
    }

    Pago actualizado = pagoRepository.save(pago);

    // Si pasa a PAGADO, validar impacto en la reserva
    if (actualizado.getEstado() == EstadoPago.PAGADO) {
      actualizarEstadoReservaPorPagos(actualizado.getReserva());
    }

    return PagoMapper.toDTO(actualizado);
  }

  @Override
  public PagoDTO anularPago(Integer idPago) {

    Pago pago = pagoRepository.findById(idPago)
        .orElseThrow(() -> new NotFoundException("Pago no encontrado"));

    if (pago.getEstado() == EstadoPago.ANULADO)
      throw new BadRequestException("El pago ya está anulado");

    Reserva reserva = pago.getReserva();

    // NUEVA VALIDACIÓN: no se puede anular pagos si la habitación ya está ocupada
    if (reserva.getHabitacion().getEstado() == EstadoHabitacion.OCUPADA) {
      throw new BadRequestException(
          "No se puede anular pagos después del check-in");
    }

    // Si el pago estaba PAGADO, puede afectar el estado de la reserva
    if (pago.getEstado() == EstadoPago.PAGADO) {

      pago.setEstado(EstadoPago.ANULADO);
      pagoRepository.save(pago);

      // Recalcular el total pagado real
      BigDecimal totalPagado = reserva.getPagos().stream()
          .filter(p -> p.getEstado() == EstadoPago.PAGADO)
          .map(Pago::getMonto)
          .reduce(BigDecimal.ZERO, BigDecimal::add);

      // Si ya no se cubre el total → la reserva vuelve a PENDIENTE
      if (totalPagado.compareTo(reserva.getTotal()) < 0) {
        reserva.setEstado(EstadoReserva.PENDIENTE);
        reservaRepository.save(reserva);

        // La habitación vuelve a DISPONIBLE solo si no está ocupada
        if (reserva.getHabitacion().getEstado() != EstadoHabitacion.OCUPADA) {
          reserva.getHabitacion().setEstado(EstadoHabitacion.DISPONIBLE);
          habitacionRepository.save(reserva.getHabitacion());
        }
      }

    } else {
      // Si estaba PENDIENTE o FALLIDO, simplemente se anula
      pago.setEstado(EstadoPago.ANULADO);
      pagoRepository.save(pago);
    }

    return PagoMapper.toDTO(pago);
  }


  // CONSULTAS
  @Override
  public PagoDTO obtenerPorId(Integer id) {
    Pago pago = pagoRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Pago no encontrado"));
    return PagoMapper.toDTO(pago);
  }

  @Override
  public List<PagoDTO> listar() {
    return pagoRepository.findAll()
        .stream()
        .map(PagoMapper::toDTO)
        .toList();
  }

  @Override
  public List<PagoDTO> buscarPorReserva(Integer idReserva) {
    return pagoRepository.findByReserva_Id(idReserva)
        .stream()
        .map(PagoMapper::toDTO)
        .toList();
  }

  @Override
  public List<PagoDTO> buscarPorEstado(String estado) {
    EstadoPago estadoEnum;
    try {
      estadoEnum = EstadoPago.valueOf(estado.toUpperCase());
    } catch (Exception e) {
      throw new BadRequestException("Estado inválido: PAGADO, PENDIENTE, FALLIDO, ANULADO");
    }

    return pagoRepository.findByEstado(estadoEnum)
        .stream()
        .map(PagoMapper::toDTO)
        .toList();
  }

  /**
   * Revisa todos los pagos PAGADOS de la reserva y decide:
   * - Si cubren el total → CONFIRMADA + habitación RESERVADA
   * - Si no → PENDIENTE + habitación DISPONIBLE
   */
  private void actualizarEstadoReservaPorPagos(Reserva reserva) {

    BigDecimal totalPagado = reserva.getPagos().stream()
        .filter(p -> p.getEstado() == EstadoPago.PAGADO)
        .map(Pago::getMonto)
        .reduce(BigDecimal.ZERO, BigDecimal::add);

    if (totalPagado.compareTo(reserva.getTotal()) >= 0) {
      reserva.setEstado(EstadoReserva.CONFIRMADA);
      reservaRepository.save(reserva);

      reserva.getHabitacion().setEstado(EstadoHabitacion.RESERVADA);
      habitacionRepository.save(reserva.getHabitacion());
    } else {
      reserva.setEstado(EstadoReserva.PENDIENTE);
      reservaRepository.save(reserva);

      reserva.getHabitacion().setEstado(EstadoHabitacion.DISPONIBLE);
      habitacionRepository.save(reserva.getHabitacion());
    }
  }
}
