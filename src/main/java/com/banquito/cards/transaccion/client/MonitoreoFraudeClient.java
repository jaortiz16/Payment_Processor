package com.banquito.cards.transaccion.client;

import com.banquito.cards.transaccion.controller.dto.RespuestaMonitoreoFraudeDTO;
import com.banquito.cards.transaccion.controller.dto.ProcesamientoFraudeRequestDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "monitoreoFraudeService", url = "https://payment-processor-nu.vercel.app")
public interface MonitoreoFraudeClient {

    @PostMapping("/transacciones/monitoreo-fraude/procesar")
    RespuestaMonitoreoFraudeDTO procesarTransaccionFraude(@RequestBody ProcesamientoFraudeRequestDTO request);
} 