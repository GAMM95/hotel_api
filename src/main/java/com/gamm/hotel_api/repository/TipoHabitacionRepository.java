package com.gamm.hotel_api.repository;

import com.gamm.hotel_api.model.entity.TipoHabitacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface TipoHabitacionRepository extends JpaRepository<TipoHabitacion, Integer> {

  Optional<TipoHabitacion> findByNombreIgnoreCase(String nombre);

  List<TipoHabitacion> findByCapacidad(Integer capacidad);

  List<TipoHabitacion> findByPrecioBaseBetween(Double min, Double max);

}
