package com.gamm.hotel_api.service.impl;

import com.gamm.hotel_api.dto.ReservaDTO;
import com.gamm.hotel_api.exceptions.NotFoundException;
import com.gamm.hotel_api.mapper.ReservaMapper;
import com.gamm.hotel_api.model.entity.*;
import com.gamm.hotel_api.model.enums.EstadoReserva;
import com.gamm.hotel_api.repository.*;
import com.gamm.hotel_api.service.ReservaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
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
  private DetalleReservaRepository detalleReservaRepository;

  @Override
  @Transactional
  public ReservaDTO crearReserva(ReservaDTO dto) {

    Huesped huesped = huespedRepository.findById(dto.getIdHuesped())
        .orElseThrow(() -> new NotFoundException("Huésped no encontrado"));

    Habitacion habitacion = habitacionRepository.findById(dto.getIdHabitacion())
        .orElseThrow(() -> new NotFoundException("Habitación no encontrada"));

    Reserva reserva = new Reserva();
    reserva.setHuesped(huesped);
    reserva.setHabitacion(habitacion);
    reserva.setFechaInicio(dto.getFechaInicio());
    reserva.setFechaFin(dto.getFechaFin());
    reserva.setEstado(EstadoReserva.PENDIENTE); // siempre inicia así

    Reserva guardada = reservaRepository.save(reserva);

    // Detalles (servicios)
    if (dto.getServicios() != null && !dto.getServicios().isEmpty()) {
      List<DetalleReserva> detalles = dto.getServicios().stream().map(d -> {
        Servicio servicio = servicioRepository.findById(d.getIdServicio())
            .orElseThrow(() -> new NotFoundException("Servicio no encontrado"));

        DetalleReserva dr = new DetalleReserva();
        dr.setReserva(guardada);
        dr.setServicio(servicio);
        dr.setCantidad(d.getCantidad());
        return dr;
      }).collect(Collectors.toList());

      detalleReservaRepository.saveAll(detalles);
      guardada.setServicios(detalles);
    }

    return ReservaMapper.toDTO(guardada);
  }

  @Override
  public ReservaDTO obtenerPorId(Integer id) {
    Reserva reserva = reservaRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Reserva no encontrada"));
    return ReservaMapper.toDTO(reserva);
  }

  @Override
  public List<ReservaDTO> listar() {
    return reservaRepository.findAll()
        .stream()
        .map(ReservaMapper::toDTO)
        .toList();
  }

  @Override
  public void eliminar(Integer id) {
    if (!reservaRepository.existsById(id)) {
      throw new NotFoundException("Reserva no encontrada");
    }
    reservaRepository.deleteById(id);
  }

  // ------------------ Estados ------------------

  @Override
  public ReservaDTO confirmarReserva(Integer id) {
    Reserva reserva = getReserva(id);

    if (reserva.getEstado() != EstadoReserva.PENDIENTE) {
      throw new IllegalStateException("Solo se pueden confirmar reservas pendientes");
    }

    reserva.setEstado(EstadoReserva.CONFIRMADA);
    return ReservaMapper.toDTO(reservaRepository.save(reserva));
  }

  @Override
  public ReservaDTO cancelarReserva(Integer id) {
    Reserva reserva = getReserva(id);

    if (reserva.getEstado() == EstadoReserva.FINALIZADA) {
      throw new IllegalStateException("No se puede cancelar una reserva finalizada");
    }

    reserva.setEstado(EstadoReserva.CANCELADA);
    return ReservaMapper.toDTO(reservaRepository.save(reserva));
  }

  @Override
  public ReservaDTO finalizarReserva(Integer id) {
    Reserva reserva = getReserva(id);

    if (reserva.getEstado() != EstadoReserva.CONFIRMADA) {
      throw new IllegalStateException("Solo se pueden finalizar reservas confirmadas");
    }

    reserva.setEstado(EstadoReserva.FINALIZADA);
    return ReservaMapper.toDTO(reservaRepository.save(reserva));
  }

  // ------------------ Búsquedas ------------------

  @Override
  public List<ReservaDTO> buscarPorEstado(String estado) {
    EstadoReserva estadoEnum = EstadoReserva.valueOf(estado.toUpperCase());
    return reservaRepository.findByEstado(estadoEnum)
        .stream()
        .map(ReservaMapper::toDTO)
        .toList();
  }

  @Override
  public List<ReservaDTO> buscarPorHuesped(Integer idHuesped) {
    return reservaRepository.findByHuesped_Id(idHuesped)
        .stream()
        .map(ReservaMapper::toDTO)
        .toList();
  }

  @Override
  public List<ReservaDTO> buscarPorHabitacion(Integer idHabitacion) {
    return reservaRepository.findByHabitacion_Id(idHabitacion)
        .stream()
        .map(ReservaMapper::toDTO)
        .toList();
  }

  @Override
  public List<ReservaDTO> buscarPorRangoFechas(LocalDate inicio, LocalDate fin) {
    return reservaRepository
        .findByFechaInicioLessThanEqualAndFechaFinGreaterThanEqual(inicio, fin)
        .stream()
        .map(ReservaMapper::toDTO)
        .toList();
  }

  // ------------------ Util ------------------

  private Reserva getReserva(Integer id) {
    return reservaRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Reserva no encontrada"));
  }
}
