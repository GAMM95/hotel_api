package com.gamm.hotel_api.service.impl;

import com.gamm.hotel_api.dto.TipoHabitacionDTO;
import com.gamm.hotel_api.exceptions.BadRequestException;
import com.gamm.hotel_api.exceptions.NotFoundException;
import com.gamm.hotel_api.mapper.TipoHabitacionMapper;
import com.gamm.hotel_api.model.entity.TipoHabitacion;
import com.gamm.hotel_api.repository.TipoHabitacionRepository;
import com.gamm.hotel_api.service.TipoHabitacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class TipoHabitacionServiceImpl implements TipoHabitacionService {

  @Autowired
  private TipoHabitacionRepository tipoHabitacionRepository;

  @Override
  @Transactional
  public TipoHabitacionDTO registrar(TipoHabitacionDTO dto) {
    // 1. Validar nombre
    if (dto.getNombre() == null || dto.getNombre().isBlank()) {
      throw new BadRequestException("El nombre del tipo de habitación es obligatorio");
    }

    // 2. Validar capacidad
    if (dto.getCapacidad() == null || dto.getCapacidad() <= 0) {
      throw new BadRequestException("La capacidad debe ser mayor que 0");
    }

    // 3. Validar precio base
    if (dto.getPrecioBase() == null || dto.getPrecioBase().compareTo(BigDecimal.ZERO) < 0) {
      throw new BadRequestException("El precio base no puede ser negativo");
    }
    // 4. Validar unicidad por nombre
    tipoHabitacionRepository.findByNombreIgnoreCase(dto.getNombre())
        .ifPresent(t -> {
          throw new BadRequestException(
              "Ya existe un tipo de habitación con el nombre: " + dto.getNombre());
        });

    // 5. Guardar
    TipoHabitacion tipo = TipoHabitacionMapper.toEntity(dto);
    TipoHabitacion guardado = tipoHabitacionRepository.save(tipo);
    return TipoHabitacionMapper.toDTO(guardado);
  }

  @Override
  @Transactional
  public TipoHabitacionDTO actualizar(Integer id, TipoHabitacionDTO dto) {
    TipoHabitacion tipo = tipoHabitacionRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Tipo de habitación no encontrado"));

    // 1. Validar nombre si viene
    if (dto.getNombre() != null && dto.getNombre().isBlank()) {
      throw new BadRequestException("El nombre no puede estar vacío");
    }

    // 2. Validar capacidad si viene
    if (dto.getCapacidad() != null && dto.getCapacidad() <= 0) {
      throw new BadRequestException("La capacidad debe ser mayor que 0");
    }

    // 3. Validar precio si viene
    if (dto.getPrecioBase() != null && dto.getPrecioBase().compareTo(BigDecimal.ZERO) < 0) {
      throw new BadRequestException("El precio base no puede ser negativo");
    }

    // 4. Validar duplicado de nombre solo si cambia
    if (dto.getNombre() != null) {
      tipoHabitacionRepository.findByNombreIgnoreCase(dto.getNombre())
          .ifPresent(t -> {
            if (!t.getId().equals(tipo.getId())) {
              throw new BadRequestException(
                  "Ya existe otro tipo de habitación con el nombre: " + dto.getNombre());
            }
          });
    }

    // 5. Actualizar
    TipoHabitacionMapper.updateEntity(tipo, dto);
    TipoHabitacion actualizado = tipoHabitacionRepository.save(tipo);
    return TipoHabitacionMapper.toDTO(actualizado);
  }

  @Override
  public TipoHabitacionDTO obtenerPorId(Integer id) {
    TipoHabitacion tipo = tipoHabitacionRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Tipo de habitación no encontrado"));
    return TipoHabitacionMapper.toDTO(tipo);
  }

  @Override
  public List<TipoHabitacionDTO> listar() {
    return tipoHabitacionRepository.findAll()
        .stream()
        .map(TipoHabitacionMapper::toDTO)
        .toList();
  }

  @Override
  @Transactional
  public void eliminar(Integer id) {
    if (!tipoHabitacionRepository.existsById(id)) {
      throw new NotFoundException("Tipo de habitación no encontrado");
    }
    tipoHabitacionRepository.deleteById(id);
  }

  @Override
  public TipoHabitacionDTO buscarPorNombre(String nombre) {
    TipoHabitacion tipo = tipoHabitacionRepository.findByNombreIgnoreCase(nombre)
        .orElseThrow(() -> new NotFoundException("Tipo de habitación no encontrado con nombre: " + nombre));
    return TipoHabitacionMapper.toDTO(tipo);
  }

  @Override
  public List<TipoHabitacionDTO> buscarPorCapacidad(Integer capacidad) {
    return tipoHabitacionRepository.findByCapacidad(capacidad)
        .stream()
        .map(TipoHabitacionMapper::toDTO)
        .toList();
  }

  @Override
  public List<TipoHabitacionDTO> buscarPorRangoPrecio(Double min, Double max) {
    return tipoHabitacionRepository.findByPrecioBaseBetween(min, max)
        .stream()
        .map(TipoHabitacionMapper::toDTO)
        .toList();
  }
}
