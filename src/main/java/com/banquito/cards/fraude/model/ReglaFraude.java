package com.banquito.cards.fraude.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.EqualsAndHashCode;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@EqualsAndHashCode(of = "codRegla")
@Entity
@Table(name = "REGLA_FRAUDE")
public class ReglaFraude implements Serializable {

    public static final String TIPO_TRANSACCIONES = "TRX";
    public static final String TIPO_MONTO = "MNT";
    public static final String TIPO_UBICACION = "GEO";
    public static final String TIPO_COMERCIO = "COM";
    public static final String TIPO_HORARIO = "HOR";
    
    public static final String NIVEL_RIESGO_BAJO = "BAJ";
    public static final String NIVEL_RIESGO_MEDIO = "MED";
    public static final String NIVEL_RIESGO_ALTO = "ALT";
    
    public static final String ESTADO_ACTIVO = "ACT";
    public static final String ESTADO_INACTIVO = "INA";
    
    public static final String PERIODO_MINUTOS = "MIN";
    public static final String PERIODO_HORAS = "HOR";
    public static final String PERIODO_DIAS = "DIA";

    @Id
    @Column(name = "COD_REGLA", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer codRegla;

    @NotNull
    @Size(min = 5, max = 50)
    @Column(name = "NOMBRE_REGLA", length = 50, nullable = false)
    private String nombreRegla;

    @NotNull
    @Size(min = 10, max = 500)
    @Column(name = "DESCRIPCION", length = 500)
    private String descripcion;

    @NotNull
    @Pattern(regexp = "TRX|MNT|GEO|COM|HOR")
    @Column(name = "TIPO_REGLA", length = 3, nullable = false)
    private String tipoRegla;

    @Min(1)
    @Max(999999999)
    @Column(name = "LIMITE_TRANSACCIONES", precision = 9, scale = 0)
    private BigDecimal limiteTransacciones;

    @NotNull
    @Pattern(regexp = "MIN|HOR|DIA")
    @Column(name = "PERIODO_TIEMPO", length = 3)
    private String periodoTiempo;

    @DecimalMin("0.01")
    @DecimalMax("999999999999999999.99")
    @Column(name = "LIMITE_MONTO_TOTAL", precision = 18, scale = 2)
    private BigDecimal limiteMontoTotal;

    @Column(name = "PAISES_PERMITIDOS", length = 1000)
    private String paisesPermitidos;

    @Column(name = "COMERCIOS_EXCLUIDOS", length = 1000)
    private String comerciosExcluidos;

    @Column(name = "HORA_INICIO")
    private LocalDateTime horaInicio;

    @Column(name = "HORA_FIN")
    private LocalDateTime horaFin;

    @DecimalMin("0.00")
    @DecimalMax("100.00")
    @Column(name = "PUNTAJE_RIESGO", precision = 5, scale = 2)
    private BigDecimal puntajeRiesgo;

    @NotNull
    @Pattern(regexp = "BAJ|MED|ALT")
    @Column(name = "NIVEL_RIESGO", length = 3)
    private String nivelRiesgo;

    @NotNull
    @Pattern(regexp = "ACT|INA")
    @Column(name = "ESTADO", length = 3, nullable = false)
    private String estado;

    @NotNull
    @Min(1)
    @Max(999)
    @Column(name = "PRIORIDAD", nullable = false)
    private Integer prioridad;

    @NotNull
    @PastOrPresent
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "FECHA_CREACION", nullable = false)
    private LocalDateTime fechaCreacion;

    @PastOrPresent
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "FECHA_ACTUALIZACION")
    private LocalDateTime fechaActualizacion;

    @Column(name = "USUARIO_CREACION", length = 50)
    private String usuarioCreacion;

    @Column(name = "USUARIO_ACTUALIZACION", length = 50)
    private String usuarioActualizacion;

    public ReglaFraude(Integer codRegla) {
        this.codRegla = codRegla;
    }
}

