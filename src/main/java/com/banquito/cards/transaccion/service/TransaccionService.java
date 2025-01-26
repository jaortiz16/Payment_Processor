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
        log.info("Buscando transacciones con estado: {}, entre fechas: {} y {}", estado, fechaInicio, fechaFin);

        if (fechaInicio == null || fechaFin == null) {
            log.error("Fechas de inicio y fin no proporcionadas");
            throw new BusinessException("Las fechas de inicio y fin son requeridas");
        }
        if (fechaInicio.isAfter(fechaFin)) {
            log.error("La fecha de inicio {} es posterior a la fecha fin {}", fechaInicio, fechaFin);
            throw new BusinessException("La fecha de inicio no puede ser posterior a la fecha fin");
        }

        List<TransaccionDTO> transacciones = this.transaccionRepository
                .findByEstadoAndFechaCreacionBetweenOrderByFechaCreacionDesc(estado, fechaInicio, fechaFin)
                .stream()
                .map(transaccionMapper::toDTO)
                .collect(Collectors.toList());

        log.info("Se encontraron {} transacciones para el estado {} entre las fechas dadas", transacciones.size(), estado);
        return transacciones;
    }

    @Transactional(readOnly = true)
    public TransaccionDTO obtenerTransaccionPorId(Integer id) {
        log.info("Buscando transacción con ID: {}", id);
        Transaccion transaccion = this.transaccionRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("No se encontró la transacción con ID: {}", id);
                    return new NotFoundException(id.toString(), ENTITY_NAME);
                });
        log.info("Transacción encontrada exitosamente: {}", transaccion);
        return transaccionMapper.toDTO(transaccion);
    }

    @Transactional(readOnly = true)
    public List<TransaccionDTO> obtenerTransaccionesPorBancoYMonto(Integer codigoBanco, BigDecimal montoMinimo, BigDecimal montoMaximo) {
        log.info("Buscando transacciones para banco: {}, monto mínimo: {}, monto máximo: {}", 
                 codigoBanco, montoMinimo, montoMaximo);

        if (montoMinimo != null && montoMaximo != null && montoMinimo.compareTo(montoMaximo) > 0) {
            log.error("Monto mínimo {} es mayor que el monto máximo {}", montoMinimo, montoMaximo);
            throw new BusinessException("El monto mínimo no puede ser mayor al monto máximo");
        }

        List<TransaccionDTO> transacciones = this.transaccionRepository
                .findByBancoCodigoAndMontoBetweenOrderByMontoDesc(codigoBanco, montoMinimo, montoMaximo)
                .stream()
                .map(transaccionMapper::toDTO)
                .collect(Collectors.toList());

        log.info("Se encontraron {} transacciones para el banco {} en el rango de montos especificado", 
                 transacciones.size(), codigoBanco);
        return transacciones;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public TransaccionDTO guardarTransaccion(TransaccionDTO transaccionDTO) {
        log.info("Iniciando proceso de guardado de transacción: {}", transaccionDTO);

        try {
            if (transaccionRepository.existsByCodigoUnicoTransaccion(transaccionDTO.getCodigoUnicoTransaccion())) {
                log.error("Ya existe una transacción con el código único: {}", transaccionDTO.getCodigoUnicoTransaccion());
                throw new BusinessException("Ya existe una transacción con el código: " + transaccionDTO.getCodigoUnicoTransaccion());
            }

            Banco banco = bancoRepository.findById(transaccionDTO.getCodigoBanco())
                    .orElseThrow(() -> {
                        log.error("No se encontró el banco con código: {}", transaccionDTO.getCodigoBanco());
                        return new NotFoundException(transaccionDTO.getCodigoBanco().toString(), "Banco");
                    });

            if (banco.getComision() == null) {
                log.error("El banco {} no tiene una comisión asignada", banco.getCodigo());
                throw new BusinessException("El banco no tiene una comisión asignada");
            }

            Transaccion transaccion = transaccionMapper.toModel(transaccionDTO);
            transaccion.setBanco(banco);
            transaccion.setComision(banco.getComision());

            log.info("Procesando comisiones para la transacción");
            if (MODALIDAD_RECURRENTE.equals(transaccion.getModalidad())) {
                asignarComisionRecurrente(transaccion);
            } else {
                asignarComisionSimple(transaccion);
            }

            Transaccion transaccionGuardada = transaccionRepository.save(transaccion);
            log.info("Transacción guardada exitosamente con ID: {}", transaccionGuardada.getCodigo());
            
            registrarCambioEstado(transaccionGuardada, transaccion.getEstado(), "Transacción registrada - Esperando respuesta del banco");
            return transaccionMapper.toDTO(transaccionGuardada);

        } catch (Exception e) {
            log.error("Error al guardar la transacción: {}, error: {}", transaccionDTO, e.getMessage());
            throw new BusinessException("Error al guardar la transacción: " + e.getMessage());
        }
    }

    @Transactional
    public void procesarConBanco(Integer transaccionId) {
        log.info("Iniciando procesamiento con banco para transacción ID: {}", transaccionId);
        
        try {
            Transaccion transaccion = transaccionRepository.findById(transaccionId)
                    .orElseThrow(() -> {
                        log.error("No se encontró la transacción con ID: {}", transaccionId);
                        return new NotFoundException(transaccionId.toString(), ENTITY_NAME);
                    });

            ConsumoTarjetaCompleteRequest request = prepararConsumoRequest(transaccion);
            log.info("Request preparado para procesar con banco: {}", request);
            
            ResponseEntity<RespuestaBanco> respuesta = tarjetaConsumoServiceClient.procesarConsumoTarjeta(request);

            if (respuesta.getStatusCodeValue() == 201) {
                log.info("Transacción {} aceptada por el banco", transaccionId);
                actualizarEstadoTransaccion(transaccion.getCodigo(), ESTADO_APROBADA, "Transacción aceptada por el banco");
            } else {
                log.warn("Transacción {} rechazada por el banco", transaccionId);
                actualizarEstadoTransaccion(transaccion.getCodigo(), ESTADO_RECHAZADA, "Transacción rechazada por el banco");
            }
        } catch (Exception e) {
            log.error("Error al procesar transacción {} con el banco: {}", transaccionId, e.getMessage());
            throw new BusinessException("Error al procesar la transacción: " + e.getMessage());
        }
    }

    private void asignarComisionSimple(Transaccion transaccion) {
        log.info("Calculando comisión simple para transacción: {}", transaccion.getCodigo());
        try {
            BigDecimal comision = comisionService.calcularComision(
                    transaccion.getComision().getCodigo(),
                    1,
                    transaccion.getMonto()
            );
            transaccion.setGtwComision(comision.toString());
            log.info("Comisión simple calculada exitosamente: {}", comision);
        } catch (Exception e) {
            log.error("Error al calcular comisión simple para transacción {}: {}", 
                      transaccion.getCodigo(), e.getMessage());
            throw new BusinessException("Error al calcular la comisión: " + e.getMessage());
        }
    }

    private void asignarComisionRecurrente(Transaccion transaccion) {
        log.info("Iniciando cálculo de comisión recurrente para transacción: {}", transaccion.getCodigo());

        try {
            if (transaccion.getCuotas() == null || transaccion.getCuotas() <= 0) {
                log.error("Número de cuotas inválido para transacción {}: {}", 
                          transaccion.getCodigo(), transaccion.getCuotas());
                throw new BusinessException("El número de cuotas debe ser mayor a cero");
            }

            LocalDateTime fechaEjecucion = LocalDateTime.now().plusDays(1);
            LocalDateTime fechaFin = fechaEjecucion.plusMonths(transaccion.getCuotas());
            
            log.debug("Configurando fechas para transacción recurrente - Inicio: {}, Fin: {}", 
                      fechaEjecucion, fechaFin);

            transaccion.setFechaEjecucionRecurrencia(fechaEjecucion);
            transaccion.setFechaFinRecurrencia(fechaFin);

            BigDecimal comision = comisionService.calcularComision(
                transaccion.getComision().getCodigo(),
                transaccion.getCuotas(),
                transaccion.getMonto()
            );
            transaccion.setGtwComision(comision.toString());

            log.info("Comisión recurrente calculada exitosamente: {} para transacción: {}", 
                     comision, transaccion.getCodigo());

            if (transaccion.getCodigoUnicoTransaccion() != null) {
                String codigo = transaccion.getCodigoUnicoTransaccion();
                while (codigo.length() < 32) {
                    codigo = "0" + codigo;
                }
                log.debug("Código único ajustado para transacción {}: {}", 
                          transaccion.getCodigo(), codigo);
            }
        } catch (Exception e) {
            log.error("Error al calcular comisión recurrente para transacción {}: {}", 
                      transaccion.getCodigo(), e.getMessage());
            throw new BusinessException("Error al calcular la comisión recurrente: " + e.getMessage());
        }
    }

    @Transactional
    public TransaccionDTO actualizarEstadoTransaccion(Integer id, String nuevoEstado, String detalle) {
        log.info("Iniciando actualización de estado para transacción con ID: {}, nuevoEstado: {}, detalle: {}", id, nuevoEstado, detalle);

        try {
            Transaccion transaccion = obtenerTransaccionPorEntidad(id);
            log.debug("Transacción encontrada: {}", transaccion);

            validarCambioEstado(transaccion.getEstado(), nuevoEstado);
            log.debug("Transición de estado válida: {} -> {}", transaccion.getEstado(), nuevoEstado);

            transaccion.setEstado(nuevoEstado);
            transaccion = this.transaccionRepository.save(transaccion);
            log.debug("Estado actualizado en la base de datos para transacción con ID: {}", id);

            registrarCambioEstado(transaccion, nuevoEstado, detalle);
            log.info("Cambio de estado registrado exitosamente para transacción con ID: {}", id);
            
            return transaccionMapper.toDTO(transaccion);

        } catch (Exception e) {
            log.error("Error al actualizar el estado de la transacción con ID: {}, error: {}", id, e.getMessage(), e);
            throw new BusinessException("Error al actualizar el estado de la transacción: " + e.getMessage());
        }
    }

    private void validarTransaccion(Transaccion transaccion) {
        log.info("Iniciando validación de transacción: {}", transaccion.getCodigo());
        
        try {
            if (transaccion.getMonto() == null || transaccion.getMonto().compareTo(BigDecimal.ZERO) <= 0) {
                log.error("Monto inválido para transacción {}: {}", 
                          transaccion.getCodigo(), transaccion.getMonto());
                throw new BusinessException("El monto debe ser mayor a cero");
            }
            if (transaccion.getBanco() == null) {
                log.error("Banco no especificado para transacción: {}", transaccion.getCodigo());
                throw new BusinessException("El banco es requerido");
            }
            if (transaccion.getNumeroTarjeta() == null || transaccion.getNumeroTarjeta().trim().isEmpty()) {
                log.error("Número de tarjeta no especificado para transacción: {}", 
                          transaccion.getCodigo());
                throw new BusinessException("El número de tarjeta es requerido");
            }
            if (transaccion.getModalidad() == null || 
                (!MODALIDAD_SIMPLE.equals(transaccion.getModalidad()) && 
                 !MODALIDAD_RECURRENTE.equals(transaccion.getModalidad()))) {
                log.error("Modalidad inválida para transacción {}: {}", 
                          transaccion.getCodigo(), transaccion.getModalidad());
                throw new BusinessException("Modalidad inválida. Use: SIM o REC");
            }
            if (transaccion.getPais() == null || transaccion.getPais().trim().length() != 2) {
                log.error("Código de país inválido para transacción {}: {}", 
                          transaccion.getCodigo(), transaccion.getPais());
                throw new BusinessException("El código de país debe tener 2 caracteres");
            }
            if (transaccion.getGtwComision() == null) {
                log.error("Comisión de gateway no especificada para transacción: {}", 
                          transaccion.getCodigo());
                throw new BusinessException("La comisión del gateway es requerida");
            }
            if (transaccion.getGtwCuenta() == null) {
                log.error("Cuenta de gateway no especificada para transacción: {}", 
                          transaccion.getCodigo());
                throw new BusinessException("La cuenta del gateway es requerida");
            }
            try {
                new BigDecimal(transaccion.getGtwComision());
            } catch (NumberFormatException e) {
                log.error("Comisión de gateway inválida para transacción {}: {}", 
                          transaccion.getCodigo(), transaccion.getGtwComision());
                throw new BusinessException("La comisión del gateway debe ser un número válido");
            }
            
            log.info("Validación de transacción {} completada exitosamente", transaccion.getCodigo());
        } catch (BusinessException e) {
            log.error("Error en validación de transacción {}: {}", 
                      transaccion.getCodigo(), e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error inesperado al validar transacción {}: {}", 
                      transaccion.getCodigo(), e.getMessage());
            throw new BusinessException("Error al validar la transacción: " + e.getMessage());
        }
    }

    private void validarCambioEstado(String estadoActual, String nuevoEstado) {
        log.info("Validando cambio de estado: {} -> {}", estadoActual, nuevoEstado);
        
        try {
            if (ESTADO_RECHAZADA.equals(estadoActual)) {
                log.error("Intento de cambiar estado de una transacción rechazada");
                throw new BusinessException("No se puede cambiar el estado de una transacción rechazada");
            }
            if (ESTADO_APROBADA.equals(estadoActual) && !ESTADO_REVISION.equals(nuevoEstado)) {
                log.error("Intento de cambiar estado de transacción aprobada a estado no permitido: {}", 
                          nuevoEstado);
                throw new BusinessException("Una transacción aprobada solo puede pasar a revisión");
            }
            if (!List.of(ESTADO_PENDIENTE, ESTADO_APROBADA, ESTADO_RECHAZADA, ESTADO_REVISION, ESTADO_PROCESADO)
                    .contains(nuevoEstado)) {
                log.error("Estado no válido especificado: {}", nuevoEstado);
                throw new BusinessException("Estado no válido");
            }
            
            log.info("Validación de cambio de estado completada exitosamente");
        } catch (BusinessException e) {
            log.error("Error en validación de cambio de estado: {}", e.getMessage());
            throw e;
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
