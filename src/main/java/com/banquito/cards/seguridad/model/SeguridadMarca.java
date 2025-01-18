package com.banquito.cards.seguridad.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "SEGURIDAD_MARCA")
public class SeguridadMarca implements Serializable {

    @Id
    @Column(name = "MARCA", length = 4, nullable = false)
    private String marca;
    
    @NotNull
    @Column(name = "CLAVE", length = 128, nullable = false)
    private String clave;
    
    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "FECHA_ACTUALIZACION", nullable = false)
    private LocalDateTime fechaActualizacion;

    public SeguridadMarca(String marca) {
        this.marca = marca;
    }
}
