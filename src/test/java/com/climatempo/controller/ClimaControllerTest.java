package com.climatempo.controller;

import com.climatempo.api.ApiExceptionHandler;
import com.climatempo.dto.ClimaBasicoDTO;
import com.climatempo.dto.ClimaComInsightsDTO;
import com.climatempo.dto.InsightObraDiaResponse;
import com.climatempo.dto.InsightsDTO;
import com.climatempo.repository.CidadeRepository;
import com.climatempo.repository.ObservacaoClimaRepository;
import com.climatempo.service.ClimaService;
import com.climatempo.service.ViabilidadeObraService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ClimaController.class)
@Import(ApiExceptionHandler.class)
class ClimaControllerTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    ClimaService climaService;

    @MockBean
    ViabilidadeObraService viabilidadeObraService;

    // Mocks extras para cobrir o construtor do controller (evita UnsatisfiedDependency)
    @MockBean
    ObservacaoClimaRepository observacaoClimaRepository;

    @MockBean
    CidadeRepository cidadeRepository;

    @Test
    void consultarCard_ok() throws Exception {
        // monta DTO retornado pelo service
        ClimaBasicoDTO clima = new ClimaBasicoDTO();
        clima.setCidade("Palmas");
        clima.setTemperatura(25.0);
        clima.setSensacaoTermica(25.0);
        clima.setUmidade(30);

        InsightsDTO ins = new InsightsDTO();
        ins.setConfortoTermico("Confortável");
        ins.setHidratacao("Moderada");
        ins.setAlertaVento("Normal");
        ins.setHorariosRecomendados("08:00–10:00");

        ClimaComInsightsDTO dto = new ClimaComInsightsDTO();
        dto.setClima(clima);
        dto.setInsights(ins);

        when(climaService.consultarComInsights(eq("Palmas"))).thenReturn(dto);

        mvc.perform(get("/api/clima/Palmas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cidade").value("📍 Palmas"))
                .andExpect(jsonPath("$.climaAtual", containsString("Umidade: 30%")))
                .andExpect(jsonPath("$.insights", containsString("Conforto: Confortável")));
    }

    @Test
    void obra5dias_ok() throws Exception {
        var met = new InsightObraDiaResponse.Metricas(
                20.0, 28.0, 24.0, 40, 10.0, 0.1, 0.0
        );
        var dia = new InsightObraDiaResponse(
                java.time.LocalDate.now(), true, 80, List.of("OK"), met
        );

        when(viabilidadeObraService.avaliarObra5Dias(1L)).thenReturn(List.of(dia));

        mvc.perform(get("/api/clima/avaliarobra/1"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("score")))
                .andExpect(content().string(containsString("metricas")));
    }
}
