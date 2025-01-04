package com.banquito.cards.comision.service;

import com.banquito.cards.comision.model.Banco;
import com.banquito.cards.comision.repository.BancoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.time.LocalDateTime;
//cambiar por runtime exception
@Service
@Transactional
public class BancoService {

    public static final String ESTADO_ACTIVO = "ACT";
    public static final String ESTADO_INACTIVO = "INA";

    private final BancoRepository bancoRepository;

    public BancoService(BancoRepository bancoRepository) {
        this.bancoRepository = bancoRepository;
    }

    @Transactional(readOnly = true)
    public List<Banco> obtenerBancosActivos() {
        return this.bancoRepository.findByEstado(ESTADO_ACTIVO);
    }

    @Transactional(readOnly = true)
    public List<Banco> obtenerBancosPorRazonSocialYEstado(String razonSocial, String estado) {
        if (razonSocial == null || razonSocial.trim().isEmpty()) {
            throw new RuntimeException("La razón social es requerida");
        }
        return this.bancoRepository.findByRazonSocialContainingAndEstado(razonSocial.toUpperCase(), estado);
    }

    @Transactional(readOnly = true)
    public List<Banco> obtenerBancosPorNombreYEstado(String nombreComercial, String estado) {
        if (nombreComercial == null || nombreComercial.trim().isEmpty()) {
            throw new RuntimeException("El nombre comercial es requerido");
        }
        return this.bancoRepository.findByNombreComercialContainingAndEstado(nombreComercial.toUpperCase(), estado);
    }

    @Transactional(readOnly = true)
    public Banco obtenerBancoPorId(Integer id) {
        return this.bancoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No existe el banco con id: " + id));
    }

    @Transactional(readOnly = true)
    public Banco obtenerBancoPorRuc(String ruc) {
        if (ruc == null || ruc.trim().isEmpty()) {
            throw new RuntimeException("El RUC es requerido");
        }
        return this.bancoRepository.findByRuc(ruc)
                .orElseThrow(() -> new RuntimeException("No existe el banco con RUC: " + ruc));
    }

    public Banco agregarBanco(Banco banco) {
        try {
            validarBanco(banco);
            if (banco.getComision() == null) {
                throw new RuntimeException("El banco debe tener una comisión asignada");
            }
    
            banco.setFechaCreacion(LocalDateTime.now());
            banco.setEstado(ESTADO_ACTIVO);
            return this.bancoRepository.save(banco);
        } catch (Exception e) {
            throw new RuntimeException("Error al crear el banco: " + e.getMessage());
        }
    }

    public Banco actualizarBanco(Integer id, Banco banco) {
        try {
            Banco bancoExistente = obtenerBancoPorId(id);
            validarBanco(banco);

            bancoExistente.setRazonSocial(banco.getRazonSocial());
            bancoExistente.setNombreComercial(banco.getNombreComercial());
            
            return this.bancoRepository.save(bancoExistente);
        } catch (Exception e) {
            throw new RuntimeException("Error al actualizar el banco: " + e.getMessage());
        }
    }

    public void inactivarBanco(Integer id) {
        try {
            Banco banco = obtenerBancoPorId(id);
            if (ESTADO_INACTIVO.equals(banco.getEstado())) {
                throw new RuntimeException("El banco ya se encuentra inactivo");
            }
            
            banco.setEstado(ESTADO_INACTIVO);
            banco.setFechaInactivacion(LocalDateTime.now());
            this.bancoRepository.save(banco);
        } catch (Exception e) {
            throw new RuntimeException("Error al inactivar el banco: " + e.getMessage());
        }
    }

    private void validarBanco(Banco banco) {
        if (banco.getCodigoInterno() == null || banco.getCodigoInterno().trim().isEmpty()) {
            throw new RuntimeException("El código interno es requerido");
        }
        if (banco.getRuc() == null || !banco.getRuc().matches("\\d{13}")) {
            throw new RuntimeException("El RUC debe tener 13 dígitos");
        }
        if (banco.getRazonSocial() == null || banco.getRazonSocial().trim().length() < 5) {
            throw new RuntimeException("La razón social debe tener al menos 5 caracteres");
        }
        if (banco.getNombreComercial() == null || banco.getNombreComercial().trim().length() < 3) {
            throw new RuntimeException("El nombre comercial debe tener al menos 3 caracteres");
        }
        if (banco.getComision() == null) {
            throw new RuntimeException("El banco debe tener una comisión asignada");
        }
    }
}
