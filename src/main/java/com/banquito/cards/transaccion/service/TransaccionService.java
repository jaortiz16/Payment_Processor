package com.banquito.cards.transaccion.service;

import com.banquito.cards.transaccion.model.*;
import com.banquito.cards.transaccion.repository.TransaccionRepository;
import com.banquito.cards.transaccion.repository.HistorialEstadoTransaccionRepository;
import com.banquito.cards.transaccion.controller.dto.*;
import com.banquito.cards.transaccion.controller.mapper.TransaccionMapper;
import com.banquito.cards.transaccion.client.*;
import com.banquito.cards.comision.service.ComisionService;
import com.banquito.cards.comision.model.Banco;
import com.banquito.cards.comision.repository.BancoRepository;
import com.banquito.cards.exception.NotFoundException;
import com.banquito.cards.exception.BusinessException;
import com.banquito.cards.fraude.service.MonitoreoFraudeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Propagation;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class TransaccionService {
    
    private static final String ENTITY_NAME = "Transaccion";
    private static final String MODALIDAD_SIMPLE = "SIM";
    private static final String MODALIDAD_RECURRENTE = "REC";
    
    private static final String ESTADO_PENDIENTE = "PEN";
    private static final String ESTADO_APROBADA = "APR";
    private static final String ESTADO_RECHAZADA = "REC";
    private static final String ESTADO_REVISION = "REV";
    private static final String ESTADO_PROCESADO = "PRO";
    
    private final TransaccionRepository transaccionRepository;
    private final HistorialEstadoTransaccionRepository historialRepository;
    private final MonitoreoFraudeService monitoreoFraudeService;
    private final ComisionService comisionService;
    private final BancoRepository bancoRepository;
    private final TarjetaServiceClient tarjetaServiceClient;
    private final TarjetaConsumoServiceClient tarjetaConsumoServiceClient;
    private final MonitoreoFraudeClient monitoreoFraudeClient;
    private final TransaccionMapper transaccionMapper;

    public TransaccionService(TransaccionRepository transaccionRepository,
                            HistorialEstadoTransaccionRepository historialRepository,
                            MonitoreoFraudeService monitoreoFraudeService,
                            ComisionService comisionService,
                            BancoRepository bancoRepository,
                            TarjetaServiceClient tarjetaServiceClient,
                            TarjetaConsumoServiceClient tarjetaConsumoServiceClient,
                            MonitoreoFraudeClient monitoreoFraudeClient,
                            TransaccionMapper transaccionMapper) {
        this.transaccionRepository = transaccionRepository;
        this.historialRepository = historialRepository;
        this.monitoreoFraudeService = monitoreoFraudeService;
        this.comisionService = comisionService;
        this.bancoRepository = bancoRepository;
        this.tarjetaServiceClient = tarjetaServiceClient;
        this.tarjetaConsumoServiceClient = tarjetaConsumoServiceClient;
        this.monitoreoFraudeClient = monitoreoFraudeClient;
        this.transaccionMapper = transaccionMapper;
    }

    @Transactional(readOnly = true)
    public List<TransaccionDTO> obtenerTransaccionesPorEstadoYFecha(String estado, LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        if (fechaInicio == null || fechaFin == null) {
            throw new BusinessException("fechas", ENTITY_NAME, "validar fechas");
        }
        if (fechaInicio.isAfter(fechaFin)) {
            throw new BusinessException("fechas", ENTITY_NAME, "validar fechas");
        }
        return this.transaccionRepository
                .findByEstadoAndFechaCreacionBetweenOrderByFechaCreacionDesc(estado, fechaInicio, fechaFin)
                .stream()
                .map(transaccionMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public TransaccionDTO obtenerTransaccionPorId(Integer id) {
        Transaccion transaccion = this.transaccionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(id.toString(), ENTITY_NAME));
        return transaccionMapper.toDTO(transaccion);
    }

    @Transactional(readOnly = true)
    public List<TransaccionDTO> obtenerTransaccionesPorBancoYMonto(Integer codigoBanco, BigDecimal montoMinimo, BigDecimal montoMaximo) {
        if (montoMinimo != null && montoMaximo != null && montoMinimo.compareTo(montoMaximo) > 0) {
            throw new BusinessException("monto", ENTITY_NAME, "validar monto");
        }
        return this.transaccionRepository
                .findByBancoCodigoAndMontoBetweenOrderByMontoDesc(codigoBanco, montoMinimo, montoMaximo)
                .stream()
                .map(transaccionMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public TransaccionDTO guardarTransaccion(TransaccionDTO transaccionDTO) {
        log.debug("Guardando transacción: {}", transaccionDTO);
        
        if (transaccionRepository.existsByCodigoUnicoTransaccion(transaccionDTO.getCodigoUnicoTransaccion())) {
            throw new BusinessException(transaccionDTO.getCodigoUnicoTransaccion(), ENTITY_NAME, "validar código único");
        }

        Banco banco = bancoRepository.findById(transaccionDTO.getCodigoBanco())
                .orElseThrow(() -> new NotFoundException(transaccionDTO.getCodigoBanco().toString(), ENTITY_NAME));
        
        if (banco.getComision() == null) {
            throw new BusinessException(transaccionDTO.getCodigoBanco().toString(), ENTITY_NAME, "validar comisión");
        }

        if (MODALIDAD_SIMPLE.equals(transaccionDTO.getModalidad())) {
            log.debug("Configurando transacción simple en DTO");
            transaccionDTO.setCuotas(0);
            transaccionDTO.setInteresDiferido(false);
        }

        Transaccion transaccion = transaccionMapper.toModel(transaccionDTO);
        transaccion.setBanco(banco);
        transaccion.setComision(banco.getComision());
        transaccion.setFechaCreacion(LocalDateTime.now());
        
        if (MODALIDAD_SIMPLE.equals(transaccionDTO.getModalidad())) {
            log.debug("Configurando transacción simple");
            transaccion.setModalidad(MODALIDAD_SIMPLE);
            transaccion.setCuotas(0);
            transaccion.setInteresDiferido(false);
            transaccion.setFechaEjecucionRecurrencia(null);
            transaccion.setFechaFinRecurrencia(null);
            asignarComisionSimple(transaccion);
        } else if (MODALIDAD_RECURRENTE.equals(transaccionDTO.getModalidad())) {
            log.debug("Configurando transacción recurrente");
            transaccion.setModalidad(MODALIDAD_RECURRENTE);
            if (transaccion.getCuotas() == null || transaccion.getCuotas() < 1) {
                throw new BusinessException("cuotas", ENTITY_NAME, "validar cuotas para modalidad recurrente");
            }
            asignarComisionRecurrente(transaccion);
        }

        try {
            validarTransaccion(transaccion);
            log.debug("Transacción validada correctamente. Modalidad: {}, Cuotas: {}", transaccion.getModalidad(), transaccion.getCuotas());
        } catch (BusinessException e) {
            log.error("Error en validación: {}", e.getMessage());
            throw e;
        }

        try {
            log.debug("Guardando transacción en BD: modalidad={}, cuotas={}", transaccion.getModalidad(), transaccion.getCuotas());
            Transaccion transaccionGuardada = transaccionRepository.save(transaccion);
            registrarCambioEstado(transaccionGuardada, transaccion.getEstado(), 
                "Transacción registrada - Esperando respuesta del banco");
            
            return transaccionMapper.toDTO(transaccionGuardada);
        } catch (Exception e) {
            log.error("Error al guardar transacción: {}", e.getMessage());
            throw new BusinessException("Error al guardar transacción: " + e.getMessage(), ENTITY_NAME, "guardar transacción");
        }
    }

    @Transactional
    public void procesarConBanco(Integer transaccionId) {
        Transaccion transaccion = transaccionRepository.findById(transaccionId)
                .orElseThrow(() -> new NotFoundException(transaccionId.toString(), ENTITY_NAME));
                
        try {
            ConsumoTarjetaRequestDTO request = prepararConsumoRequest(transaccion);
            try {
                ResponseEntity<RespuestaBancoDTO> respuesta = tarjetaConsumoServiceClient.procesarConsumoTarjeta(request);
                
                if (respuesta.getStatusCodeValue() == 201) {
                    actualizarEstadoTransaccion(transaccion.getCodigo(), ESTADO_APROBADA, 
                        "Transacción aceptada por el banco");
                } else {
                    actualizarEstadoTransaccion(transaccion.getCodigo(), ESTADO_RECHAZADA, 
                        "Transacción rechazada por el banco");
                }
            } catch (Exception e) {
                String mensajeError = obtenerMensajeError(e);
                actualizarEstadoTransaccion(transaccion.getCodigo(), ESTADO_RECHAZADA, mensajeError);
            }
        } catch (Exception e) {
            throw new BusinessException("Error al procesar la transacción: " + e.getMessage(), ENTITY_NAME, "procesar transacción");
        }
    }

    private void asignarComisionSimple(Transaccion transaccion) {
        if (transaccion.getGtwComision() == null || transaccion.getGtwComision().trim().isEmpty()) {
            BigDecimal comision = comisionService.calcularComision(
                transaccion.getComision().getCodigo(),
                1,
                transaccion.getMonto()
            );
            transaccion.setGtwComision(comision.toString());
        }
        
        if (transaccion.getCuotas() == null) {
            transaccion.setCuotas(0);
        }
        if (transaccion.getInteresDiferido() == null) {
            transaccion.setInteresDiferido(false);
        }
        transaccion.setFechaEjecucionRecurrencia(null);
        transaccion.setFechaFinRecurrencia(null);
    }

    private void asignarComisionRecurrente(Transaccion transaccion) {
        if (transaccion.getCuotas() == null || transaccion.getCuotas() <= 0) {
            throw new BusinessException(transaccion.getCuotas().toString(), ENTITY_NAME, "validar cuotas");
        }

        LocalDateTime fechaEjecucion = LocalDateTime.now().plusDays(1);
        LocalDateTime fechaFin = fechaEjecucion.plusMonths(transaccion.getCuotas());

        transaccion.setFechaEjecucionRecurrencia(fechaEjecucion);
        transaccion.setFechaFinRecurrencia(fechaFin);

        if (transaccion.getGtwComision() == null || transaccion.getGtwComision().trim().isEmpty()) {
            BigDecimal comision = comisionService.calcularComision(
                transaccion.getComision().getCodigo(),
                transaccion.getCuotas(),
                transaccion.getMonto()
            );
            transaccion.setGtwComision(comision.toString());
        }
        
        if (transaccion.getCodigoUnicoTransaccion() != null) {
            String codigo = transaccion.getCodigoUnicoTransaccion();
            while (codigo.length() < 32) {
                codigo = "0" + codigo;
            }
            transaccion.setCodigoUnicoTransaccion(codigo);
        }
    }

    @Transactional
    public TransaccionDTO actualizarEstadoTransaccion(Integer id, String nuevoEstado, String detalle) {
        try {
            Transaccion transaccion = obtenerTransaccionPorEntidad(id);
            validarCambioEstado(transaccion.getEstado(), nuevoEstado);
            
            transaccion.setEstado(nuevoEstado);
            transaccion = this.transaccionRepository.save(transaccion);
            registrarCambioEstado(transaccion, nuevoEstado, detalle);
            
            return transaccionMapper.toDTO(transaccion);
        } catch (Exception e) {
            throw new BusinessException(id.toString(), ENTITY_NAME, "actualizar estado");
        }
    }

    private void validarTransaccion(Transaccion transaccion) {
        if (transaccion.getMonto() == null || transaccion.getMonto().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("monto", ENTITY_NAME, "validar monto");
        }
        if (transaccion.getBanco() == null) {
            throw new BusinessException("banco", ENTITY_NAME, "validar banco");
        }
        if (transaccion.getNumeroTarjeta() == null || transaccion.getNumeroTarjeta().trim().isEmpty()) {
            throw new BusinessException("número tarjeta", ENTITY_NAME, "validar tarjeta");
        }
        if (transaccion.getModalidad() == null || 
            (!MODALIDAD_SIMPLE.equals(transaccion.getModalidad()) && 
             !MODALIDAD_RECURRENTE.equals(transaccion.getModalidad()))) {
            throw new BusinessException("modalidad", ENTITY_NAME, "validar modalidad");
        }

        if (MODALIDAD_RECURRENTE.equals(transaccion.getModalidad())) {
            if (transaccion.getCuotas() == null || transaccion.getCuotas() <= 0) {
                throw new BusinessException("cuotas", ENTITY_NAME, "validar cuotas para modalidad recurrente");
            }
        }

        if (transaccion.getPais() == null || transaccion.getPais().trim().length() != 2) {
            throw new BusinessException("país", ENTITY_NAME, "validar país");
        }
        if (transaccion.getGtwComision() == null) {
            throw new BusinessException("comisión", ENTITY_NAME, "validar comisión");
        }
        if (transaccion.getGtwCuenta() == null) {
            throw new BusinessException("cuenta", ENTITY_NAME, "validar cuenta");
        }
        try {
            new BigDecimal(transaccion.getGtwComision());
        } catch (NumberFormatException e) {
            throw new BusinessException("comisión", ENTITY_NAME, "validar formato comisión");
        }
    }

    private void validarCambioEstado(String estadoActual, String nuevoEstado) {
        if (ESTADO_RECHAZADA.equals(estadoActual)) {
            throw new BusinessException(estadoActual, ENTITY_NAME, "validar cambio estado");
        }
        if (ESTADO_APROBADA.equals(estadoActual) && !ESTADO_REVISION.equals(nuevoEstado)) {
            throw new BusinessException(estadoActual, ENTITY_NAME, "validar cambio estado");
        }
        if (!List.of(ESTADO_PENDIENTE, ESTADO_APROBADA, ESTADO_RECHAZADA, ESTADO_REVISION, ESTADO_PROCESADO)
                .contains(nuevoEstado)) {
            throw new BusinessException("Estado no válido", ENTITY_NAME, "validar estado");
        }
    }

    private void registrarCambioEstado(Transaccion transaccion, String estado, String detalle) {
        try {
            transaccion = transaccionRepository.save(transaccion);
            
            HistorialEstadoTransaccion historial = new HistorialEstadoTransaccion();
            historial.setTransaccion(transaccion);
            historial.setEstado(estado);
            historial.setFechaEstadoCambio(LocalDateTime.now());
            historial.setDetalle(detalle);
            this.historialRepository.save(historial);
        } catch (Exception e) {
            throw new BusinessException(e.getMessage(), ENTITY_NAME, "registrar cambio estado");
        }
    }

    private Transaccion obtenerTransaccionPorEntidad(Integer id) {
        return transaccionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(id.toString(), ENTITY_NAME));
    }

    private String obtenerMensajeError(Exception e) {
        String errorMessage = e.getMessage();
        if (errorMessage != null) {
            if (errorMessage.contains("\"mensaje\":\"")) {
                int start = errorMessage.indexOf("\"mensaje\":\"") + 10;
                int end = errorMessage.indexOf("\"", start);
                if (end > start) {
                    return errorMessage.substring(start, end);
                }
            } else if (errorMessage.contains("\"message\":\"")) {
                int start = errorMessage.indexOf("\"message\":\"") + 11;
                int end = errorMessage.indexOf("\"", start);
                if (end > start) {
                    return errorMessage.substring(start, end);
                }
            }
        }
        return "Error al procesar la transacción con el banco";
    }

    private ConsumoTarjetaRequestDTO prepararConsumoRequest(Transaccion transaccion) {
        try {
            if (transaccion.getMonto() == null) {
                throw new BusinessException("monto", ENTITY_NAME, "validar monto");
            }

            ConsumoTarjetaRequestDTO.DetalleComision gtw = new ConsumoTarjetaRequestDTO.DetalleComision();
            gtw.setReferencia("REF-" + transaccion.getCodigoUnicoTransaccion());
            gtw.setComision(new BigDecimal(transaccion.getGtwComision()));
            gtw.setNumeroCuenta(transaccion.getGtwCuenta());

            ConsumoTarjetaRequestDTO.DetalleComision processor = new ConsumoTarjetaRequestDTO.DetalleComision();
            processor.setReferencia("PROC-" + transaccion.getCodigoUnicoTransaccion());
            processor.setComision(new BigDecimal("0.01"));
            processor.setNumeroCuenta("002");

            ConsumoTarjetaRequestDTO.Detalle detalle = new ConsumoTarjetaRequestDTO.Detalle();
            detalle.setGtw(gtw);
            detalle.setProcessor(processor);

            ConsumoTarjetaRequestDTO request = ConsumoTarjetaRequestDTO.builder()
                    .numeroTarjeta(transaccion.getNumeroTarjeta())
                    .cvv(transaccion.getCvv())
                    .fechaCaducidad(transaccion.getFechaExpiracionTarjeta())
                    .valor(transaccion.getMonto())
                    .descripcion("Transacción " + transaccion.getCodigoUnicoTransaccion())
                    .beneficiario(transaccion.getBeneficiario() != null ? 
                        transaccion.getBeneficiario() : "Beneficiario por defecto")
                    .numeroCuenta(transaccion.getNumeroCuenta())
                    .detalle(detalle)
                    .build();

            if (MODALIDAD_SIMPLE.equals(transaccion.getModalidad())) {
                request.setEsDiferido(false);
                request.setCuotas(0);
            } else {
                request.setEsDiferido(transaccion.getInteresDiferido());
                request.setCuotas(transaccion.getCuotas());
            }

            return request;
        } catch (Exception e) {
            throw new BusinessException(e.getMessage(), ENTITY_NAME, "preparar request");
        }
    }

    @Transactional
    public TransaccionDTO procesarRespuestaFraude(String codigoUnicoTransaccion, String decision) {
        Transaccion transaccion = transaccionRepository.findFirstByCodigoUnicoTransaccionOrderByFechaCreacionDesc(codigoUnicoTransaccion)
                .orElseThrow(() -> new NotFoundException(codigoUnicoTransaccion, ENTITY_NAME));
        
        if ("APROBAR".equals(decision)) {
            actualizarEstadoTransaccion(transaccion.getCodigo(), ESTADO_APROBADA, "Aprobado por monitoreo de fraude");
        } else if ("RECHAZAR".equals(decision)) {
            actualizarEstadoTransaccion(transaccion.getCodigo(), ESTADO_RECHAZADA, "Rechazado por monitoreo de fraude");
        } else {
            throw new BusinessException(decision, ENTITY_NAME, "validar decisión fraude");
        }
        
        return transaccionMapper.toDTO(transaccion);
    }
}
