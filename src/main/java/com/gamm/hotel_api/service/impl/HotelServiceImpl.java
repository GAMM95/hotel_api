package com.gamm.hotel_api.service.impl;

import com.gamm.hotel_api.dto.HotelDTO;
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
    // DTO → Entity
    Hotel hotel = HotelMapper.toEntity(dto);

    Hotel guardado = hotelRepository.save(hotel);
    return HotelMapper.toDTO(guardado);
  }

  @Override
  public HotelDTO actualizarHotel(Integer id, HotelDTO dto) {
    Hotel hotel = hotelRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Hotel no encontrado"));

    // Actualizar los datos con el mapper
    HotelMapper.updateEntity(hotel, dto);

    Hotel actualizado = hotelRepository.save(hotel);
    return HotelMapper.toDTO(actualizado);
  }

  @Override
  public void eliminar(Integer id) {
    if (!hotelRepository.existsById(id)) {
      throw new NotFoundException("Hotel no encontrado");
    }
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
    return hotelRepository.findByCiudadIgnoreCase(ciudad)
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
}
