package com.climatempo.controller;

import com.climatempo.dto.ClimaCardResponse;
import com.climatempo.dto.ClimaComInsightsDTO;
import com.climatempo.dto.InsightObraDiaResponse;
import com.climatempo.model.ObservacaoClima;
import com.climatempo.repository.ObservacaoClimaRepository;
import com.climatempo.service.ClimaService;
import com.climatempo.service.ViabilidadeObraService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/clima")
public class ClimaController {

    private final ClimaService climaService;
    private final ViabilidadeObraService viabilidadeObraService;

    public ClimaController(ClimaService climaService, ViabilidadeObraService viabilidadeObraService, ObservacaoClimaRepository observacaoClimaRepository) {
        this.climaService = climaService;
        this.viabilidadeObraService = viabilidadeObraService;
    }

    // Exibe clima + insights em formato "card"
    @GetMapping("/{cidade}")
    public ResponseEntity<ClimaCardResponse> consultarCard(@PathVariable String cidade) {
        if (cidade == null || cidade.isBlank()) {
            throw new IllegalArgumentException("Informe o nome da cidade.");
        }

        ClimaComInsightsDTO dto = climaService.consultarComInsights(cidade);

        String climaAtual = String.format(
                "🌡️ %.1f°C | 🤔 Sensação: %.1f°C | 💧 Umidade: %d%%",
                dto.getClima().getTemperatura(),
                dto.getClima().getSensacaoTermica(),
                dto.getClima().getUmidade()
        );

        String insights = String.format(
                "😌 Conforto: %s | 🥤 Hidratação: %s | 🌬️ Vento: %s | ⏰ Horários: %s",
                dto.getInsights().getConfortoTermico(),
                dto.getInsights().getHidratacao(),
                dto.getInsights().getAlertaVento(),
                dto.getInsights().getHorariosRecomendados()
        );

        ClimaCardResponse body = new ClimaCardResponse(
                "📍 " + dto.getClima().getCidade(),
                climaAtual,
                insights
        );
        return ResponseEntity.ok(body);
    }

    // Últimas 10 observações por cidade
    @GetMapping("/{cidadeId}/historico")
    public ResponseEntity<List<ObservacaoClima>> historico(@PathVariable Long cidadeId) {
        if (cidadeId == null) {
            throw new IllegalArgumentException("cidadeId é obrigatório.");
        }
        return ResponseEntity.ok(climaService.ultimas10PorCidade(cidadeId));
    }

    // Avaliação para obra (próximos 5 dias)
    @GetMapping("/avaliarobra/{cidadeId}")
    public ResponseEntity<List<InsightObraDiaResponse>> obra5dias(@PathVariable Long cidadeId) {
        if (cidadeId == null) {
            throw new IllegalArgumentException("ID da cidade é obrigatório.");
        }
        return ResponseEntity.ok(viabilidadeObraService.avaliarObra5Dias(cidadeId));
    }

    @DeleteMapping("/remover/{obsId}")
    public ResponseEntity<Void> removerObservacao(@PathVariable Long obsId) {
        if (obsId == null) {
            throw new IllegalArgumentException("ID da observação é obrigatório.");
        }
        climaService.removerRegistro(obsId);
        return ResponseEntity.noContent().build(); // 204 sem corpo
    }

}
