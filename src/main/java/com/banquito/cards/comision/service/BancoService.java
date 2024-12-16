package com.banquito.cards.comision.service;

import com.banquito.cards.comision.model.Banco;
import com.banquito.cards.comision.repository.BancoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;

@Service
public class BancoService {

    private final BancoRepository bancoRepository;

    public BancoService(BancoRepository bancoRepository) {
        this.bancoRepository = bancoRepository;
    }

    public List<Banco> obtenerTodos() {
        return this.bancoRepository.findAll();
    }

    public Optional<Banco> obtenerPorId(Integer id) {
        return this.bancoRepository.findById(id);
    }

    @Transactional
    public Banco crear(Banco banco) {
        banco.setFechaCreacion(LocalDateTime.now());
        banco.setEstado("ACT");
        return this.bancoRepository.save(banco);
    }

    @Transactional
    public Banco actualizar(Integer id, Banco banco) {
        Banco bancoExistente = this.bancoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Banco no encontrado"));
        
        bancoExistente.setCodigoInterno(banco.getCodigoInterno());
        bancoExistente.setRazonSocial(banco.getRazonSocial());
        bancoExistente.setNombreComercial(banco.getNombreComercial());
        
        return this.bancoRepository.save(bancoExistente);
    }

    @Transactional
    public void inactivar(Integer id) {
        Banco banco = this.bancoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Banco no encontrado"));
        
        banco.setEstado("INA");
        banco.setFechaInactivacion(LocalDateTime.now());
        this.bancoRepository.save(banco);
    }
}
