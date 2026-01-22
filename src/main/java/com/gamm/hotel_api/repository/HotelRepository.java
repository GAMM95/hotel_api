package com.gamm.hotel_api.repository;

import com.gamm.hotel_api.model.entity.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HotelRepository extends JpaRepository<Hotel, Integer> {
  // Buscar por nombre
  List<Hotel> findByNombreContainingIgnoreCase(String nombre);

  // Buscar por ciudad
  List<Hotel> findByCiudadContainingIgnoreCase(String ciudad);

  // Buscar por número de estrellas
  List<Hotel> findByEstrellas(Integer estrellas);

  // Buscar por nombre de hotel y ciudad
  Optional<Hotel> findByNombreIgnoreCaseAndCiudadIgnoreCase(String nombre, String ciudad);

  // Buscar por rango de estrellas
  List<Hotel> findByEstrellasBetween(Integer min, Integer max);

  // Buscar por dirección
  List<Hotel> findByDireccionContainingIgnoreCase(String direccion);

  Integer id(Integer id);
}
