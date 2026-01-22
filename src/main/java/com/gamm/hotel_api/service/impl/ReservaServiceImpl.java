package com.gamm.hotel_api.service.impl;

import com.gamm.hotel_api.dto.DetalleReservaDTO;
import com.gamm.hotel_api.dto.ReservaDTO;
import com.gamm.hotel_api.exceptions.BadRequestException;
import com.gamm.hotel_api.exceptions.NotFoundException;
import com.gamm.hotel_api.mapper.ReservaMapper;
import com.gamm.hotel_api.model.entity.*;
import com.gamm.hotel_api.model.enums.EstadoHabitacion;
import com.gamm.hotel_api.model.enums.EstadoPago;
import com.gamm.hotel_api.model.enums.EstadoReserva;
import com.gamm.hotel_api.repository.*;
import com.gamm.hotel_api.service.ReservaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class ReservaServiceImpl implements ReservaService {

  @Autowired
  private ReservaRepository reservaRepository;
  @Autowired
  private HuespedRepository huespedRepository;
  @Autowired
  private HabitacionRepository habitacionRepository;
  @Autowired
  private ServicioRepository servicioRepository;
  @Autowired
  private PagoRepository pagoRepository;
  @Autowired
  private DetalleReservaRepository detalleReservaRepository;

  @Override
  public ReservaDTO registrarReserva(ReservaDTO dto) {

    /** Validaciones */
    if (dto.getIdHuesped() == null)
      throw new BadRequestException("El huésped es obligatorio");
    if (dto.getIdHabitacion() == null)
      throw new BadRequestException("La habitación es obligatoria");
    if (dto.getFechaInicio() == null || dto.getFechaFin() == null)
      throw new BadRequestException("Las fechas son obligatorias");
    if (dto.getFechaInicio().isAfter(dto.getFechaFin()))
      throw new BadRequestException("La fecha inicio no puede ser mayor a la fecha fin");
    if (dto.getFechaInicio().isBefore(LocalDate.now()))
      throw new BadRequestException("No se permiten reservas en fechas pasadas");

    // Buscar huésped por su id
    Huesped huesped = huespedRepository.findById(dto.getIdHuesped())
        .orElseThrow(() -> new NotFoundException("Huésped no encontrado"));

    // Buscar habitación por su id
    Habitacion habitacion = habitacionRepository.findById(dto.getIdHabitacion())
        .orElseThrow(() -> new NotFoundException("Habitación no encontrada"));

    // Validar que la habitación no esté en mantenimiento
    if (habitacion.getEstado() == EstadoHabitacion.MANTENIMIENTO)
      throw new BadRequestException("La habitación está en mantenimiento");

    // Verificar si existe alguna reserva que se cruce en el rango de fechas
    boolean existeCruce = reservaRepository
        .existsByHabitacion_IdAndEstadoInAndFechaInicioLessThanEqualAndFechaFinGreaterThanEqual(
            dto.getIdHabitacion(),
            List.of(EstadoReserva.PENDIENTE, EstadoReserva.CONFIRMADA),
            dto.getFechaFin(),
            dto.getFechaInicio()
        );
    if (existeCruce)
      throw new BadRequestException("La habitación ya está reservada en ese rango");

    /** Creacion de la reserva */
    // Asigacion de data a la reserva
    Reserva reserva = new Reserva();
    reserva.setHuesped(huesped);
    reserva.setHabitacion(habitacion);
    reserva.setFechaInicio(dto.getFechaInicio());
    reserva.setFechaFin(dto.getFechaFin());
    reserva.setEstado(EstadoReserva.PENDIENTE);

    /** Procesamiento de servicios */
    // Lista vacía para almacenar los detalles de la reserva
    List<DetalleReserva> detalles = new ArrayList<>();
    // Validar que el DTO tenga servicios
    if (dto.getServicios() != null && !dto.getServicios().isEmpty()) {
      // Recorrer todos los servicios enviados en el DTO
      for (DetalleReservaDTO d : dto.getServicios()) {
        // Validar que la cantidad no sea nula y sea mayor a 0
        if (d.getCantidad() == null || d.getCantidad() <= 0)
          throw new BadRequestException("La cantidad debe ser mayor a 0");

        // Buscar el servicio por su id
        Servicio servicio = servicioRepository.findById(d.getIdServicio())
            .orElseThrow(() -> new NotFoundException("Servicio no encontrado"));
        // Validar que el servicio pertenezca al mismo hotel de la habitación
        if (!servicio.getHotel().getId().equals(habitacion.getHotel().getId()))
          throw new BadRequestException(
              "El servicio " + servicio.getNombre() + " no pertenece al hotel de la habitación");

        // Crear detalle de reserva
        DetalleReserva dr = new DetalleReserva();
        dr.setReserva(reserva);
        dr.setServicio(servicio);
        dr.setCantidad(d.getCantidad());
        detalles.add(dr);
      }
    }
    // Asignar la lista de servicios a la reserva
    reserva.setServicios(detalles);

    /** Calculo de reserva  (Habitacion + servicios ) */
    recalcularTotal(reserva);

    // Guardar la reserva en la base de datos
    Reserva guardada = reservaRepository.save(reserva);
    // Si existen servicios, guardar los detalles
    if (!detalles.isEmpty())
      detalleReservaRepository.saveAll(detalles);

    // Retornar la reserva guardada convertida a DTO
    return ReservaMapper.toDTO(guardada);
  }

  @Override
  public ReservaDTO actualizarReserva(Integer id, ReservaDTO dto) {

    // Obtener la reserva
    Reserva reserva = getReserva(id);

    // Validar solo se puedan modificar reservas en estado PENDIENTE
    if (reserva.getEstado() != EstadoReserva.PENDIENTE)
      throw new BadRequestException("Solo se pueden modificar reservas en estado PENDIENTE");

    // ACTUALIZACIÓN DE FECHAS
    if (dto.getFechaInicio() != null && dto.getFechaFin() != null) {

      if (dto.getFechaInicio().isAfter(dto.getFechaFin()))
        throw new BadRequestException("La fecha inicio no puede ser mayor a la fecha fin");

      // Se valida que no exista cruce con otras reservas,
      boolean existeCruce = reservaRepository
          .existsByHabitacion_IdAndIdNotAndEstadoInAndFechaInicioLessThanEqualAndFechaFinGreaterThanEqual(
              reserva.getHabitacion().getId(),
              reserva.getId(),
              List.of(EstadoReserva.PENDIENTE, EstadoReserva.CONFIRMADA),
              dto.getFechaFin(),
              dto.getFechaInicio()
          );

      // Si existe cruce con otra reserva, se lanza excepción
      if (existeCruce)
        throw new BadRequestException("La habitación ya está reservada en ese rango");
      reserva.setFechaInicio(dto.getFechaInicio());
      reserva.setFechaFin(dto.getFechaFin());
    }

    // ACTUALIZACIÓN DE SERVICIOS
    // Eliminar todos los servicios actuales de la reserva
    reserva.getServicios().clear();

    // Validar que el DTO tenga servicios
    if (dto.getServicios() != null) {

      // Recorrer los servicios enviados en el DTO
      for (DetalleReservaDTO d : dto.getServicios()) {

        // Buscar servicio
        Servicio servicio = servicioRepository.findById(d.getIdServicio())
            .orElseThrow(() -> new NotFoundException("Servicio no encontrado"));

        // Crear nuevo detalle de reserva
        DetalleReserva dr = new DetalleReserva();
        dr.setReserva(reserva);
        dr.setServicio(servicio);
        dr.setCantidad(d.getCantidad());
        // Agregar detalle a la lista
        reserva.getServicios().add(dr);
      }
    }
    // Recalcular el total
    recalcularTotal(reserva);
    // Guardar cambios
    Reserva guardada = reservaRepository.save(reserva);
    // Retornar mapper
    return ReservaMapper.toDTO(guardada);
  }

  @Override
  public ReservaDTO confirmarReserva(Integer id) {
    return cambiarEstado(id, EstadoReserva.CONFIRMADA);
  }

  @Override
  @Transactional
  public ReservaDTO checkIn(Integer id) {

    // Obtener la reserva por su id
    Reserva reserva = getReserva(id);

    // Validar que la reserva esté en estado CONFIRMADA
    if (reserva.getEstado() != EstadoReserva.CONFIRMADA) {
      throw new BadRequestException("Solo se puede hacer check-in a reservas CONFIRMADAS");
    }

    // Obtener la habitación asociada a la reserva
    Habitacion habitacion = reserva.getHabitacion();

    // Validar que la habitación esté marcada como RESERVADA
    if (habitacion.getEstado() != EstadoHabitacion.RESERVADA) {
      throw new BadRequestException("La habitación no está lista para ocupar");
    }

    // Cambiar estado de habitación a OCUPADA
    habitacion.setEstado(EstadoHabitacion.OCUPADA);
    habitacionRepository.save(habitacion);

    // Retornar la reserva convertida a DTO
    return ReservaMapper.toDTO(reserva);
  }

  // Método que cancela una reserva
  @Override
  public ReservaDTO cancelarReserva(Integer id) {
    return cambiarEstado(id, EstadoReserva.CANCELADA);
  }

  @Override
  public ReservaDTO checkOut(Integer id) {
    return cambiarEstado(id, EstadoReserva.FINALIZADA);
  }

  /**
   * Método centralizado que maneja toda la lógica de transición de estados.
   * Aquí se controla:
   * - Validación de transiciones
   * - Pago previo a la confirmación
   * - Estado físico de la habitación
   */
  private ReservaDTO cambiarEstado(Integer id, EstadoReserva nuevoEstado) {

    Reserva reserva = getReserva(id);
    Habitacion habitacion = reserva.getHabitacion();

    // Regla de negocio: no se puede cancelar después del check-in
    if (nuevoEstado == EstadoReserva.CANCELADA &&
        habitacion.getEstado() == EstadoHabitacion.OCUPADA) {
      throw new BadRequestException(
          "No se puede cancelar una reserva después del check-in. Debe realizarse check-out.");
    }

    // NUEVA LÓGICA: Anular pagos PAGADOS si se cancela antes del check-in
    if (nuevoEstado == EstadoReserva.CANCELADA) {
      if (reserva.getPagos() != null) {
        for (Pago p : reserva.getPagos()) {
          if (p.getEstado() == EstadoPago.PAGADO) {
            p.setEstado(EstadoPago.ANULADO);
            pagoRepository.save(p);
          }
        }
      }
    }

    // Regla adicional opcional: solo se puede hacer check-out si está OCUPADA
    if (nuevoEstado == EstadoReserva.FINALIZADA &&
        habitacion.getEstado() != EstadoHabitacion.OCUPADA) {
      throw new BadRequestException(
          "Solo se puede hacer check-out cuando la habitación está OCUPADA.");
    }

    validarCambioEstado(reserva.getEstado(), nuevoEstado);

    if (nuevoEstado == EstadoReserva.CONFIRMADA) {
      boolean pagoValido = pagoRepository
          .existsByReserva_IdAndEstado(id, EstadoPago.PAGADO);
      if (!pagoValido)
        throw new BadRequestException("No se puede confirmar la reserva sin un pago PAGADO");
    }

    reserva.setEstado(nuevoEstado);

    switch (nuevoEstado) {
      case CONFIRMADA -> habitacion.setEstado(EstadoHabitacion.RESERVADA);
      case FINALIZADA, CANCELADA -> habitacion.setEstado(EstadoHabitacion.DISPONIBLE);
    }

    habitacionRepository.save(habitacion);
    return ReservaMapper.toDTO(reservaRepository.save(reserva));
  }

  /**
   * Método que valida si un cambio de estado es permitido.
   * Controla todas las transiciones válidas de la reserva.
   */
  private void validarCambioEstado(EstadoReserva actual, EstadoReserva nuevo) {

    // Validar que una reserva FINALIZADA no pueda cambiar de estado
    if (actual == EstadoReserva.FINALIZADA)
      throw new BadRequestException("Una reserva FINALIZADA no puede cambiar de estado");

    // Validar que una reserva CANCELADA no pueda cambiar de estado
    if (actual == EstadoReserva.CANCELADA)
      throw new BadRequestException("Una reserva CANCELADA no puede cambiar de estado");

    switch (actual) {
      // Caso cuando la reserva está en estado PENDIENTE
      case PENDIENTE -> {
        // Desde PENDIENTE solo se permite pasar a CONFIRMADA o CANCELADA
        if (nuevo != EstadoReserva.CONFIRMADA && nuevo != EstadoReserva.CANCELADA)
          throw new BadRequestException(
              "Desde PENDIENTE solo se puede pasar a CONFIRMADA o CANCELADA");
      }
      // Caso cuando la reserva está en estado CONFIRMADA
      case CONFIRMADA -> {
        // Desde CONFIRMADA solo se permite pasar a FINALIZADA o CANCELADA
        if (nuevo != EstadoReserva.FINALIZADA && nuevo != EstadoReserva.CANCELADA)
          throw new BadRequestException(
              "Desde CONFIRMADA solo se puede pasar a FINALIZADA o CANCELADA");
      }
    }
  }
  // CONSULTAS
  @Override
  public ReservaDTO obtenerPorId(Integer id) {
    // Buscar reserva por ID usando el método utilitario getReserva
    Reserva reserva = getReserva(id);
    return ReservaMapper.toDTO(reserva);
  }

  // Lista todas las reservas existentes
  @Override
  public List<ReservaDTO> listar() {
    return reservaRepository.findAll()
        .stream()
        .map(ReservaMapper::toDTO)
        .toList();
  }

  // Busca reservas por estado (PENDIENTE, CONFIRMADA, CANCELADA, FINALIZADA)
  @Override
  public List<ReservaDTO> buscarPorEstado(String estado) {

    // Variable donde se almacenará el enum convertido
    EstadoReserva estadoEnum;

    try {
      // Se convierte el String recibido a enum, en mayúsculas
      estadoEnum = EstadoReserva.valueOf(estado.toUpperCase());
    } catch (Exception e) {
      // Si el String no coincide con ningún enum, se lanza excepción de negocio
      throw new BadRequestException(
          "Estado inválido. Valores permitidos: PENDIENTE, CONFIRMADA, CANCELADA, FINALIZADA");
    }

    // Se buscan las reservas por el estado convertido
    return reservaRepository.findByEstado(estadoEnum)

        // Se pasa a stream
        .stream()

        // Se transforma cada entidad en DTO
        .map(ReservaMapper::toDTO)

        // Se retorna como lista
        .toList();
  }

  // Busca todas las reservas de un huésped específico
  @Override
  public List<ReservaDTO> buscarPorHuesped(Integer idHuesped) {
    return reservaRepository.findByHuesped_Id(idHuesped)
        .stream()
        .map(ReservaMapper::toDTO)
        .toList();
  }

  // Busca todas las reservas asociadas a una habitación
  @Override
  public List<ReservaDTO> buscarPorHabitacion(Integer idHabitacion) {
    return reservaRepository.findByHabitacion_Id(idHabitacion)
        .stream()
        .map(ReservaMapper::toDTO)
        .toList();
  }

  // Busca reservas que se crucen con un rango de fechas
  @Override
  public List<ReservaDTO> buscarPorRangoFechas(LocalDate inicio, LocalDate fin) {
    return reservaRepository
        .findByFechaInicioLessThanEqualAndFechaFinGreaterThanEqual(inicio, fin)
        .stream()
        .map(ReservaMapper::toDTO)
        .toList();
  }

  // Elimina una reserva siempre que cumpla las reglas de negocio
  @Override
  public void eliminar(Integer id) {

    // Obtener la reserva por su ID
    Reserva reserva = getReserva(id);

    // Se valida que una reserva CONFIRMADA no pueda ser eliminada
    if (reserva.getEstado() == EstadoReserva.CONFIRMADA)
      throw new BadRequestException("No se puede eliminar una reserva CONFIRMADA");
    // Se valida que una reserva FINALIZADA no pueda ser eliminada
    if (reserva.getEstado() == EstadoReserva.FINALIZADA)
      throw new BadRequestException("No se puede eliminar una reserva FINALIZADA");

    // Si la reserva está PENDIENTE o CANCELADA, sí se permite eliminar
    // Obtener la habitación asociada a la reserva
    Habitacion habitacion = reserva.getHabitacion();

    // Liberar la habitación cambiando su estado a DISPONIBLE
    habitacion.setEstado(EstadoHabitacion.DISPONIBLE);
    habitacionRepository.save(habitacion);

    // Validar si la reserva tiene servicios asociados
    if (reserva.getServicios() != null && !reserva.getServicios().isEmpty()) {
      // Eliminar los detalles de reserva para mantener integridad referencial
      detalleReservaRepository.deleteAll(reserva.getServicios());
    }
    reservaRepository.delete(reserva);
  }


  // MÉTODOS UTILITARIOS

  /**
   * Obtiene una reserva por su ID.
   * Este método centraliza la búsqueda y el manejo de la excepción
   * cuando una reserva no existe.
   */
  private Reserva getReserva(Integer id) {
    // Buscar reserva
    return reservaRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Reserva no encontrada"));
  }

  /**
   * Recalcula el total de una reserva.
   * total = (precioHabitacion * númeroDeNoches) + suma(precioServicio * cantidad)
   */
  private void recalcularTotal(Reserva reserva) {

    // Se calcula la cantidad de días entre la fecha de inicio y la fecha de fin
    long noches = reserva.getFechaInicio()

        // Se calcula la diferencia de fechas
        .until(reserva.getFechaFin())

        // Se obtiene la cantidad de días
        .getDays();

    // Si el cálculo da 0 noches (misma fecha inicio y fin), se cobra como mínimo 1 noche
    if (noches == 0)
      noches = 1;

    // Se calcula el total correspondiente únicamente a la habitación
    BigDecimal totalHabitacion =
        reserva.getHabitacion()                 // Se obtiene la habitación asociada
            .getPrecio()                     // Se obtiene el precio por noche
            .multiply(BigDecimal.valueOf(noches)); // Se multiplica por la cantidad de noches

    // Se calcula el total de todos los servicios asociados a la reserva
    BigDecimal totalServicios = reserva.getServicios() // Se obtiene la lista de servicios
        .stream()                                      // Se convierte en stream
        .map(d ->                                    // Por cada detalle de reserva:
            d.getServicio()                           // Se obtiene el servicio
                .getPrecio()                             // Se obtiene su precio unitario
                .multiply(BigDecimal.valueOf(d.getCantidad())) // Se multiplica por la cantidad solicitada
        )
        .reduce(BigDecimal.ZERO, BigDecimal::add);    // Se suman todos los subtotales

    // Se suma el total de la habitación y el total de los servicios
    BigDecimal totalFinal = totalHabitacion.add(totalServicios);

    // Se asigna el total final calculado a la reserva
    reserva.setTotal(totalFinal);
  }
}
