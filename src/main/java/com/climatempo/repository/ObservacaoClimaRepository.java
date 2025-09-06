package com.climatempo.repository;

import com.climatempo.model.ObservacaoClima;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ObservacaoClimaRepository extends JpaRepository<ObservacaoClima, Long> {

    List<ObservacaoClima> findTop10ByCidade_IdOrderByObservadoEmDesc(Long cidadeId);
}