package com.banquito.cards.comision.controller;

import com.banquito.cards.comision.model.Banco;
import com.banquito.cards.comision.service.BancoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/bancos")
public class BancoController {

    private final BancoService bancoService;

    public BancoController(BancoService bancoService) {
        this.bancoService = bancoService;
    }

    @GetMapping("/bancos-activos")
    public ResponseEntity<List<Banco>> listarBancosActivos() {
        try {
            return ResponseEntity.ok(bancoService.obtenerBancosActivos());
        } catch (RuntimeException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/buscar-por-razon-social")
    public ResponseEntity<List<Banco>> buscarPorRazonSocialYEstado(
            @RequestParam String razonSocial,
            @RequestParam(required = false, defaultValue = "ACT") String estado) {
        try {
            return ResponseEntity.ok(bancoService.obtenerBancosPorRazonSocialYEstado(razonSocial, estado));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/buscar-nombre")
    public ResponseEntity<List<Banco>> buscarPorNombreYEstado(
            @RequestParam String nombreComercial,
            @RequestParam(required = false, defaultValue = "ACT") String estado) {
        try {
            return ResponseEntity.ok(bancoService.obtenerBancosPorNombreYEstado(nombreComercial, estado));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Banco> obtenerPorId(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(bancoService.obtenerBancoPorId(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/ruc/{ruc}")
    public ResponseEntity<Banco> obtenerPorRuc(@PathVariable String ruc) {
        try {
            return ResponseEntity.ok(bancoService.obtenerBancoPorRuc(ruc));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/agregar-banco")
    public ResponseEntity<Banco> agregarBanco(@RequestBody Banco banco) {
        try {
            return ResponseEntity.ok(bancoService.agregarBanco(banco));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/actualizar-banco/{id}")
    public ResponseEntity<Banco> actualizarBanco(@PathVariable Integer id, @RequestBody Banco banco) {
        try {
            return ResponseEntity.ok(bancoService.actualizarBanco(id, banco));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/inactivar-banco/{id}")
    public ResponseEntity<Void> inactivarBanco(@PathVariable Integer id) {
        try {
            bancoService.inactivarBanco(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}