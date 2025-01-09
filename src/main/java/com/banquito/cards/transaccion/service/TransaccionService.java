package com.banquito.cards.transaccion.service;

import com.banquito.cards.transaccion.model.Transaccion;
import com.banquito.cards.transaccion.model.HistorialEstadoTransaccion;
import com.banquito.cards.transaccion.model.ConsumoTarjetaRequest;
import com.banquito.cards.transaccion.model.ConsumoTarjetaCompleteRequest;
import com.banquito.cards.transaccion.repository.TransaccionRepository;
import com.banquito.cards.transaccion.repository.HistorialEstadoTransaccionRepository;
import com.banquito.cards.transaccion.client.TarjetaServiceClient;
import com.banquito.cards.fraude.service.MonitoreoFraudeService;
import com.banquito.cards.comision.service.ComisionService;
import com.banquito.cards.comision.model.Banco;
import com.banquito.cards.comision.repository.BancoRepository;
import com.banquito.cards.transaccion.client.TarjetaConsumoServiceClient;
import com.banquito.cards.transaccion.model.RespuestaBanco;
import com.banquito.cards.transaccion.client.MonitoreoFraudeClient;
import com.banquito.cards.transaccion.model.ProcesamientoFraudeRequest;
import com.banquito.cards.transaccion.model.RespuestaMonitoreoFraude;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Propagation;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class TransaccionService {
    
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

    public TransaccionService(TransaccionRepository transaccionRepository,
                            HistorialEstadoTransaccionRepository historialRepository,
                            MonitoreoFraudeService monitoreoFraudeService,
                            ComisionService comisionService,
                            BancoRepository bancoRepository,
                            TarjetaServiceClient tarjetaServiceClient,
                            TarjetaConsumoServiceClient tarjetaConsumoServiceClient,
                            MonitoreoFraudeClient monitoreoFraudeClient) {
        this.transaccionRepository = transaccionRepository;
        this.historialRepository = historialRepository;
        this.monitoreoFraudeService = monitoreoFraudeService;
        this.comisionService = comisionService;
        this.bancoRepository = bancoRepository;
        this.tarjetaServiceClient = tarjetaServiceClient;
        this.tarjetaConsumoServiceClient = tarjetaConsumoServiceClient;
        this.monitoreoFraudeClient = monitoreoFraudeClient;
    }

    @Transactional(readOnly = true)
    public List<Transaccion> obtenerTransaccionesPorEstadoYFecha(String estado, LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        if (fechaInicio == null || fechaFin == null) {
            throw new RuntimeException("Las fechas de inicio y fin son requeridas");
        }
        if (fechaInicio.isAfter(fechaFin)) {
            throw new RuntimeException("La fecha de inicio no puede ser posterior a la fecha fin");
        }
        return this.transaccionRepository.findByEstadoAndFechaCreacionBetween(estado, fechaInicio, fechaFin);
    }

    @Transactional(readOnly = true)
    public Transaccion obtenerTransaccionPorId(Integer id) {
        return this.transaccionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No existe la transacción con id: " + id));
    }

    @Transactional(readOnly = true)
    public List<Transaccion> obtenerTransaccionesPorBancoYMonto(Integer codigoBanco, BigDecimal montoMinimo, BigDecimal montoMaximo) {
        if (montoMinimo != null && montoMaximo != null && montoMinimo.compareTo(montoMaximo) > 0) {
            throw new RuntimeException("El monto mínimo no puede ser mayor al monto máximo");
        }
        return this.transaccionRepository.findByBancoCodigoAndMontoBetween(codigoBanco, montoMinimo, montoMaximo);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Transaccion guardarTransaccion(Transaccion transaccion) {
        // 1. Validaciones iniciales
        if (transaccionRepository.existsByCodigoUnicoTransaccion(transaccion.getCodigoUnicoTransaccion())) {
            throw new RuntimeException("Ya existe una transacción con el código: " + 
                transaccion.getCodigoUnicoTransaccion());
        }
        validarTransaccion(transaccion);
        
        // 2. Obtener y validar el banco y su comisión
        Banco banco = bancoRepository.findById(transaccion.getBanco().getCodigo())
                .orElseThrow(() -> new RuntimeException("No existe el banco con id: " + transaccion.getBanco().getCodigo()));
        if (banco.getComision() == null) {
            throw new RuntimeException("El banco no tiene una comisión asignada");
        }
        transaccion.setBanco(banco);
        transaccion.setComision(banco.getComision());
        
        // 3. Asignar comisión según modalidad
        if (MODALIDAD_RECURRENTE.equals(transaccion.getModalidad())) {
            asignarComisionRecurrente(transaccion);
        } else {
            asignarComisionSimple(transaccion);
        }

        // 4. Guardar la transacción
        Transaccion transaccionGuardada = this.transaccionRepository.save(transaccion);
        registrarCambioEstado(transaccionGuardada, transaccion.getEstado(), "Transacción registrada - Esperando respuesta del banco");
        
        return transaccionGuardada;
    }

    @Transactional
    public void procesarConBanco(Transaccion transaccion) {
        try {
            ConsumoTarjetaCompleteRequest request = prepararConsumoRequest(transaccion);
            try {
                ResponseEntity<RespuestaBanco> respuesta = tarjetaConsumoServiceClient.procesarConsumoTarjeta(request);
                
                if (respuesta.getStatusCodeValue() == 201) {
                    actualizarEstadoTransaccion(transaccion.getCodigo(), ESTADO_APROBADA, 
                        "Transacción aceptada por el banco");
                } else {
                    // Cualquier otra respuesta (incluyendo 400) se trata como rechazo
                    actualizarEstadoTransaccion(transaccion.getCodigo(), ESTADO_RECHAZADA, 
                        "Transacción rechazada por el banco");
                }
            } catch (Exception e) {
                String errorMessage = e.getMessage();
                System.out.println("Error del banco: " + errorMessage);
                
                String mensajeError = "Transacción rechazada por el banco";
                
                // Buscar el mensaje de error en el JSON de respuesta
                if (errorMessage != null) {
                    if (errorMessage.contains("\"mensaje\":\"")) {
                        int start = errorMessage.indexOf("\"mensaje\":\"") + 10;
                        int end = errorMessage.indexOf("\"", start);
                        if (end > start) {
                            mensajeError = errorMessage.substring(start, end);
                        }
                    } else if (errorMessage.contains("\"message\":\"")) {
                        int start = errorMessage.indexOf("\"message\":\"") + 11;
                        int end = errorMessage.indexOf("\"", start);
                        if (end > start) {
                            mensajeError = errorMessage.substring(start, end);
                        }
                    }
                }
                
                actualizarEstadoTransaccion(transaccion.getCodigo(), ESTADO_RECHAZADA, mensajeError);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error al procesar la transacción: " + e.getMessage());
        }
    }

    private void asignarComisionSimple(Transaccion transaccion) {
        BigDecimal comision = comisionService.calcularComision(
            transaccion.getComision().getCodigo(),
            1, // Una sola transacción
            transaccion.getMonto()
        );
    }

    private void asignarComisionRecurrente(Transaccion transaccion) {
        if (transaccion.getCuotas() == null || transaccion.getCuotas() <= 0) {
            throw new RuntimeException("El número de cuotas debe ser mayor a cero");
        }

        // La fecha de ejecución será la fecha actual
        LocalDateTime fechaEjecucion = LocalDateTime.now();
        // La fecha fin será la fecha actual más el número de cuotas en meses
        LocalDateTime fechaFin = fechaEjecucion.plusMonths(transaccion.getCuotas());

        transaccion.setFechaEjecucionRecurrencia(fechaEjecucion);
        transaccion.setFechaFinRecurrencia(fechaFin);

        // Calcular comisión basada en el número de cuotas
        BigDecimal comision = comisionService.calcularComision(
            transaccion.getComision().getCodigo(),
            transaccion.getCuotas(),
            transaccion.getMonto()
        );
    }

    public Transaccion actualizarEstadoTransaccion(Integer id, String nuevoEstado, String detalle) {
        try {
            Transaccion transaccion = obtenerTransaccionPorId(id);
            validarCambioEstado(transaccion.getEstado(), nuevoEstado);
            
            transaccion.setEstado(nuevoEstado);
            transaccion = this.transaccionRepository.save(transaccion);
            registrarCambioEstado(transaccion, nuevoEstado, detalle);
            
            return transaccion;
        } catch (Exception e) {
            throw new RuntimeException("Error al actualizar el estado de la transacción: " + e.getMessage());
        }
    }

    private void validarTransaccion(Transaccion transaccion) {
        if (transaccion.getMonto() == null || transaccion.getMonto().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("El monto debe ser mayor a cero");
        }
        if (transaccion.getBanco() == null) {
            throw new RuntimeException("El banco es requerido");
        }
        if (transaccion.getNumeroTarjeta() == null || transaccion.getNumeroTarjeta().trim().isEmpty()) {
            throw new RuntimeException("El número de tarjeta es requerido");
        }
        if (transaccion.getModalidad() == null || 
            (!MODALIDAD_SIMPLE.equals(transaccion.getModalidad()) && 
             !MODALIDAD_RECURRENTE.equals(transaccion.getModalidad()))) {
            throw new RuntimeException("Modalidad inválida. Use: SIM o REC");
        }
        if (transaccion.getPais() == null || transaccion.getPais().trim().length() != 2) {
            throw new RuntimeException("El código de país debe tener 2 caracteres");
        }
        if (transaccion.getGtwComision() == null) {
            throw new RuntimeException("La comisión del gateway es requerida");
        }
        if (transaccion.getGtwCuenta() == null) {
            throw new RuntimeException("La cuenta del gateway es requerida");
        }
        // Validar que las comisiones sean números válidos
        try {
            new BigDecimal(transaccion.getGtwComision());
        } catch (NumberFormatException e) {
            throw new RuntimeException("La comisión del gateway debe ser un número válido");
        }
    }

    private void validarCambioEstado(String estadoActual, String nuevoEstado) {
        if (ESTADO_RECHAZADA.equals(estadoActual)) {
            throw new RuntimeException("No se puede cambiar el estado de una transacción rechazada");
        }
        if (ESTADO_APROBADA.equals(estadoActual) && !ESTADO_REVISION.equals(nuevoEstado)) {
            throw new RuntimeException("Una transacción aprobada solo puede pasar a revisión");
        }
        if (!List.of(ESTADO_PENDIENTE, ESTADO_APROBADA, ESTADO_RECHAZADA, ESTADO_REVISION, ESTADO_PROCESADO)
                .contains(nuevoEstado)) {
            throw new RuntimeException("Estado no válido");
        }
    }

    private void registrarCambioEstado(Transaccion transaccion, String estado, String detalle) {
        try {
            HistorialEstadoTransaccion historial = new HistorialEstadoTransaccion();
            historial.setTransaccion(transaccion);
            historial.setEstado(estado);
            historial.setFechaEstadoCambio(LocalDateTime.now());
            historial.setDetalle(detalle);
            this.historialRepository.save(historial);
        } catch (Exception e) {
            throw new RuntimeException("Error al registrar el cambio de estado: " + e.getMessage());
        }
    }

    @Transactional
    public Transaccion procesarRespuestaFraude(String codigoUnicoTransaccion, String decision) {
        try {
            Transaccion transaccion = transaccionRepository
                    .findFirstByCodigoUnicoTransaccionOrderByFechaCreacionDesc(codigoUnicoTransaccion)
                    .orElseThrow(() -> new RuntimeException("Transacción no encontrada"));

            if (!"REV".equals(transaccion.getEstado())) {
                throw new RuntimeException("La transacción no está en estado de revisión");
            }

            ProcesamientoFraudeRequest request = new ProcesamientoFraudeRequest();
            request.setCodigoUnicoTransaccion(codigoUnicoTransaccion);
            request.setDecision(decision);

            RespuestaMonitoreoFraude respuesta = monitoreoFraudeClient.procesarTransaccionFraude(request);

            if (respuesta.isSuccess()) {
                // Actualizar estado en monitoreo de fraude
                monitoreoFraudeService.actualizarEstadoMonitoreoFraude(codigoUnicoTransaccion, ESTADO_PROCESADO);

                // Primero actualizamos el estado en monitoreo de fraude a PROCESADO
                transaccion.setEstado(ESTADO_PROCESADO);
                Transaccion transaccionProcesada = transaccionRepository.save(transaccion);
                registrarCambioEstado(transaccionProcesada, ESTADO_PROCESADO, 
                    "Transacción procesada en monitoreo de fraude");

                if ("RECHAZADO".equals(respuesta.getEstado())) {
                    // Si es rechazado como fraude, volver a procesar como transacción normal
                    transaccion.setEstado(ESTADO_PENDIENTE);
                    Transaccion transaccionActualizada = transaccionRepository.save(transaccion);
                    registrarCambioEstado(transaccionActualizada, ESTADO_PENDIENTE, 
                        "Posible fraude rechazado - Procesando normal");
                    
                    // Preparar la solicitud para el consumo normal
                    ConsumoTarjetaCompleteRequest consumoRequest = prepararConsumoRequest(transaccion);
                    
                    try {
                        ResponseEntity<RespuestaBanco> respuestaEntity = tarjetaConsumoServiceClient.procesarConsumoTarjeta(consumoRequest);
                        RespuestaBanco respuestaBanco = respuestaEntity.getBody();
                        
                        if (respuestaBanco == null) {
                            throw new RuntimeException("Respuesta vacía del banco");
                        }
                        
                        if (respuestaBanco.isSuccess()) {
                            return actualizarEstadoTransaccion(transaccionActualizada.getCodigo(), ESTADO_APROBADA, 
                                "Transacción aprobada por el banco");
                        } else {
                            return actualizarEstadoTransaccion(transaccionActualizada.getCodigo(), ESTADO_RECHAZADA, 
                                respuestaBanco.getMessage());
                        }
                    } catch (Exception e) {
                        throw new RuntimeException("Error al procesar el consumo: " + e.getMessage());
                    }
                } else {
                    // Si es aceptado como fraude, marcar como rechazada
                    transaccion.setEstado(ESTADO_RECHAZADA);
                    Transaccion transaccionActualizada = transaccionRepository.save(transaccion);
                    registrarCambioEstado(transaccionActualizada, ESTADO_RECHAZADA, 
                        "Confirmado como fraude - Transacción rechazada");
                    return transaccionActualizada;
                }
            } else {
                throw new RuntimeException("Error al procesar la respuesta de fraude: " + respuesta.getMessage());
            }
        } catch (Exception e) {
            throw new RuntimeException("Error al procesar la respuesta de fraude: " + e.getMessage());
        }
    }

    private ConsumoTarjetaCompleteRequest prepararConsumoRequest(Transaccion transaccion) {
        try {
            if (transaccion.getMonto() == null) {
                throw new RuntimeException("El monto no puede ser nulo");
            }

            ConsumoTarjetaCompleteRequest request = new ConsumoTarjetaCompleteRequest();
            request.setNumeroTarjeta(transaccion.getNumeroTarjeta());
            request.setCvv(transaccion.getCvv());
            request.setFechaCaducidad(transaccion.getFechaExpiracionTarjeta());
            request.setValor(transaccion.getMonto());
            request.setDescripcion("Transacción " + transaccion.getCodigoUnicoTransaccion());
            request.setNumeroCuenta(transaccion.getNumeroCuenta());
            request.setEsDiferido("REC".equals(transaccion.getModalidad()));
            request.setCuotas(transaccion.getCuotas() != null ? transaccion.getCuotas() : 1);
            request.setInteresDiferido(transaccion.getInteresDiferido() != null ? transaccion.getInteresDiferido() : false);
            request.setBeneficiario(transaccion.getBeneficiario() != null ? 
                transaccion.getBeneficiario() : "Beneficiario por defecto");

            ConsumoTarjetaCompleteRequest.DetalleComisiones detalle = new ConsumoTarjetaCompleteRequest.DetalleComisiones();
            
            ConsumoTarjetaCompleteRequest.ComisionDetalle gtw = new ConsumoTarjetaCompleteRequest.ComisionDetalle();
            gtw.setComision(new BigDecimal(transaccion.getGtwComision()));
            gtw.setNumeroCuenta(transaccion.getGtwCuenta());
            detalle.setGtw(gtw);

            ConsumoTarjetaCompleteRequest.ComisionDetalle processor = new ConsumoTarjetaCompleteRequest.ComisionDetalle();
            processor.setComision(new BigDecimal("0.10"));
            processor.setNumeroCuenta("00000002");
            detalle.setProcessor(processor);

            ConsumoTarjetaCompleteRequest.ComisionDetalle marca = new ConsumoTarjetaCompleteRequest.ComisionDetalle();
            marca.setComision(new BigDecimal("0.10"));
            marca.setNumeroCuenta("00000002");
            detalle.setMarca(marca);

            request.setDetalle(detalle);
            return request;
        } catch (Exception e) {
            throw new RuntimeException("Error al preparar el request: " + e.getMessage(), e);
        }
    }
}
