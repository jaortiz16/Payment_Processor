package com.banquito.cards.transaccion.client;

import com.banquito.cards.transaccion.model.ConsumoTarjetaCompleteRequest;
import com.banquito.cards.transaccion.model.RespuestaBanco;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "tarjetaConsumoService", url = "https://efe3-2803-6320-2a-33f0-6881-87c7-5db-3f37.ngrok-free.app")
public interface TarjetaConsumoServiceClient {

    @PostMapping("/transacciones/consumo-tarjeta")
    ResponseEntity<RespuestaBanco> procesarConsumoTarjeta(@RequestBody ConsumoTarjetaCompleteRequest request);
}