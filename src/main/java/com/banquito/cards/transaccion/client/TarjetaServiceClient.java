package com.banquito.cards.transaccion.client;

import com.banquito.cards.transaccion.controller.dto.ConsumoTarjetaRequestDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "tarjetaService", url = "https://payment-processor-nu.vercel.app")
public interface TarjetaServiceClient {

    @PostMapping("/transacciones/consumo-tarjeta/validar")
    void validarConsumoTarjeta(@RequestBody ConsumoTarjetaRequestDTO request);
} 