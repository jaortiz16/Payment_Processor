package com.banquito.cards.comision.repository;

import com.banquito.cards.comision.model.Banco;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BancoRepository extends JpaRepository<Banco, Integer> {
}
