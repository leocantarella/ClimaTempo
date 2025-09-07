
package com.climatempo.service;

import com.climatempo.client.WeatherClient;
import com.climatempo.dto.ForecastResponse;
import com.climatempo.model.Cidade;
import com.climatempo.repository.CidadeRepository;
import com.climatempo.repository.ObservacaoClimaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ViabilidadeObraServiceTest {

    @Mock WeatherClient weatherClient;
    @Mock ObservacaoClimaRepository observacaoClimaRepository;
    @Mock CidadeRepository cidadeRepository;

    @InjectMocks ViabilidadeObraService service;

    @Test
    void deveAvaliarAteCincoDias() {
        Cidade cidade = new Cidade();
        cidade.setId(1L);
        cidade.setNome("Palmas");
        when(cidadeRepository.findById(1L)).thenReturn(Optional.of(cidade));

        ZoneId tz = ZoneId.of("America/Sao_Paulo");
        LocalDate hoje = LocalDate.now(tz);

        long dt1 = hoje.atTime(6,0).atZone(tz).toEpochSecond();
        long dt2 = hoje.atTime(9,0).atZone(tz).toEpochSecond();
        long dt3 = hoje.plusDays(1).atTime(12,0).atZone(tz).toEpochSecond();

        ForecastResponse.Item i1 = new ForecastResponse.Item(
                new ForecastResponse.Main(24.0, 24.0, 40),
                new ForecastResponse.Wind(3.0),
                0.0,
                new ForecastResponse.Rain(null),
                dt1
        );
        ForecastResponse.Item i2 = new ForecastResponse.Item(
                new ForecastResponse.Main(26.0, 26.0, 35),
                new ForecastResponse.Wind(4.0),
                0.0,
                new ForecastResponse.Rain(null),
                dt2
        );
        ForecastResponse.Item i3 = new ForecastResponse.Item(
                new ForecastResponse.Main(25.0, 25.0, 30),
                new ForecastResponse.Wind(5.0),
                0.0,
                new ForecastResponse.Rain(null),
                dt3
        );

        ForecastResponse forecast = new ForecastResponse(List.of(i1, i2, i3));
        when(weatherClient.previsaoPorCidade(eq("Palmas"), any(), eq("metric"))).thenReturn(forecast);

        var dias = service.avaliarObra5Dias(1L);

        assertThat(dias).isNotEmpty();
        assertThat(dias.size()).isBetween(1, 5);
        assertThat(dias.get(0).metricas().tempDia()).isGreaterThan(0.0);
    }
}
