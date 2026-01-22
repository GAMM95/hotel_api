package com.gamm.hotel_api.service.impl;

import com.gamm.hotel_api.dto.ServicioDTO;
import com.gamm.hotel_api.exceptions.BadRequestException;
import com.gamm.hotel_api.exceptions.NotFoundException;
import com.gamm.hotel_api.mapper.ServicioMapper;
import com.gamm.hotel_api.model.entity.Hotel;
import com.gamm.hotel_api.model.entity.Servicio;
import com.gamm.hotel_api.repository.HotelRepository;
import com.gamm.hotel_api.repository.ServicioRepository;
import com.gamm.hotel_api.service.ServicioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ServicioServiceImpl implements ServicioService {

  @Autowired
  private ServicioRepository servicioRepository;

  @Autowired
  private HotelRepository hotelRepository;

  // ---------- CRUD ----------
  @Override
  @Transactional
  public ServicioDTO registrarServicio(ServicioDTO dto) {
    // 1. Validar campos obligatorios
    if (dto.getNombre() == null || dto.getNombre().isBlank()) {
      throw new BadRequestException("El nombre del servicio es obligatorio");
    }

    if (dto.getPrecio() == null || dto.getPrecio().compareTo(BigDecimal.ZERO) < 0) {
      throw new BadRequestException("El precio del servicio debe ser mayor o igual a 0");
    }

    if (dto.getIdHotel() == null) {
      throw new BadRequestException("El hotel es obligatorio para registrar un servicio");
    }

    // 2. Verificar que el hotel exista
    Hotel hotel = hotelRepository.findById(dto.getIdHotel())
        .orElseThrow(() -> new NotFoundException("Hotel no encontrado"));

    // 3. Validar unicidad logica del nombre + hotel
    servicioRepository.findByNombreIgnoreCaseAndHotelId(dto.getNombre(), hotel.getId())
        .ifPresent(servicio -> {
          throw new BadRequestException(
              "Ya existe un servicio llamado '" + dto.getNombre() +
                  "' para este hotel"
          );
        });
    // 4. Convertir de DTO a entidad
    Servicio servicio = ServicioMapper.toEntity(dto, hotel);
    // 5. Persistencia en la base de datos
    Servicio guardado = servicioRepository.save(servicio);
    // 6. Retornar DTO
    return ServicioMapper.toDTO(guardado);
  }

  @Override
  public ServicioDTO obtenerPorId(Integer id) {
    Servicio servicio = servicioRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Servicio no encontrado"));
    return ServicioMapper.toDTO(servicio);
  }

  @Override
  public List<ServicioDTO> listar() {
    return servicioRepository.findAll()
        .stream()
        .map(ServicioMapper::toDTO)
        .toList();
  }

  @Override
  @Transactional
  public ServicioDTO actualizar(Integer id, ServicioDTO dto) {
    // 1. Verificar que el servicio exista
    Servicio servicio = servicioRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Servicio no encontrado"));

    // 2. Validar precio si viene en el DTO
    if (dto.getPrecio() != null && dto.getPrecio().compareTo(BigDecimal.ZERO) < 0) {
      throw new BadRequestException("El precio del servicio no puede ser negativo");
    }

    // 3. Determinar el hotel correcto
    Hotel hotelActual = servicio.getHotel();
    Hotel hotelFinal = hotelActual;

    if (dto.getIdHotel() != null && !dto.getIdHotel().equals(hotelActual.getId())) {
      hotelFinal = hotelRepository.findById(dto.getIdHotel())
          .orElseThrow(() -> new NotFoundException("Hotel no encontrado"));
    }

    // 4. Validar unicidad lógica: nombre + hotel
    servicioRepository.findByNombreIgnoreCaseAndHotelId(dto.getNombre(), hotelFinal.getId())
        .ifPresent(servicioEncontrado -> {
          if (!servicioEncontrado.getId().equals(servicio.getId())) {
            throw new BadRequestException(
                "Ya existe otro servicio llamado '" + dto.getNombre() +
                    "' para este hotel"
            );
          }
        });

    // 5. Actualizar la entidad usando el mapper
    ServicioMapper.updateEntity(servicio, dto, hotelFinal);

    // 6. Persistir cambios
    Servicio actualizado = servicioRepository.save(servicio);

    // 7. Retornar DTO actualizado
    return ServicioMapper.toDTO(actualizado);
  }

  @Override
  public void eliminar(Integer id) {
    if (!servicioRepository.existsById(id)) {
      throw new NotFoundException("Servicio no encontrado");
    }
    servicioRepository.deleteById(id);
  }

  // ---------- BÚSQUEDAS ----------
  @Override
  public ServicioDTO buscarPorNombre(String nombre) {
    Servicio servicio = servicioRepository.findByNombreContainingIgnoreCase(nombre)
        .orElseThrow(() -> new NotFoundException("Servicio no encontrado con nombre: " + nombre));
    return ServicioMapper.toDTO(servicio);
  }

  @Override
  public List<ServicioDTO> buscarPorPrecioMenorIgual(BigDecimal precio) {
    return servicioRepository.findByPrecioLessThanEqual(precio)
        .stream()
        .map(ServicioMapper::toDTO)
        .toList();
  }

  @Override
  public List<ServicioDTO> buscarPorPrecioMayorIgual(BigDecimal precio) {
    return servicioRepository.findByPrecioGreaterThanEqual(precio)
        .stream()
        .map(ServicioMapper::toDTO)
        .toList();
  }

  @Override
  public List<ServicioDTO> buscarPorRangoPrecio(BigDecimal min, BigDecimal max) {
    return servicioRepository.findByPrecioBetween(min, max)
        .stream()
        .map(ServicioMapper::toDTO)
        .toList();
  }
}
