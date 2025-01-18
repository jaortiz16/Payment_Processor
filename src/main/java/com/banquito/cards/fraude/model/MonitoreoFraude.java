package com.banquito.cards.fraude.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.math.BigDecimal;
import com.banquito.cards.transaccion.model.Transaccion;

@Data
@NoArgsConstructor
@Entity
@Table(name = "MONITOREO_FRAUDE")
public class MonitoreoFraude implements Serializable {

    public static final String ESTADO_PENDIENTE = "PEN";
    public static final String ESTADO_PROCESADO = "PRO";
    public static final String ESTADO_RECHAZADO = "REC";
    public static final String ESTADO_APROBADO = "APR";
    public static final String ESTADO_EN_REVISION = "REV";
    
    public static final String NIVEL_RIESGO_BAJO = "BAJ";
    public static final String NIVEL_RIESGO_MEDIO = "MED";
    public static final String NIVEL_RIESGO_ALTO = "ALT";

    @Id
    @Column(name = "COD_MONITOREO_FRAUDE", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer codigo;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COD_REGLA", referencedColumnName = "COD_REGLA")
    private ReglaFraude reglaFraude;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COD_TRANSACCION", referencedColumnName = "COD_TRANSACCION")
    private Transaccion transaccion;

    @NotNull
    @Pattern(regexp = "BAJ|MED|ALT")
    @Column(name = "NIVEL_RIESGO", length = 3, nullable = false)
    private String nivelRiesgo;

    @NotNull
    @DecimalMin("0.00")
    @DecimalMax("100.00")
    @Column(name = "PUNTAJE_RIESGO", precision = 5, scale = 2, nullable = false)
    private BigDecimal puntajeRiesgo;

    @NotNull
    @Pattern(regexp = "PEN|PRO|REC|APR|REV")
    @Column(name = "ESTADO", length = 3, nullable = false)
    private String estado;

    @Size(max = 500)
    @Column(name = "DETALLE", length = 500)
    private String detalle;

    @Size(max = 200)
    @Column(name = "ACCION_TOMADA", length = 200)
    private String accionTomada;

    @Column(name = "REQUIERE_VERIFICACION_ADICIONAL")
    private Boolean requiereVerificacionAdicional;

    @Size(max = 200)
    @Column(name = "MOTIVO_VERIFICACION", length = 200)
    private String motivoVerificacion;

    @NotNull
    @PastOrPresent
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "FECHA_DETECCION", nullable = false)
    private LocalDateTime fechaDeteccion;

    @PastOrPresent
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "FECHA_PROCESAMIENTO")
    private LocalDateTime fechaProcesamiento;

    @Size(min = 32, max = 64)
    @Column(name = "CODIGO_UNICO_TRANSACCION", length = 64)
    private String codigoUnicoTransaccion;

    @Column(name = "USUARIO_PROCESAMIENTO", length = 50)
    private String usuarioProcesamiento;

    @Column(name = "IP_ORIGEN", length = 50)
    private String ipOrigen;

    @Column(name = "UBICACION_GEOGRAFICA", length = 200)
    private String ubicacionGeografica;
}
