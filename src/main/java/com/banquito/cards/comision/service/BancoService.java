package com.banquito.cards.comision.service;

import com.banquito.cards.comision.model.Banco;
import com.banquito.cards.comision.model.Comision;
import com.banquito.cards.comision.repository.BancoRepository;
import com.banquito.cards.comision.repository.ComisionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.time.LocalDateTime;

@Service
@Transactional
public class BancoService {

    private static final String ESTADO_ACTIVO = "ACT";
    private static final String ESTADO_INACTIVO = "INA";

    private final BancoRepository bancoRepository;
    private final ComisionRepository comisionRepository;

    public BancoService(BancoRepository bancoRepository, ComisionRepository comisionRepository) {
        this.bancoRepository = bancoRepository;
        this.comisionRepository = comisionRepository;
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
        Banco banco = this.bancoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No existe el banco con id: " + id));
        
        if (banco.getComision() != null) {
            Comision comision = this.comisionRepository.findById(banco.getComision().getCodigo())
                    .orElseThrow(() -> new RuntimeException("No existe la comisión asociada al banco"));
            banco.setComision(comision);
        }
        
        return banco;
    }

    @Transactional(readOnly = true)
    public Banco obtenerBancoPorRuc(String ruc) {
        if (ruc == null || ruc.trim().isEmpty()) {
            throw new RuntimeException("El RUC es requerido");
        }
        Banco banco = this.bancoRepository.findByRuc(ruc)
                .orElseThrow(() -> new RuntimeException("No existe el banco con RUC: " + ruc));

        if (banco.getComision() != null) {
            Comision comision = this.comisionRepository.findById(banco.getComision().getCodigo())
                    .orElseThrow(() -> new RuntimeException("No existe la comisión asociada al banco"));
            banco.setComision(comision);
        }
        
        return banco;
    }

    public Banco agregarBanco(Banco banco) {
        try {
            validarBanco(banco);
            if (banco.getComision() == null) {
                throw new RuntimeException("El banco debe tener una comisión asignada");
            }
            Comision comision = this.comisionRepository.findById(banco.getComision().getCodigo())
                    .orElseThrow(() -> new RuntimeException("La comisión especificada no existe"));
            banco.setComision(comision);
    
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
            
            if (banco.getComision() != null && !banco.getComision().getCodigo().equals(bancoExistente.getComision().getCodigo())) {
                Comision nuevaComision = this.comisionRepository.findById(banco.getComision().getCodigo())
                        .orElseThrow(() -> new RuntimeException("La nueva comisión especificada no existe"));
                bancoExistente.setComision(nuevaComision);
            }
            
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
