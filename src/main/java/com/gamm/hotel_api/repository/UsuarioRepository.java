package com.gamm.hotel_api.repository;

import com.gamm.hotel_api.model.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
  Optional<Usuario> findByUsername(String username);
}
