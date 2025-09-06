package com.climatempo.controller;


import com.climatempo.dto.InsightObraDiaResponse;
import com.climatempo.dto.WeatherResponse;
import com.climatempo.model.ObservacaoClima;
import com.climatempo.service.ClimaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/weather")
public class ClimaController {

    private final ClimaService climaService;

    public ClimaController(ClimaService climaService) {
        this.climaService = climaService;
    }

    @GetMapping("/{cidade}")
    public ResponseEntity<WeatherResponse> clima(@PathVariable String cidade){
        return ResponseEntity.ok(climaService.climaPorCidade(cidade));
    }

    @GetMapping("/{cidadeId}/historico")
    public ResponseEntity<List<ObservacaoClima>> historico(@PathVariable Long cidadeId) {
        return ResponseEntity.ok(climaService.ultimas10PorCidade(cidadeId));
    }

    @GetMapping("/avaliarobra/{cidadeId}")
    public ResponseEntity<java.util.List<InsightObraDiaResponse>> obra5dias(@PathVariable Long cidadeId) {
        return ResponseEntity.ok(climaService.avaliarObra5Dias(cidadeId));
    }
}
