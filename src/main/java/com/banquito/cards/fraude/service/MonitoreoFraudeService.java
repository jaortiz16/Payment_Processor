package com.banquito.cards.fraude.service;

import com.banquito.cards.fraude.model.MonitoreoFraude;
import com.banquito.cards.fraude.model.ReglaFraude;
import com.banquito.cards.fraude.repository.MonitoreoFraudeRepository;
import com.banquito.cards.fraude.repository.ReglaFraudeRepository;
import com.banquito.cards.transaccion.model.Transaccion;
import com.banquito.cards.transaccion.repository.TransaccionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@Transactional
public class MonitoreoFraudeService {

    private static final String ESTADO_ACTIVO = "ACT";
    private static final String TIPO_REGLA_MONTO = "MNT";
    private static final String TIPO_REGLA_TRANSACCION = "TRX";
    private static final String TIPO_REGLA_UBICACION = "UBI";
    private static final String PERIODO_DIA = "DIA";
    private static final String PERIODO_HORA = "HOR";
    private static final String PERIODO_SEMANA = "SEM";

    private final MonitoreoFraudeRepository monitoreoFraudeRepository;
    private final ReglaFraudeRepository reglaFraudeRepository;
    private final TransaccionRepository transaccionRepository;

    public MonitoreoFraudeService(MonitoreoFraudeRepository monitoreoFraudeRepository,
                                ReglaFraudeRepository reglaFraudeRepository,
                                TransaccionRepository transaccionRepository) {
        this.monitoreoFraudeRepository = monitoreoFraudeRepository;
        this.reglaFraudeRepository = reglaFraudeRepository;
        this.transaccionRepository = transaccionRepository;
    }

    public String evaluarRiesgoTransaccion(Transaccion transaccion) {
        if (transaccion == null) {
            throw new RuntimeException("La transacción es requerida para evaluar el riesgo");
        }

        try {
            String nivelRiesgoMayor = "BAJ";
            List<ReglaFraude> reglasActivas = reglaFraudeRepository.findByEstado(ESTADO_ACTIVO);

            if (reglasActivas.isEmpty()) {
                return nivelRiesgoMayor;
            }

            for (ReglaFraude regla : reglasActivas) {
                boolean cumpleRegla = false;

                switch (regla.getTipoRegla()) {
                    case TIPO_REGLA_MONTO:
                        cumpleRegla = evaluarReglaMonto(transaccion, regla);
                        break;
                    case TIPO_REGLA_TRANSACCION:
                        cumpleRegla = evaluarReglaTransacciones(transaccion, regla);
                        break;
                    case TIPO_REGLA_UBICACION:
                        cumpleRegla = evaluarReglaUbicacion(transaccion, regla);
                        break;
                    default:
                        continue;
                }

                if (cumpleRegla) {
                    try {
                        MonitoreoFraude alerta = new MonitoreoFraude();
                        alerta.setTransaccion(transaccion);
                        alerta.setReglaFraude(regla);
                        alerta.setNivelRiesgo(regla.getNivelRiesgo());
                        alerta.setPuntajeRiesgo(calcularPuntajeRiesgo(regla));
                        alerta.setEstado("PEN");
                        alerta.setDetalle(generarDetalleFraude(transaccion, regla));
                        alerta.setFechaDeteccion(LocalDateTime.now());
                        alerta.setCodigoUnicoTransaccion(transaccion.getCodigoUnicoTransaccion());
                        monitoreoFraudeRepository.save(alerta);

                        if (compararNivelRiesgo(regla.getNivelRiesgo(), nivelRiesgoMayor) > 0) {
                            nivelRiesgoMayor = regla.getNivelRiesgo();
                        }
                    } catch (Exception e) {
                        throw new RuntimeException("Error al crear monitoreo de fraude: " + e.getMessage());
                    }
                }
            }

            return nivelRiesgoMayor;
        } catch (Exception e) {
            throw new RuntimeException("Error al evaluar riesgo de transacción: " + e.getMessage());
        }
    }

    private boolean evaluarReglaMonto(Transaccion transaccion, ReglaFraude regla) {
        if (regla.getLimiteMontoTotal() == null) {
            return false;
        }
        return transaccion.getMonto().compareTo(regla.getLimiteMontoTotal()) > 0;
    }

    private boolean evaluarReglaTransacciones(Transaccion transaccion, ReglaFraude regla) {
        if (regla.getLimiteTransacciones() == null || regla.getPeriodoTiempo() == null) {
            return false;
        }

        LocalDateTime fechaInicio = calcularFechaInicio(transaccion.getFechaCreacion(), regla.getPeriodoTiempo());
        List<Transaccion> transaccionesPeriodo = transaccionRepository
            .findByNumeroTarjetaAndFechaCreacionBetween(
                transaccion.getNumeroTarjeta(),
                fechaInicio,
                transaccion.getFechaCreacion());

        return transaccionesPeriodo.size() >= regla.getLimiteTransacciones().intValue();
    }

    private boolean evaluarReglaUbicacion(Transaccion transaccion, ReglaFraude regla) {
        if (regla.getPeriodoTiempo() == null) {
            return false;
        }

        LocalDateTime fechaInicio = calcularFechaInicio(transaccion.getFechaCreacion(), regla.getPeriodoTiempo());
        List<Transaccion> transaccionesPeriodo = transaccionRepository
            .findByNumeroTarjetaAndFechaCreacionBetween(
                transaccion.getNumeroTarjeta(),
                fechaInicio,
                transaccion.getFechaCreacion());

        // Verificar si hay transacciones de diferentes países en el período
        return transaccionesPeriodo.stream()
            .map(Transaccion::getPais)
            .distinct()
            .count() > 1;
    }

    private LocalDateTime calcularFechaInicio(LocalDateTime fechaReferencia, String periodoTiempo) {
        switch (periodoTiempo) {
            case PERIODO_HORA:
                return fechaReferencia.minus(1, ChronoUnit.HOURS);
            case PERIODO_DIA:
                return fechaReferencia.minus(1, ChronoUnit.DAYS);
            case PERIODO_SEMANA:
                return fechaReferencia.minus(7, ChronoUnit.DAYS);
            default:
                throw new RuntimeException("Periodo de tiempo no válido");
        }
    }

    private int compararNivelRiesgo(String nivel1, String nivel2) {
        int pesoNivel1 = obtenerPesoNivelRiesgo(nivel1);
        int pesoNivel2 = obtenerPesoNivelRiesgo(nivel2);
        return Integer.compare(pesoNivel1, pesoNivel2);
    }

    private int obtenerPesoNivelRiesgo(String nivel) {
        switch (nivel) {
            case "BAJ": return 1;
            case "MED": return 2;
            case "ALT": return 3;
            default: return 0;
        }
    }

    private BigDecimal calcularPuntajeRiesgo(ReglaFraude regla) {
        switch (regla.getNivelRiesgo()) {
            case "BAJ": return new BigDecimal("0.3");
            case "MED": return new BigDecimal("0.6");
            case "ALT": return new BigDecimal("0.9");
            default: return BigDecimal.ZERO;
        }
    }

    private String generarDetalleFraude(Transaccion transaccion, ReglaFraude regla) {
        StringBuilder detalle = new StringBuilder();
        detalle.append("Alerta de fraude detectada - ");
        
        switch (regla.getTipoRegla()) {
            case TIPO_REGLA_MONTO:
                detalle.append("Monto excede el límite establecido. ");
                detalle.append("Monto transacción: ").append(transaccion.getMonto());
                detalle.append(", Límite: ").append(regla.getLimiteMontoTotal());
                break;
            case TIPO_REGLA_TRANSACCION:
                detalle.append("Exceso de transacciones en período de tiempo. ");
                detalle.append("Regla: ").append(regla.getLimiteTransacciones());
                detalle.append(" transacciones por ").append(regla.getPeriodoTiempo().toLowerCase());
                break;
            case TIPO_REGLA_UBICACION:
                detalle.append("Transacciones desde diferentes países en corto tiempo. ");
                detalle.append("País actual: ").append(transaccion.getPais());
                break;
        }
        
        return detalle.toString();
    }

    @Transactional(readOnly = true)
    public List<MonitoreoFraude> obtenerAlertasPendientes() {
        return monitoreoFraudeRepository.findByEstado("PEN");
    }

    @Transactional(readOnly = true)
    public List<MonitoreoFraude> obtenerAlertasPorFecha(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        if (fechaInicio == null || fechaFin == null) {
            throw new RuntimeException("Las fechas de inicio y fin son requeridas");
        }
        if (fechaInicio.isAfter(fechaFin)) {
            throw new RuntimeException("La fecha de inicio no puede ser posterior a la fecha fin");
        }
        return monitoreoFraudeRepository.findByFechaDeteccionBetween(fechaInicio, fechaFin);
    }

    @Transactional(readOnly = true)
    public List<MonitoreoFraude> obtenerAlertasPorTransaccion(Integer codTransaccion) {
        if (codTransaccion == null) {
            throw new RuntimeException("El código de transacción es requerido");
        }
        return monitoreoFraudeRepository.findByTransaccionCodigo(codTransaccion);
    }

    @Transactional
    public void procesarAlerta(Integer id, String estado, String detalle) {
        if (id == null) {
            throw new RuntimeException("El ID de la alerta es requerido");
        }
        if (estado == null || estado.trim().isEmpty()) {
            throw new RuntimeException("El estado es requerido");
        }

        MonitoreoFraude alerta = monitoreoFraudeRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("No existe la alerta con ID: " + id));

        if (!"PEN".equals(alerta.getEstado())) {
            throw new RuntimeException("Solo se pueden procesar alertas pendientes");
        }

        alerta.setEstado(estado);
        alerta.setDetalle(detalle);
        alerta.setFechaProcesamiento(LocalDateTime.now());
        monitoreoFraudeRepository.save(alerta);
    }

    public void actualizarEstadoMonitoreoFraude(String codigoUnicoTransaccion, String estado) {
        // Actualizar el estado en la tabla de monitoreo de fraude
        MonitoreoFraude monitoreo = monitoreoFraudeRepository.findByCodigoUnicoTransaccion(codigoUnicoTransaccion)
                .orElseThrow(() -> new RuntimeException("No se encontró el registro de monitoreo de fraude"));
        monitoreo.setEstado(estado);
        monitoreoFraudeRepository.save(monitoreo);
    }
}
