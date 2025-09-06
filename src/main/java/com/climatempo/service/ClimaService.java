package com.climatempo.service;


import com.climatempo.client.WeatherClient;
import com.climatempo.dto.ForecastResponse;
import com.climatempo.dto.InsightObraDiaResponse;
import com.climatempo.dto.WeatherResponse;
import com.climatempo.model.Cidade;
import com.climatempo.model.ObservacaoClima;
import com.climatempo.repository.CidadeRepository;
import com.climatempo.repository.ObservacaoClimaRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ClimaService {

    private final WeatherClient weatherClient;
    private final ObservacaoClimaRepository obsRepository;
    private final CidadeRepository cidadeRepository;

    public ClimaService(WeatherClient weatherClient, ObservacaoClimaRepository obsRepository, CidadeRepository cidadeRepository) {
        this.weatherClient = weatherClient;
        this.obsRepository = obsRepository;
        this.cidadeRepository = cidadeRepository;
    }

    @Value("${openweathermap.api.key}")
    private String apiKey;


    //Cria a observação da cidade
    @Transactional
    public WeatherResponse climaPorCidade(String nomeCidade){
        WeatherResponse api = weatherClient.climaPorCidade(nomeCidade, apiKey, "metric");

        String nomeNormalizado = api.name();
        Cidade cidade = cidadeRepository.findByNomeIgnoreCase(nomeNormalizado)
                .orElseGet(() -> {
                    Cidade c = new Cidade();
                    c.setNome(nomeNormalizado);
                    return cidadeRepository.save(c);
                });

        ObservacaoClima o = new ObservacaoClima();
        o.setCidade(cidade);
        o.setObservadoEm(LocalDateTime.now());
        o.setTempC(BigDecimal.valueOf(api.main().temp()));
        o.setSensacaoTerm(BigDecimal.valueOf(api.main().feels_like()));
        o.setUmidade(api.main().humidity());
        obsRepository.save(o);

        return api;
    }

    //Filtramos as últimas 10 observações por ID da cidade
    public List<ObservacaoClima>ultimas10PorCidade(Long cidadeId){
        return obsRepository.findTop10ByCidade_IdOrderByObservadoEmDesc(cidadeId);
    }


    //Removemos uma observação do DB
    @Transactional
    public void removerRegistro(Long id){
        obsRepository.deleteById(id);
    }

    // Limiares
    private static final double LIMITE_PROB_CHUVA = 0.40;
    private static final double LIMITE_CHUVA_MM   = 2.0;
    private static final double LIMITE_VENTO_KMH  = 30.0;
    private static final double TEMP_MIN_OK       = 15.0;
    private static final double TEMP_MAX_OK       = 32.0;
    private static final int    UMIDADE_MAX_OK    = 85;

    private static final java.time.ZoneId ZONE = java.time.ZoneId.of("America/Sao_Paulo");

    // === NOVO: insight 5 dias sem One Call ===
    public java.util.List<InsightObraDiaResponse> avaliarObra5Dias(Long cidadeId) {
        if (cidadeId == null) throw new IllegalArgumentException("cidadeId obrigatório.");

        Cidade cidade = cidadeRepository.findById(cidadeId)
                .orElseThrow(() -> new IllegalArgumentException("Cidade não encontrada: id=" + cidadeId));

        // 1) Pegar forecast 5d/3h pelo NOME
        ForecastResponse forecast = weatherClient.previsaoPorCidade(cidade.getNome(), apiKey, "metric");
        java.util.List<ForecastResponse.Item> itens = (forecast != null && forecast.list() != null)
                ? forecast.list() : java.util.List.of();
        if (itens.isEmpty()) return java.util.List.of();

        // 2) Agregar por DIA local (America/Sao_Paulo)
        // Vamos usar um Map<LocalDate, List<Item>> simples
        java.util.Map<java.time.LocalDate, java.util.List<ForecastResponse.Item>> porDia = new java.util.LinkedHashMap<>();

        for (int i = 0; i < itens.size(); i++) {
            ForecastResponse.Item it = itens.get(i);
            java.time.LocalDate diaLocal = java.time.Instant.ofEpochSecond(it.dt())
                    .atZone(ZONE).toLocalDate();
            porDia.computeIfAbsent(diaLocal, d -> new java.util.ArrayList<>()).add(it);
        }

        // 3) Selecionar até 5 dias a partir de HOJE
        java.time.LocalDate hoje = java.time.LocalDate.now(ZONE);
        java.util.List<java.time.LocalDate> diasOrdenados = new java.util.ArrayList<>(porDia.keySet());
        diasOrdenados.sort(java.util.Comparator.naturalOrder());

        java.util.List<InsightObraDiaResponse> saida = new java.util.ArrayList<>();
        int adicionados = 0;

        for (int di = 0; di < diasOrdenados.size(); di++) {
            java.time.LocalDate dia = diasOrdenados.get(di);
            if (dia.isBefore(hoje)) continue; // ignora dias passados

            java.util.List<ForecastResponse.Item> blocos = porDia.get(dia);
            if (blocos == null || blocos.isEmpty()) continue;

            // 4) Calcular métricas do dia
            double tempMin = Double.POSITIVE_INFINITY;
            double tempMax = Double.NEGATIVE_INFINITY;
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

                // POP 0..1
                double pop = (b.pop() != null) ? b.pop() : 0.0;
                if (pop > probChuvaMax) probChuvaMax = pop;

                // chuva acumulada 3h
                double mm = (b.rain() != null && b.rain().threeH() != null) ? b.rain().threeH() : 0.0;
                chuvaTotalMm += mm;
            }

            double tempDia = (cont > 0) ? (somaTemp / cont) : Double.NaN;
            int umidadeMedia = (cont > 0) ? (int)Math.round((double)somaUmid / cont) : 0;

            // 5) Scoring e motivos
            int score = 100;
            java.util.List<String> motivos = new java.util.ArrayList<>();

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

            InsightObraDiaResponse.Metricas metricas = new InsightObraDiaResponse.Metricas(
                    tempMin, tempMax, tempDia, umidadeMedia, ventoMaxKmH, probChuvaMax, chuvaTotalMm
            );

            saida.add(new InsightObraDiaResponse(dia, bom, score, motivos, metricas));
            adicionados++;
            if (adicionados == 5) break; // só 5 dias
        }

        return saida;
    }

}
