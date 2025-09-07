package com.climatempo.service;


import com.climatempo.client.WeatherClient;
import com.climatempo.dto.ForecastResponse;
import com.climatempo.dto.InsightObraDiaResponse;
import com.climatempo.model.Cidade;
import com.climatempo.repository.CidadeRepository;
import com.climatempo.repository.ObservacaoClimaRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;


@Service
public class ViabilidadeObraService {


    private final WeatherClient weatherClient;
    private final ObservacaoClimaRepository obsRepository;
    private final CidadeRepository cidadeRepository;
    private final InsightsService insightsService;

    public ViabilidadeObraService(WeatherClient weatherClient, ObservacaoClimaRepository obsRepository, CidadeRepository cidadeRepository, InsightsService insightsService) {
        this.weatherClient = weatherClient;
        this.obsRepository = obsRepository;
        this.cidadeRepository = cidadeRepository;
        this.insightsService = insightsService;
    }


    @Value("${openweathermap.api.key}")
    private String apiKey;

    // Constantes
    private static final double LIMITE_PROB_CHUVA = 0.40;  //Limite de 40% probabilidade de chuva
    private static final double LIMITE_CHUVA_MM   = 2.0; //Limite de 2mm de chuva acumulada
    private static final double LIMITE_VENTO_KMH  = 30.0; //Limite de ventos de 30km/h
    private static final double TEMP_MIN_OK       = 15.0; // Temperatura mínima
    private static final double TEMP_MAX_OK       = 32.0; // Temperatura máxima
    private static final int    UMIDADE_MAX_OK    = 85; // Umidade em 85%

    private static final java.time.ZoneId ZONE = java.time.ZoneId.of("America/Sao_Paulo");

    public List<InsightObraDiaResponse> avaliarObra5Dias(Long cidadeId) {
        if (cidadeId == null) throw new IllegalArgumentException("Obrigatório informar o ID da ");

        Cidade cidade = cidadeRepository.findById(cidadeId)
                .orElseThrow(() -> new IllegalArgumentException("Cidade não encontrada!"));

        // 1) Pegar o forecast de 5 dias e "proteção" contra valores nulos
        ForecastResponse forecast = weatherClient.previsaoPorCidade(cidade.getNome(), apiKey, "metric");
        List<ForecastResponse.Item> itens = (forecast != null && forecast.list() != null)
                ? forecast.list() :List.of();
        if (itens.isEmpty()) return List.of();

        // 2) Agregar por DIA local (America/Sao_Paulo)
       Map<LocalDate, List<ForecastResponse.Item>> porDia = new LinkedHashMap<>();

        for (int i = 0; i < itens.size(); i++) {
            ForecastResponse.Item it = itens.get(i);
            LocalDate diaLocal = java.time.Instant.ofEpochSecond(it.dt())
                    .atZone(ZONE).toLocalDate();
            porDia.computeIfAbsent(diaLocal, d -> new ArrayList<>()).add(it);
        }

        // 3) Selecionar 5 dias a partir de HOJE
      LocalDate hoje = LocalDate.now(ZONE);
     List<LocalDate> diasOrdenados = new ArrayList<>(porDia.keySet());
        diasOrdenados.sort(Comparator.naturalOrder());

       List<InsightObraDiaResponse> saida = new ArrayList<>();
        int adicionados = 0;

        for (int di = 0; di < diasOrdenados.size(); di++) {
            java.time.LocalDate dia = diasOrdenados.get(di);
            if (dia.isBefore(hoje)) continue; // ignora dias passados

            List<ForecastResponse.Item> blocos = porDia.get(dia);
            if (blocos == null || blocos.isEmpty()) continue;

            // 4) Calcular métricas do dia
            double tempMin = blocos.get(0).main().temp();
            double tempMax = tempMin;
            double somaTemp = 0.0;
            int    somaUmid = 0;
            int    cont     = 0;

            double ventoMaxKmH = 0.0;
            double probChuvaMax = 0.0;
            double chuvaTotalMm = 0.0;

            for (int i = 0; i < blocos.size(); i++) {
                ForecastResponse.Item b = blocos.get(i);

                double t = b.main().temp();
                if (t < tempMin) tempMin = t;
                if (t > tempMax) tempMax = t;
                somaTemp += t;

                somaUmid += b.main().humidity();
                cont++;

                // vento m/s -> km/h
                double vMs = (b.wind() != null) ? b.wind().speed() : 0.0;
                double vKmH = vMs * 3.6;
                if (vKmH > ventoMaxKmH) ventoMaxKmH = vKmH;

                // Probabilidade 0..1
                double prob = (b.pop() != null) ? b.pop() : 0.0;
                if (prob > probChuvaMax) probChuvaMax = prob;

                // chuva acumulada 3h
                double mm = (b.rain() != null && b.rain().threeH() != null) ? b.rain().threeH() : 0.0;
                chuvaTotalMm += mm;
            }

            double tempDia = (cont > 0) ? (somaTemp / cont) : Double.NaN;
            int umidadeMedia = (cont > 0) ? (int)Math.round((double)somaUmid / cont) : 0;

            // 5) Pontuação e motivos
            int score = 100;
           List<String> motivos = new ArrayList<>();

            if (probChuvaMax >= LIMITE_PROB_CHUVA) {
                score -= 35; motivos.add("Alta probabilidade de chuva.");
            }
            if (chuvaTotalMm >= LIMITE_CHUVA_MM) {
                score -= 25; motivos.add("Chuva acumulada significativa.");
            }
            if (ventoMaxKmH >= LIMITE_VENTO_KMH) {
                score -= 20; motivos.add("Vento forte previsto.");
            }
            if (tempDia < TEMP_MIN_OK || tempDia > TEMP_MAX_OK) {
                score -= 25; motivos.add("Temperatura pouco adequada para obra.");
            }
            if (umidadeMedia >= UMIDADE_MAX_OK) {
                score -= 20; motivos.add("Umidade muito alta.");
            }

            if (score < 0) score = 0;
            if (score > 100) score = 100;

            boolean bom = (score >= 60);
            if (motivos.isEmpty()) motivos.add("Condições gerais favoráveis.");

            InsightObraDiaResponse.Metricas metricas = new InsightObraDiaResponse.
                    Metricas(tempMin, tempMax, tempDia, umidadeMedia, ventoMaxKmH, probChuvaMax, chuvaTotalMm
            );

            saida.add(new InsightObraDiaResponse(dia, bom, score, motivos, metricas));
            adicionados++;
            if (adicionados == 5) break; // só 5 dias
        }
        return saida;
    }
}
