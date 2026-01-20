package com.gamm.hotel_api.service.impl;

import com.gamm.hotel_api.dto.HabitacionDTO;
import com.gamm.hotel_api.exceptions.NotFoundException;
import com.gamm.hotel_api.model.entity.Habitacion;
import com.gamm.hotel_api.model.entity.Hotel;
import com.gamm.hotel_api.model.entity.TipoHabitacion;
import com.gamm.hotel_api.model.enums.EstadoHabitacion;
import com.gamm.hotel_api.repository.HabitacionRepository;
import com.gamm.hotel_api.mapper.HabitacionMapper;
import com.gamm.hotel_api.repository.HotelRepository;
import com.gamm.hotel_api.service.HabitacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class HabitacionServiceImpl implements HabitacionService {

  @Autowired
  private HabitacionRepository habitacionRepository;

  @Autowired
  private HotelRepository hotelRepository;

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
    Habitacion habitacion = HabitacionMapper.toEntity(dto);

    // Asignar Hotel
    Hotel hotel = hotelRepository.findById(dto.getIdHotel())
        .orElseThrow(() -> new NotFoundException("Hotel no encontrado"));
    habitacion.setHotel(hotel);

    // Asignar TipoHabitacion mínimo
    TipoHabitacion tipo = new TipoHabitacion();
    tipo.setNombre(dto.getTipoHabitacion());
    habitacion.setTipoHabitacion(tipo);

    // Asignar Estado
    habitacion.setEstado(EstadoHabitacion.valueOf(dto.getEstado()));

    Habitacion guardada = habitacionRepository.save(habitacion);
    return HabitacionMapper.toDTO(guardada);
  }

  @Override
  @Transactional
  public HabitacionDTO actualizarHabitacion(Integer id, HabitacionDTO dto) {
    Habitacion habitacion = habitacionRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Habitación no encontrada"));

    HabitacionMapper.updateEntity(habitacion, dto);

    // Actualizar relaciones y estado
    if (dto.getIdHotel() != null) {
      Hotel hotel = hotelRepository.findById(dto.getIdHotel())
          .orElseThrow(() -> new NotFoundException("Hotel no encontrado"));
      habitacion.setHotel(hotel);
    }

    if (dto.getTipoHabitacion() != null) {
      TipoHabitacion tipo = new TipoHabitacion();
      tipo.setNombre(dto.getTipoHabitacion());
      habitacion.setTipoHabitacion(tipo);
    }

    if (dto.getEstado() != null) {
      habitacion.setEstado(EstadoHabitacion.valueOf(dto.getEstado()));
    }

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
    return habitacionRepository.findByHotel_Id(idHotel)
        .stream()
        .map(HabitacionMapper::toDTO)
        .toList();
  }

  @Override
  public List<HabitacionDTO> buscarPorEstado(String estado) {
    return habitacionRepository.findByEstado(estado)
        .stream()
        .map(HabitacionMapper::toDTO)
        .toList();
  }


  @Override
  public List<HabitacionDTO> buscarPorHotelYEstado(Integer idHotel, String estado) {
    return habitacionRepository.findByHotel_IdAndEstado(idHotel, estado)
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
}
