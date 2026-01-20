package com.gamm.hotel_api.repository;

import com.gamm.hotel_api.model.entity.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HotelRepository extends JpaRepository<Hotel, Integer> {
  // Buscar por nombre
  List<Hotel> findByNombreContainingIgnoreCase(String nombre);

  // Buscar por ciudad
  List<Hotel> findByCiudadIgnoreCase(String ciudad);

  // Buscar por número de estrellas
  List<Hotel> findByEstrellas(Integer estrellas);

  // Buscar por rango de estrellas
  List<Hotel> findByEstrellasBetween(Integer min, Integer max);

  // Buscar por dirección
  List<Hotel> findByDireccionContainingIgnoreCase(String direccion);
}
