package com.gamm.hotel_api.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "hotel")
public class Hotel {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(nullable = false, length = 150)
  private String nombre;

  @Column(nullable = false)
  private String direccion;

  @Column(nullable = false, length = 100)
  private String ciudad;

  @Column(length = 20)
  private String telefono;

  private Integer estrellas;
}
