package com.wefin.repository;

import com.wefin.model.Moeda;
import com.wefin.model.Reino;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MoedaRepository extends JpaRepository<Moeda, Long> {
    Optional<Moeda> findByNome(String nome);
}
