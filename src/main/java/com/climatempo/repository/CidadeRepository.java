package com.climatempo.repository;

import com.climatempo.model.Cidade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CidadeRepository extends JpaRepository<Cidade, Long> {

    public Optional<Cidade> findByNomeIgnoreCase(String nome);
}
