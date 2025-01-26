package com.banquito.cards.transaccion.model;

import com.banquito.cards.comision.model.Banco;
import com.banquito.cards.comision.model.Comision;
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
@EqualsAndHashCode(of = "codigo")
@Entity
@Table(name = "TRANSACCION")
public class Transaccion implements Serializable {

    public static final String MODALIDAD_SIMPLE = "SIM";
    public static final String MODALIDAD_RECURRENTE = "REC";
    
    public static final String ESTADO_PENDIENTE = "PEN";
    public static final String ESTADO_APROBADA = "APR";
    public static final String ESTADO_RECHAZADA = "REC";
    public static final String ESTADO_REVISION = "REV";
    public static final String ESTADO_PROCESADO = "PRO";

    @Id
    @Column(name = "COD_TRANSACCION", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer codigo;
    
    @NotNull
    @ManyToOne
    @JoinColumn(name = "COD_BANCO", referencedColumnName = "COD_BANCO", nullable = false)
    private Banco banco;
    
    @NotNull
    @ManyToOne
    @JoinColumn(name = "COD_COMISION", referencedColumnName = "COD_COMISION")
    private Comision comision;
    
    @NotNull
    @DecimalMin("0.01")
    @DecimalMax("999999999999999999.99")
    @Column(name = "MONTO", nullable = false, precision = 18, scale = 2)
    private BigDecimal monto = BigDecimal.ZERO;

    @NotNull
    @Pattern(regexp = "SIM|REC")
    @Column(name = "MODALIDAD", length = 3, nullable = false)
    private String modalidad;
    
    @NotNull
    @Size(min = 3, max = 3)
    @Column(name = "CODIGO_MONEDA", length = 3, nullable = false)
    private String codigoMoneda;
    
    @NotNull
    @Size(min = 2, max = 4)
    @Column(name = "MARCA", length = 4, nullable = false)
    private String marca;
    
    @NotNull
    @Size(min = 4, max = 64)
    @Column(name = "FECHA_EXPIRACION_TARJETA", length = 64, nullable = false)
    private String fechaExpiracionTarjeta;
    
    @NotNull
    @Size(min = 5, max = 128)
    @Column(name = "NOMBRE_TARJETA", length = 128, nullable = false)
    private String nombreTarjeta;
    
    @NotNull
    @Size(min = 16, max = 128)
    @Column(name = "NUMERO_TARJETA", length = 128, nullable = false)
    private String numeroTarjeta;
    
    @NotNull
    @Size(min = 10, max = 256)
    @Column(name = "DIRECCION_TARJETA", length = 256, nullable = false)
    private String direccionTarjeta;
    
    @NotNull
    @Size(min = 3, max = 128)
    @Column(name = "CVV", length = 128, nullable = false)
    private String cvv;

    @NotNull
    @Size(min = 2, max = 2)
    @Column(name = "PAIS", length = 2, nullable = false)
    private String pais;
    
    @NotNull
    @Pattern(regexp = "PEN|APR|REC|REV|PRO")
    @Column(name = "ESTADO", length = 3, nullable = false)
    private String estado;
    
    @Size(max = 50)
    @Column(name = "DETALLE", length = 50)
    private String detalle;

    @Size(min = 32, max = 64)
    @Column(name = "CODIGO_UNICO_TRANSACCION", length = 64)
    private String codigoUnicoTransaccion;
    
    @NotNull
    @PastOrPresent
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "FECHA_CREACION", nullable = false)
    private LocalDateTime fechaCreacion;

    @Future
    @Column(name = "FECHA_EJECUCION_RECURRENCIA")
    private LocalDateTime fechaEjecucionRecurrencia;

    @Future
    @Column(name = "FECHA_FIN_RECURRENCIA")
    private LocalDateTime fechaFinRecurrencia;

    @DecimalMin("0.00")
    @DecimalMax("100.00")
    @Column(name = "GTW_COMISION", length = 20)
    private String gtwComision = "0.00";

    @Size(min = 8, max = 20)
    @Column(name = "GTW_CUENTA", length = 20)
    private String gtwCuenta;

    @Size(min = 8, max = 20)
    @Column(name = "NUMERO_CUENTA", length = 20)
    private String numeroCuenta;

    @Min(1)
    @Max(48)
    @Column(name = "CUOTAS")
    private Integer cuotas;

    @Column(name = "INTERES_DIFERIDO")
    private Boolean interesDiferido;

    @Size(max = 128)
    @Column(name = "BENEFICIARIO", length = 128)
    private String beneficiario;

    public Transaccion(Integer codigo) {
        this.codigo = codigo;
    }
}
