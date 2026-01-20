package com.gamm.hotel_api.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "huesped")
public class Huesped {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @OneToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "persona_id", nullable = false, unique = true)
  private Persona persona;

  @Column(length = 100)
  private String nacionalidad;
}
