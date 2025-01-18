package com.banquito.cards.seguridad.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "SEGURIDAD_BANCO")
public class SeguridadBanco implements Serializable {

    @Id
    @Column(name = "COD_SEGURIDAD_BANCO", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer code;
    
    @NotNull
    @Column(name = "CLAVE", length = 128, nullable = false)
    private String clave;
    
    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "FECHA_ACTUALIZACION", nullable = false)
    private LocalDateTime fechaActualizacion;
    
    @NotNull
    @Temporal(TemporalType.DATE)
    @Column(name = "FECHA_ACTIVACION", nullable = false)
    private LocalDate fechaActivacion;
    
    @NotNull
    @Column(name = "ESTADO", length = 3, nullable = false)
    private String estado;

    public SeguridadBanco(Integer code) {
        this.code = code;
    }
}
