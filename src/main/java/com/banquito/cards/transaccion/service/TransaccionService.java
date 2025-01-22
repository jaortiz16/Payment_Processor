package com.banquito.cards.transaccion.service;

import com.banquito.cards.transaccion.model.*;
import com.banquito.cards.transaccion.repository.TransaccionRepository;
import com.banquito.cards.transaccion.repository.HistorialEstadoTransaccionRepository;
import com.banquito.cards.transaccion.controller.dto.TransaccionDTO;
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
            throw new BusinessException("Las fechas de inicio y fin son requeridas");
        }
        if (fechaInicio.isAfter(fechaFin)) {
            throw new BusinessException("La fecha de inicio no puede ser posterior a la fecha fin");
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
            throw new BusinessException("El monto mínimo no puede ser mayor al monto máximo");
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
        
        // 1. Validar código único
        if (transaccionRepository.existsByCodigoUnicoTransaccion(transaccionDTO.getCodigoUnicoTransaccion())) {
            throw new BusinessException("Ya existe una transacción con el código: " + 
                transaccionDTO.getCodigoUnicoTransaccion());
        }

        // 2. Obtener y validar el banco y su comisión
        Banco banco = bancoRepository.findById(transaccionDTO.getCodigoBanco())
                .orElseThrow(() -> new NotFoundException(transaccionDTO.getCodigoBanco().toString(), "Banco"));
        
        if (banco.getComision() == null) {
            throw new BusinessException("El banco no tiene una comisión asignada");
        }

        // 3. Crear y configurar la transacción
        Transaccion transaccion = transaccionMapper.toModel(transaccionDTO);
        transaccion.setBanco(banco);
        transaccion.setComision(banco.getComision());
        
        // 4. Asignar comisión según modalidad
        if (MODALIDAD_RECURRENTE.equals(transaccion.getModalidad())) {
            asignarComisionRecurrente(transaccion);
        } else {
            asignarComisionSimple(transaccion);
        }

        // 5. Guardar y registrar estado
        Transaccion transaccionGuardada = transaccionRepository.save(transaccion);
        registrarCambioEstado(transaccionGuardada, transaccion.getEstado(), 
            "Transacción registrada - Esperando respuesta del banco");
        
        return transaccionMapper.toDTO(transaccionGuardada);
    }

    @Transactional
    public void procesarConBanco(Integer transaccionId) {
        Transaccion transaccion = transaccionRepository.findById(transaccionId)
                .orElseThrow(() -> new NotFoundException(transaccionId.toString(), ENTITY_NAME));
                
        try {
            ConsumoTarjetaCompleteRequest request = prepararConsumoRequest(transaccion);
            try {
                ResponseEntity<RespuestaBanco> respuesta = tarjetaConsumoServiceClient.procesarConsumoTarjeta(request);
                
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
            throw new BusinessException("Error al procesar la transacción: " + e.getMessage());
        }
    }

    private void asignarComisionSimple(Transaccion transaccion) {
        BigDecimal comision = comisionService.calcularComision(
            transaccion.getComision().getCodigo(),
            1,
            transaccion.getMonto()
        );
        transaccion.setGtwComision(comision.toString());
    }

    private void asignarComisionRecurrente(Transaccion transaccion) {
        if (transaccion.getCuotas() == null || transaccion.getCuotas() <= 0) {
            throw new BusinessException("El número de cuotas debe ser mayor a cero");
        }

        // Aseguramos que la fecha de ejecución sea futura
        LocalDateTime fechaEjecucion = LocalDateTime.now().plusDays(1);
        LocalDateTime fechaFin = fechaEjecucion.plusMonths(transaccion.getCuotas());

        transaccion.setFechaEjecucionRecurrencia(fechaEjecucion);
        transaccion.setFechaFinRecurrencia(fechaFin);

        BigDecimal comision = comisionService.calcularComision(
            transaccion.getComision().getCodigo(),
            transaccion.getCuotas(),
            transaccion.getMonto()
        );
        transaccion.setGtwComision(comision.toString());
        
        // Aseguramos que el código único de transacción tenga el largo correcto
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
            throw new BusinessException("Error al actualizar el estado de la transacción: " + e.getMessage());
        }
    }

    private void validarTransaccion(Transaccion transaccion) {
        if (transaccion.getMonto() == null || transaccion.getMonto().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("El monto debe ser mayor a cero");
        }
        if (transaccion.getBanco() == null) {
            throw new BusinessException("El banco es requerido");
        }
        if (transaccion.getNumeroTarjeta() == null || transaccion.getNumeroTarjeta().trim().isEmpty()) {
            throw new BusinessException("El número de tarjeta es requerido");
        }
        if (transaccion.getModalidad() == null || 
            (!MODALIDAD_SIMPLE.equals(transaccion.getModalidad()) && 
             !MODALIDAD_RECURRENTE.equals(transaccion.getModalidad()))) {
            throw new BusinessException("Modalidad inválida. Use: SIM o REC");
        }
        if (transaccion.getPais() == null || transaccion.getPais().trim().length() != 2) {
            throw new BusinessException("El código de país debe tener 2 caracteres");
        }
        if (transaccion.getGtwComision() == null) {
            throw new BusinessException("La comisión del gateway es requerida");
        }
        if (transaccion.getGtwCuenta() == null) {
            throw new BusinessException("La cuenta del gateway es requerida");
        }
        try {
            new BigDecimal(transaccion.getGtwComision());
        } catch (NumberFormatException e) {
            throw new BusinessException("La comisión del gateway debe ser un número válido");
        }
    }

    private void validarCambioEstado(String estadoActual, String nuevoEstado) {
        if (ESTADO_RECHAZADA.equals(estadoActual)) {
            throw new BusinessException("No se puede cambiar el estado de una transacción rechazada");
        }
        if (ESTADO_APROBADA.equals(estadoActual) && !ESTADO_REVISION.equals(nuevoEstado)) {
            throw new BusinessException("Una transacción aprobada solo puede pasar a revisión");
        }
        if (!List.of(ESTADO_PENDIENTE, ESTADO_APROBADA, ESTADO_RECHAZADA, ESTADO_REVISION, ESTADO_PROCESADO)
                .contains(nuevoEstado)) {
            throw new BusinessException("Estado no válido");
        }
    }

    private void registrarCambioEstado(Transaccion transaccion, String estado, String detalle) {
        try {
            // Primero guardamos la transacción para asegurar que tenga un ID
            transaccion = transaccionRepository.save(transaccion);
            
            HistorialEstadoTransaccion historial = new HistorialEstadoTransaccion();
            historial.setTransaccion(transaccion);
            historial.setEstado(estado);
            historial.setFechaEstadoCambio(LocalDateTime.now());
            historial.setDetalle(detalle);
            this.historialRepository.save(historial);
        } catch (Exception e) {
            throw new BusinessException("Error al registrar el cambio de estado: " + e.getMessage());
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

    private ConsumoTarjetaCompleteRequest prepararConsumoRequest(Transaccion transaccion) {
        try {
            if (transaccion.getMonto() == null) {
                throw new BusinessException("El monto no puede ser nulo");
            }

            return ConsumoTarjetaCompleteRequest.builder()
                    .numeroTarjeta(transaccion.getNumeroTarjeta())
                    .cvv(transaccion.getCvv())
                    .fechaCaducidad(transaccion.getFechaExpiracionTarjeta())
                    .valor(transaccion.getMonto())
                    .descripcion("Transacción " + transaccion.getCodigoUnicoTransaccion())
                    .numeroCuenta(transaccion.getNumeroCuenta())
                    .esDiferido("REC".equals(transaccion.getModalidad()))
                    .cuotas(transaccion.getCuotas() != null ? transaccion.getCuotas() : 1)
                    .interesDiferido(transaccion.getInteresDiferido() != null ? transaccion.getInteresDiferido() : false)
                    .beneficiario(transaccion.getBeneficiario() != null ? 
                        transaccion.getBeneficiario() : "Beneficiario por defecto")
                    .detalle(prepararDetalleComisiones(transaccion))
                    .build();
        } catch (Exception e) {
            throw new BusinessException("Error al preparar el request: " + e.getMessage());
        }
    }

    private ConsumoTarjetaCompleteRequest.DetalleComisiones prepararDetalleComisiones(Transaccion transaccion) {
        ConsumoTarjetaCompleteRequest.ComisionDetalle gtw = ConsumoTarjetaCompleteRequest.ComisionDetalle.builder()
                .comision(new BigDecimal(transaccion.getGtwComision()))
                .numeroCuenta(transaccion.getGtwCuenta())
                .build();

        ConsumoTarjetaCompleteRequest.ComisionDetalle processor = ConsumoTarjetaCompleteRequest.ComisionDetalle.builder()
                .comision(new BigDecimal("0.10"))
                .numeroCuenta("00000002")
                .build();

        ConsumoTarjetaCompleteRequest.ComisionDetalle marca = ConsumoTarjetaCompleteRequest.ComisionDetalle.builder()
                .comision(new BigDecimal("0.10"))
                .numeroCuenta("00000002")
                .build();

        return ConsumoTarjetaCompleteRequest.DetalleComisiones.builder()
                .gtw(gtw)
                .processor(processor)
                .marca(marca)
                .build();
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
            throw new BusinessException("Decisión de fraude inválida. Use: APROBAR o RECHAZAR");
        }
        
        return transaccionMapper.toDTO(transaccion);
    }
}
