package com.gamm.hotel_api.service.impl;

import com.gamm.hotel_api.dto.HotelDTO;
import com.gamm.hotel_api.exceptions.BadRequestException;
import com.gamm.hotel_api.exceptions.NotFoundException;
import com.gamm.hotel_api.mapper.HotelMapper;
import com.gamm.hotel_api.model.entity.Hotel;
import com.gamm.hotel_api.repository.HotelRepository;
import com.gamm.hotel_api.service.HotelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HotelServiceImpl implements HotelService {

  @Autowired
  private HotelRepository hotelRepository;

  @Override
  public List<HotelDTO> listar() {
    return hotelRepository.findAll()
        .stream()
        .map(HotelMapper::toDTO)
        .toList();
  }

  @Override
  public HotelDTO obtenerPorId(Integer id) {
    Hotel hotel = hotelRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Hotel no encontrado"));
    return HotelMapper.toDTO(hotel);
  }

  @Override
  public HotelDTO registrarHotel(HotelDTO dto) {
    // Validar datos mínimos
    if (dto.getNombre() == null || dto.getNombre().isBlank()) {
      throw new BadRequestException("El nombre del hotel es obligatorio");
    }

    if (dto.getCiudad() == null || dto.getCiudad().isBlank()) {
      throw new BadRequestException("La ciudad del hotel es obligatoria");
    }

    if (dto.getEstrellas() == null || dto.getEstrellas() < 1 || dto.getEstrellas() > 5) {
      throw new BadRequestException("Las estrellas deben estar entre 1 y 5");
    }

    // Validar unicidad: nombre + ciudad
    hotelRepository.findByNombreIgnoreCaseAndCiudadIgnoreCase(dto.getNombre(), dto.getCiudad())
        .ifPresent(hotel -> {
          throw new BadRequestException(
              "Ya existe un hotel con nombre '" + dto.getNombre() +
                  "' en la ciudad '" + dto.getCiudad() + "'"
          );
        });

    // Conversion del DTO a entidad
    Hotel hotel = HotelMapper.toEntity(dto);

    // Persistencia en la base de datos
    Hotel guardado = hotelRepository.save(hotel);
    // Retornar en formato DTO
    return HotelMapper.toDTO(guardado);
  }

  @Override
  public HotelDTO actualizarHotel(Integer id, HotelDTO dto) {
    // 1. Verificar que el hotel exista antes de actualizar
    Hotel hotel = hotelRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Hotel no encontrado"));

    // 2. Validar el rango de estrellas
    if (dto.getEstrellas() != null && (dto.getEstrellas() < 1 || dto.getEstrellas() > 5)) {
      throw new BadRequestException("Las estrellas deben estar entre 1 y 5");
    }

    // 3. Validar duplicado solo si cambia nombre o ciudad
    hotelRepository.findByNombreIgnoreCaseAndCiudadIgnoreCase(dto.getNombre(), dto.getCiudad())
        .ifPresent(hotelEncontrado -> {
          if (!hotelEncontrado.getId().equals(hotel.getId())) {
            throw new BadRequestException(
                "Ya existe otro hotel con nombre '" + dto.getNombre() +
                    "' en la ciudad '" + dto.getCiudad() + "'"
            );
          }
        });

    // 4. Actualizar los datos con el mapper
    HotelMapper.updateEntity(hotel, dto);
    // 5. Persistencia en los cambios
    Hotel actualizado = hotelRepository.save(hotel);
    // 6. Retornar la version actualizada en formato DTO
    return HotelMapper.toDTO(actualizado);
  }

  @Override
  public void eliminar(Integer id) {
    // Verificar que el hotel exista antes de intentar eliminarlo
    if (!hotelRepository.existsById(id)) {
      throw new NotFoundException("Hotel no encontrado");
    }
    // Eliminación física del registro
    hotelRepository.deleteById(id);
  }

  // Busquedas personalizadas
  @Override
  public List<HotelDTO> buscarPorNombre(String nombre) {
    return hotelRepository.findByNombreContainingIgnoreCase(nombre)
        .stream()
        .map(HotelMapper::toDTO)
        .toList();
  }

  @Override
  public List<HotelDTO> buscarPorCiudad(String ciudad) {
    return hotelRepository.findByCiudadContainingIgnoreCase(ciudad)
        .stream()
        .map(HotelMapper::toDTO)
        .toList();
  }

  @Override
  public List<HotelDTO> buscarPorEstrellas(Integer estrellas) {
    return hotelRepository.findByEstrellas(estrellas)
        .stream()
        .map(HotelMapper::toDTO)
        .toList();
  }

  @Override
  public List<HotelDTO> buscarPorRangoEstrellas(Integer min, Integer max) {
    return hotelRepository.findByEstrellasBetween(min, max)
        .stream()
        .map(HotelMapper::toDTO)
        .toList();
  }

  @Override
  public List<HotelDTO> buscarPorDireccion(String direccion) {
    return hotelRepository.findByDireccionContainingIgnoreCase(direccion)
        .stream()
        .map(HotelMapper::toDTO)
        .toList();
  }
}