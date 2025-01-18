package com.banquito.cards.comision.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Entity
@Table(name = "BANCO")
public class Banco implements Serializable {

    @Id
    @Column(name = "COD_BANCO", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer codigo;

    @NotNull
    @Column(name = "CODIGO_INTERNO", length = 10, nullable = false)
    private String codigoInterno;

    @NotNull
    @Column(name = "RUC", length = 13, nullable = false)
    private String ruc;

    @NotNull
    @Column(name = "RAZON_SOCIAL", length = 100, nullable = false)
    private String razonSocial;

    @NotNull
    @Column(name = "NOMBRE_COMERCIAL", length = 100, nullable = false)
    private String nombreComercial;

    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "FECHA_CREACION", nullable = false)
    private LocalDateTime fechaCreacion;

    @ManyToOne
    @JoinColumn(name = "COD_COMISION", referencedColumnName = "COD_COMISION")
    private Comision comision;

    @NotNull
    @Column(name = "ESTADO", length = 3, nullable = false)
    private String estado;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "FECHA_INACTIVACION")
    private LocalDateTime fechaInactivacion;

    public Banco(Integer codigo) {
        this.codigo = codigo;
    }
}
