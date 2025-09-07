
package com.climatempo.repository;

import com.climatempo.model.Cidade;
import com.climatempo.model.ObservacaoClima;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ObservacaoClimaRepositoryTest {

    @Autowired ObservacaoClimaRepository obsRepo;
    @Autowired CidadeRepository cidadeRepo;

    @Test
    void findTop10ByCidadeOrderByObservadoEmDesc_ok() {
        Cidade c = new Cidade();
        c.setNome("Palmas");
        c = cidadeRepo.save(c);

        for (int i=0; i<12; i++) {
            ObservacaoClima o = new ObservacaoClima();
            o.setCidade(c);
            o.setObservadoEm(LocalDateTime.now().minusHours(12 - i));
            o.setTempC(BigDecimal.valueOf(25.0));
            o.setSensacaoTerm(BigDecimal.valueOf(25.0));
            o.setUmidade(30);
            obsRepo.save(o);
        }

        List<ObservacaoClima> ultimas10 = obsRepo.findTop10ByCidade_IdOrderByObservadoEmDesc(c.getId());
        assertThat(ultimas10).hasSize(10);
        assertThat(ultimas10.get(0).getObservadoEm()).isAfter(ultimas10.get(9).getObservadoEm());
    }
}
