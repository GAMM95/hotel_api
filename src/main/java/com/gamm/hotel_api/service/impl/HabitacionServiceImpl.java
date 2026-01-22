package com.gamm.hotel_api.service.impl;

import com.gamm.hotel_api.dto.HabitacionDTO;
import com.gamm.hotel_api.exceptions.BadRequestException;
import com.gamm.hotel_api.exceptions.NotFoundException;
import com.gamm.hotel_api.model.entity.Habitacion;
import com.gamm.hotel_api.model.entity.Hotel;
import com.gamm.hotel_api.model.entity.TipoHabitacion;
import com.gamm.hotel_api.model.enums.EstadoHabitacion;
import com.gamm.hotel_api.repository.HabitacionRepository;
import com.gamm.hotel_api.mapper.HabitacionMapper;
import com.gamm.hotel_api.repository.HotelRepository;
import com.gamm.hotel_api.repository.TipoHabitacionRepository;
import com.gamm.hotel_api.service.HabitacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class HabitacionServiceImpl implements HabitacionService {

  @Autowired
  private HabitacionRepository habitacionRepository;

  @Autowired
  private HotelRepository hotelRepository;
  @Autowired
  private TipoHabitacionRepository tipoHabitacionRepository;

  @Override
  public List<HabitacionDTO> listar() {
    return habitacionRepository.findAll()
        .stream()
        .map(HabitacionMapper::toDTO)
        .toList();
  }

  @Override
  public HabitacionDTO obtenerPorId(Integer id) {
    Habitacion habitacion = habitacionRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Habitación no encontrada"));
    return HabitacionMapper.toDTO(habitacion);
  }

  @Override
  @Transactional
  public HabitacionDTO registrarHabitacion(HabitacionDTO dto) {

    // 1. Validaciones mínimas
    if (dto.getNumero() == null || dto.getNumero().isBlank()) {
      throw new BadRequestException("El número de habitación es obligatorio");
    }

    if (dto.getIdHotel() == null) {
      throw new BadRequestException("El hotel es obligatorio");
    }

    if (dto.getIdTipoHabitacion() == null &&
        (dto.getTipoHabitacion() == null || dto.getTipoHabitacion().isBlank())) {
      throw new BadRequestException("Debe indicar el tipo de habitación");
    }

    // 2. Validar existencia de hotel
    Hotel hotel = hotelRepository.findById(dto.getIdHotel())
        .orElseThrow(() -> new NotFoundException("Hotel no encontrado"));

    // 3. Validar duplicado de la habitación
    habitacionRepository
        .findByNumeroAndHotel_Id(dto.getNumero(), dto.getIdHotel())
        .ifPresent(h -> {
          throw new BadRequestException(
              "Ya existe la habitación número " + dto.getNumero() + " en este hotel");
        });

    // 4. Construir entidad
    Habitacion habitacion = HabitacionMapper.toEntity(dto);
    habitacion.setHotel(hotel);

    // 5. Asignar TipoHabitacion
    TipoHabitacion tipo;
    if (dto.getIdTipoHabitacion() != null) {
      tipo = tipoHabitacionRepository.findById(dto.getIdTipoHabitacion())
          .orElseThrow(() -> new NotFoundException("Tipo Habitación no encontrado"));
    } else {
      tipo = tipoHabitacionRepository
          .findByNombreIgnoreCase(dto.getTipoHabitacion())
          .orElseGet(() -> {
            TipoHabitacion nuevo = new TipoHabitacion();
            nuevo.setNombre(dto.getTipoHabitacion());
            return tipoHabitacionRepository.save(nuevo);
          });
    }
    habitacion.setTipoHabitacion(tipo);

    // 6. FORZAR estado DISPONIBLE al registrar
    habitacion.setEstado(EstadoHabitacion.DISPONIBLE);

    // 7. Persistir
    Habitacion guardada = habitacionRepository.save(habitacion);
    return HabitacionMapper.toDTO(guardada);
  }

  @Override
  @Transactional
  public HabitacionDTO actualizarHabitacion(Integer id, HabitacionDTO dto) {
    Habitacion habitacion = habitacionRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Habitación no encontrada"));

    // 1. Validaciones básicas
    if (dto.getNumero() != null && dto.getNumero().isBlank()) {
      throw new BadRequestException("El número de habitación no puede estar vacío");
    }

    if (dto.getEstado() != null) {
      try {
        EstadoHabitacion.valueOf(dto.getEstado());
      } catch (IllegalArgumentException e) {
        throw new BadRequestException(
            "Estado inválido. Valores permitidos: LIBRE, OCUPADA, MANTENIMIENTO");
      }
    }

    // 2. Actualizar hotel si cambia
    if (dto.getIdHotel() != null) {
      Hotel hotel = hotelRepository.findById(dto.getIdHotel())
          .orElseThrow(() -> new NotFoundException("Hotel no encontrado"));
      habitacion.setHotel(hotel);
    }

    // 3. Actualizar tipo de habitación
    if (dto.getIdTipoHabitacion() != null) {
      TipoHabitacion tipo = tipoHabitacionRepository.findById(dto.getIdTipoHabitacion())
          .orElseThrow(() -> new NotFoundException("Tipo de habitación no encontrado"));
      habitacion.setTipoHabitacion(tipo);

    } else if (dto.getTipoHabitacion() != null) {
      TipoHabitacion tipo = tipoHabitacionRepository
          .findByNombreIgnoreCase(dto.getTipoHabitacion())
          .orElseGet(() -> {
            TipoHabitacion nuevo = new TipoHabitacion();
            nuevo.setNombre(dto.getTipoHabitacion());
            return tipoHabitacionRepository.save(nuevo);
          });
      habitacion.setTipoHabitacion(tipo);
    }

    // 4. Validar duplicados de número de habitación por hotel
    if (dto.getNumero() != null || dto.getIdHotel() != null) {
      String numero = dto.getNumero() != null ? dto.getNumero() : habitacion.getNumero();
      Integer hotelId = dto.getIdHotel() != null ? dto.getIdHotel() : habitacion.getHotel().getId();

      habitacionRepository
          .findByNumeroAndHotel_Id(numero, hotelId)
          .ifPresent(h -> {
            if (!h.getId().equals(habitacion.getId())) {
              throw new BadRequestException(
                  "Ya existe otra habitación con número " + numero + " en este hotel");
            }
          });
    }

    // 5. Actualizar estado
    if (dto.getEstado() != null) {
      habitacion.setEstado(EstadoHabitacion.valueOf(dto.getEstado()));
    }

    // 6. Actualizar campos simples con mapper
    HabitacionMapper.updateEntity(habitacion, dto);

    // 7. Persistir cambios
    Habitacion actualizada = habitacionRepository.save(habitacion);
    return HabitacionMapper.toDTO(actualizada);
  }

  @Override
  public void eliminar(Integer id) {
    if (!habitacionRepository.existsById(id)) {
      throw new NotFoundException("Habitación no encontrada");
    }
    habitacionRepository.deleteById(id);
  }

  // Busqudas personalizadas
  @Override
  public List<HabitacionDTO> buscarPorHotel(Integer idHotel) {
    // Validar que exista el hotel
    Hotel hotel = hotelRepository.findById(idHotel)
        .orElseThrow(() -> new NotFoundException("Hotel no encontrado"));

    // Retornar resultados
    return habitacionRepository.findByHotel_Id(idHotel)
        .stream()
        .map(HabitacionMapper::toDTO)
        .toList();
  }

  @Override
  public List<HabitacionDTO> buscarPorEstado(String estado) {
    EstadoHabitacion estadoEnum;
    try {
      estadoEnum = EstadoHabitacion.valueOf(estado.toUpperCase());
    } catch (IllegalArgumentException e) {
      throw new BadRequestException(
          "Estado inválido. Valores permitidos: DISPONIBLE, OCUPADA, MANTENIMIENTO");
    }

    return habitacionRepository.findByEstado(estadoEnum)
        .stream()
        .map(HabitacionMapper::toDTO)
        .toList();
  }


  @Override
  public List<HabitacionDTO> buscarPorHotelYEstado(Integer idHotel, String estado) {
    EstadoHabitacion estadoEnum;
    try {
      estadoEnum = EstadoHabitacion.valueOf(estado.toUpperCase());
    } catch (IllegalArgumentException e) {
      throw new BadRequestException(
          "Estado inválido. Valores permitidos: DISPONIBLE, OCUPADA, MANTENIMIENTO");
    }
    return habitacionRepository.findByHotel_IdAndEstado(idHotel, estadoEnum)
        .stream()
        .map(HabitacionMapper::toDTO)
        .toList();
  }

  @Override
  public List<HabitacionDTO> buscarPorTipo(String tipo) {
    return habitacionRepository.findByTipoHabitacion_Nombre(tipo)
        .stream()
        .map(HabitacionMapper::toDTO)
        .toList();
  }

  @Override
  public List<HabitacionDTO> buscarPorHotelYTipo(Integer idHotel, String tipo) {
    return habitacionRepository.findByHotel_IdAndTipoHabitacion_Nombre(idHotel, tipo)
        .stream()
        .map(HabitacionMapper::toDTO)
        .toList();
  }

  @Override
  public List<HabitacionDTO> buscarPorRangoPrecio(BigDecimal min, BigDecimal max) {
    if (min == null || max == null || min.compareTo(max) > 0) {
      throw new BadRequestException("Rango de precio inválido");
    }

    return habitacionRepository.findByPrecioBetween(min, max)
        .stream()
        .map(HabitacionMapper::toDTO)
        .toList();
  }

  @Override
  public List<HabitacionDTO> buscarPorHotelYRangoPrecio(Integer hotelId, BigDecimal min, BigDecimal max) {
    if (min == null || max == null || min.compareTo(max) > 0) {
      throw new BadRequestException("Rango de precio inválido");
    }

    // Validar hotel
    hotelRepository.findById(hotelId)
        .orElseThrow(() -> new NotFoundException("Hotel no encontrado"));

    return habitacionRepository.findByHotel_IdAndPrecioBetween(hotelId, min, max)
        .stream()
        .map(HabitacionMapper::toDTO)
        .toList();
  }

}
