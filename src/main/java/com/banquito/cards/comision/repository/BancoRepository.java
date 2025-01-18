package com.banquito.cards.comision.repository;

import com.banquito.cards.comision.model.Banco;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BancoRepository extends JpaRepository<Banco, Integer> {
    
    List<Banco> findByEstado(String estado);
    
    List<Banco> findByRazonSocialContainingAndEstado(String razonSocial, String estado);
    
    Optional<Banco> findByRuc(String ruc);
    
    List<Banco> findByNombreComercialContainingAndEstado(String nombreComercial, String estado);
    
    boolean existsByRuc(String ruc);
    
    boolean existsByCodigoInterno(String codigoInterno);
    
    Optional<Banco> findByRucAndEstado(String ruc, String estado);
    
    Optional<Banco> findByCodigoInternoAndEstado(String codigoInterno, String estado);
}
