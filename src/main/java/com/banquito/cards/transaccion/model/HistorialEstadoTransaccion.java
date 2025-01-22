package com.banquito.cards.transaccion.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Entity
@Table(name = "HISTORIAL_ESTADO_TRANSACCION")
public class HistorialEstadoTransaccion implements Serializable {

    public static final String ESTADO_PENDIENTE = "PEN";
    public static final String ESTADO_APROBADA = "APR";
    public static final String ESTADO_RECHAZADA = "REC";
    public static final String ESTADO_REVISION = "REV";
    public static final String ESTADO_PROCESADO = "PRO";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "COD_HISTORIAL_ESTADO", nullable = false)
    private Integer codHistorialEstado;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CODIGO_TRANSACCION", referencedColumnName = "COD_TRANSACCION", nullable = false)
    private Transaccion transaccion;

    @NotNull
    @Pattern(regexp = "PEN|APR|REC|REV|PRO")
    @Column(name = "ESTADO", length = 3, nullable = false)
    private String estado;

    @NotNull
    @PastOrPresent
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "FECHA_ESTADO_CAMBIO")
    private LocalDateTime fechaEstadoCambio;

    @Size(max = 200)
    @Column(name = "DETALLE", length = 200)
    private String detalle;
}
