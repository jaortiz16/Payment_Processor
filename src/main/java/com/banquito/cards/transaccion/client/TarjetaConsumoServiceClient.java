package com.banquito.cards.transaccion.client;

import com.banquito.cards.transaccion.model.ConsumoTarjetaCompleteRequest;
import com.banquito.cards.transaccion.model.RespuestaBanco;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "tarjetaConsumoService", url = "https://2f3a-2803-6320-2a-33f0-281e-df89-dad4-ff52.ngrok-free.app")
public interface TarjetaConsumoServiceClient {

    @PostMapping("/transacciones/consumo-tarjeta")
    ResponseEntity<RespuestaBanco> procesarConsumoTarjeta(@RequestBody ConsumoTarjetaCompleteRequest request);
}
