package com.banquito.cards.comision.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Entity
@Table(name = "COMISION")
public class Comision implements Serializable {

    public static final String TIPO_PORCENTAJE = "POR";
    public static final String TIPO_FIJO = "FIJ";

    @Id
    @Column(name = "COD_COMISION", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer codigo;

    @NotNull
    @Column(name = "TIPO", length = 3, nullable = false)
    private String tipo;

    @NotNull
    @PositiveOrZero
    @Column(name = "MONTO_BASE", precision = 20, scale = 4, nullable = false)
    private BigDecimal montoBase;

    @NotNull
    @PositiveOrZero
    @Column(name = "TRANSACCIONES_BASE", precision = 9, scale = 0, nullable = false)
    private Integer transaccionesBase;

    @NotNull
    @Column(name = "MANEJA_SEGMENTOS", nullable = false)
    private Boolean manejaSegmentos;

    @NotNull
    @PastOrPresent
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "FECHA_CREACION", nullable = false)
    private LocalDateTime fechaCreacion;

    public Comision(Integer codigo) {
        this.codigo = codigo;
    }
}
