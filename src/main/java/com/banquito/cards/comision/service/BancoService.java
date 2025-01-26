package com.banquito.cards.comision.service;

import com.banquito.cards.comision.model.Banco;
import com.banquito.cards.comision.model.Comision;
import com.banquito.cards.comision.repository.BancoRepository;
import com.banquito.cards.comision.repository.ComisionRepository;
import com.banquito.cards.exception.NotFoundException;
import com.banquito.cards.exception.BusinessException;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class BancoService {

    private static final String ENTITY_NAME = "Banco";
    private static final String ESTADO_ACTIVO = "ACT";
    private static final String ESTADO_INACTIVO = "INA";
    private final BancoRepository bancoRepository;
    private final ComisionRepository comisionRepository;

    public BancoService(BancoRepository bancoRepository, ComisionRepository comisionRepository) {
        this.bancoRepository = bancoRepository;
        this.comisionRepository = comisionRepository;
    }

    @Transactional(readOnly = true)
    public List<Banco> obtenerTodosLosBancos(String sortBy, String sortDir) {
        Sort.Direction direction = Sort.Direction.fromString(sortDir.toUpperCase());
        Sort sort = Sort.by(direction, sortBy);
        return this.bancoRepository.findAll(sort);
    }

    @Transactional(readOnly = true)
    public List<Banco> obtenerBancosActivos() {
        return this.bancoRepository.findByEstado(ESTADO_ACTIVO);
    }

    @Transactional(readOnly = true)
    public List<Banco> obtenerBancosPorRazonSocialYEstado(String razonSocial, String estado) {
        validarEstado(estado);
        return this.bancoRepository.findByRazonSocialContainingAndEstado(razonSocial, estado);
    }

    @Transactional(readOnly = true)
    public List<Banco> obtenerBancosPorNombreYEstado(String nombreComercial, String estado) {
        validarEstado(estado);
        return this.bancoRepository.findByNombreComercialContainingAndEstado(nombreComercial, estado);
    }

    @Transactional(readOnly = true)
    public Banco obtenerBancoPorId(Integer id) {
        return this.bancoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(id.toString(), ENTITY_NAME));
    }

    public Banco obtenerBancoPorRuc(String ruc) {
        return bancoRepository.findByRucAndEstado(ruc, ESTADO_ACTIVO)
                .orElseThrow(() -> new NotFoundException(ruc, ENTITY_NAME));
    }

    public Banco crearBanco(Banco banco) {
        validarBanco(banco);
        if (bancoRepository.existsByRuc(banco.getRuc())) {
            throw new BusinessException(banco.getRuc(), ENTITY_NAME, "crear - RUC duplicado");
        }
        if (bancoRepository.existsByCodigoInterno(banco.getCodigoInterno())) {
            throw new BusinessException(banco.getCodigoInterno(), ENTITY_NAME, "crear - código interno duplicado");
        }
        
        if (banco.getComision() != null && banco.getComision().getCodigo() != null) {
            Comision comision = comisionRepository.findById(banco.getComision().getCodigo())
                .orElseThrow(() -> new NotFoundException(banco.getComision().getCodigo().toString(), "Comision"));
            banco.setComision(comision);
        }
        
        banco.setEstado(ESTADO_ACTIVO);
        banco.setFechaCreacion(LocalDateTime.now());
        return this.bancoRepository.save(banco);
    }

    public Banco actualizarBanco(Integer id, Banco banco) {
        Banco bancoExistente = obtenerBancoPorId(id);
        validarBanco(banco);
        
        if (!bancoExistente.getRuc().equals(banco.getRuc()) && 
            bancoRepository.existsByRuc(banco.getRuc())) {
            throw new BusinessException(banco.getRuc(), ENTITY_NAME, "actualizar - RUC duplicado");
        }
        if (!bancoExistente.getCodigoInterno().equals(banco.getCodigoInterno()) && 
            bancoRepository.existsByCodigoInterno(banco.getCodigoInterno())) {
            throw new BusinessException(banco.getCodigoInterno(), ENTITY_NAME, "actualizar - código interno duplicado");
        }
        
        if (banco.getComision() != null && banco.getComision().getCodigo() != null) {
            Comision comision = comisionRepository.findById(banco.getComision().getCodigo())
                .orElseThrow(() -> new NotFoundException(banco.getComision().getCodigo().toString(), "Comision"));
            bancoExistente.setComision(comision);
        }
        
        bancoExistente.setRazonSocial(banco.getRazonSocial());
        bancoExistente.setNombreComercial(banco.getNombreComercial());
        bancoExistente.setRuc(banco.getRuc());
        bancoExistente.setCodigoInterno(banco.getCodigoInterno());
        
        return this.bancoRepository.save(bancoExistente);
    }

    public void inactivarBanco(Integer id) {
        Banco banco = obtenerBancoPorId(id);
        if (ESTADO_INACTIVO.equals(banco.getEstado())) {
            throw new BusinessException(id.toString(), ENTITY_NAME, "inactivar - banco ya inactivo");
        }
        banco.setEstado(ESTADO_INACTIVO);
        banco.setFechaInactivacion(LocalDateTime.now());
        this.bancoRepository.save(banco);
    }

    private void validarBanco(Banco banco) {
        if (banco.getRazonSocial() == null || banco.getRazonSocial().trim().isEmpty()) {
            throw new BusinessException("razón social", ENTITY_NAME, "validar - campo requerido");
        }
        if (banco.getNombreComercial() == null || banco.getNombreComercial().trim().isEmpty()) {
            throw new BusinessException("nombre comercial", ENTITY_NAME, "validar - campo requerido");
        }
        if (banco.getRuc() == null || banco.getRuc().trim().isEmpty()) {
            throw new BusinessException("RUC", ENTITY_NAME, "validar - campo requerido");
        }
        if (banco.getCodigoInterno() == null || banco.getCodigoInterno().trim().isEmpty()) {
            throw new BusinessException("código interno", ENTITY_NAME, "validar - campo requerido");
        }
    }

    private void validarEstado(String estado) {
        if (!List.of(ESTADO_ACTIVO, ESTADO_INACTIVO).contains(estado)) {
            throw new BusinessException(estado, ENTITY_NAME, "validar - estado inválido");
        }
    }
}
