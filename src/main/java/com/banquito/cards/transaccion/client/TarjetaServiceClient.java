package com.banquito.cards.transaccion.client;

import com.banquito.cards.transaccion.model.ConsumoTarjetaRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "tarjetaService", url = "http://localhost:3000")
public interface TarjetaServiceClient {

    @PostMapping("/transacciones/consumo-tarjeta/validar")
    void validarConsumoTarjeta(@RequestBody ConsumoTarjetaRequest request);
} 