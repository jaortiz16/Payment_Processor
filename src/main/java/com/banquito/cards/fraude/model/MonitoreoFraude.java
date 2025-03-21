package com.banquito.cards.fraude.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.EqualsAndHashCode;
import java.io.Serializable;
import java.time.LocalDateTime;

import com.banquito.cards.transaccion.model.Transaccion;

@Getter
@Setter
@ToString
@NoArgsConstructor
@EqualsAndHashCode(of = "codigo")
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
    @Column(name = "COD_MONITOREO_FRAUDE", length = 36, nullable = false)
    private String codigo;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COD_REGLA", referencedColumnName = "COD_REGLA", nullable = false)
    private ReglaFraude reglaFraude;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COD_TRANSACCION", referencedColumnName = "COD_TRANSACCION", nullable = false)
    private Transaccion transaccion;

    @NotNull
    @Pattern(regexp = "BAJ|MED|ALT")
    @Column(name = "NIVEL_RIESGO", length = 32, nullable = false)
    private String nivelRiesgo;

    @NotNull
    @DecimalMin("0.00")
    @DecimalMax("100.00")
    @Column(name = "PUNTAJE_RIESGO", nullable = false)
    private Integer puntajeRiesgo;

    @NotNull
    @Pattern(regexp = "PEN|PRO|REC|APR|REV")
    @Column(name = "ESTADO", length = 3, nullable = false)
    private String estado;

    @Size(max = 500)
    @Column(name = "DETALLE", length = 500)
    private String detalle;

    @Size(max = 128)
    @Column(name = "ACCION_TOMADA", length = 128)
    private String accionTomada;

    @Column(name = "REQUIERE_VERIFICACION_ADICIONAL", nullable = false)
    private Boolean requiereVerificacionAdicional;

    @Size(max = 128)
    @Column(name = "MOTIVO_VERIFICACION", length = 128)
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
    @Column(name = "CODIGO_UNICO_TRANSACCION", length = 64, nullable = false)
    private String codigoUnicoTransaccion;

    @Column(name = "USUARIO_PROCESAMIENTO", length = 64)
    private String usuarioProcesamiento;

    @Column(name = "IP_ORIGEN", length = 64)
    private String ipOrigen;

    @Column(name = "UBICACION_GEOGRAFICA", length = 128)
    private String ubicacionGeografica;

    @PastOrPresent
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "FECHA_ACTUALIZACION")
    private LocalDateTime fechaActualizacion;

    public MonitoreoFraude(String codigo) {
        this.codigo = codigo;
    }
}
