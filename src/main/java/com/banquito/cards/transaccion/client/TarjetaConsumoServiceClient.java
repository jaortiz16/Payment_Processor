package com.banquito.cards.transaccion.client;

import com.banquito.cards.transaccion.controller.dto.ConsumoTarjetaRequestDTO;
import com.banquito.cards.transaccion.controller.dto.RespuestaBancoDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "tarjetaConsumoService", url = "http://3.129.23.35/")
public interface TarjetaConsumoServiceClient {

    @PostMapping("/v1/transacciones")
    ResponseEntity<RespuestaBancoDTO> procesarConsumoTarjeta(@RequestBody ConsumoTarjetaRequestDTO request);
}
