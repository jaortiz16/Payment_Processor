package com.banquito.cards.transaccion.client;

import com.banquito.cards.transaccion.controller.dto.ConsumoTarjetaCompleteRequestDTO;
import com.banquito.cards.transaccion.controller.dto.RespuestaBancoDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "tarjetaConsumoService", url = "http://3.21.100.241")
public interface TarjetaConsumoServiceClient {

    @PostMapping("/transacciones/consumo-tarjeta")
    ResponseEntity<RespuestaBancoDTO> procesarConsumoTarjeta(@RequestBody ConsumoTarjetaCompleteRequestDTO request);
}
