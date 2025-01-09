package com.banquito.cards.transaccion.client;

import com.banquito.cards.transaccion.model.RespuestaMonitoreoFraude;
import com.banquito.cards.transaccion.model.ProcesamientoFraudeRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "monitoreoFraudeService", url = "http://localhost:3000")
public interface MonitoreoFraudeClient {

    @PostMapping("/transacciones/monitoreo-fraude/procesar")
    RespuestaMonitoreoFraude procesarTransaccionFraude(@RequestBody ProcesamientoFraudeRequest request);
} 