package com.gamm.hotel_api.repository;

import com.gamm.hotel_api.model.entity.Habitacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HabitacionRepository extends JpaRepository<Habitacion, Integer> {
  // Buscar por habitacion dentro de un hotel
  Optional<Habitacion> findByNumeroAndHotel_Id(String numero, Integer hotelId);

  // Buscar todas las habitaciones de un hotel por id
  List<Habitacion> findByHotel_Id(Integer hotelId);

  // Buscar habitaciones por estado (libre, ocupada, mantenimiento)
  List<Habitacion> findByEstado(String estado); // o EstadoHabitacion enum

  List<Habitacion> findByHotel_IdAndEstado(Integer hotelId, String estado);

  // Buscar habitacions por tipo
  List<Habitacion> findByTipoHabitacion_Nombre(String tipo);

  List<Habitacion> findByHotel_IdAndTipoHabitacion_Nombre(Integer hotelId, String tipo);

}
