package com.climatempo.service;

import com.climatempo.client.WeatherClient;
import com.climatempo.dto.*;
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
    private final InsightsService insightsService;

    public ClimaService(WeatherClient weatherClient, ObservacaoClimaRepository obsRepository, CidadeRepository cidadeRepository, InsightsService insightsService) {
        this.weatherClient = weatherClient;
        this.obsRepository = obsRepository;
        this.cidadeRepository = cidadeRepository;
        this.insightsService = insightsService;
    }

    @Value("${openweathermap.api.key}")
    private String apiKey;


    //Cria a observação da cidade
    @Transactional
    public WeatherResponse climaPorCidade(String nomeCidade){
        WeatherResponse api = weatherClient.climaPorCidade(nomeCidade, apiKey, "metric", "pt_br");

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

    //Gerando a leitura de clima do dia com Insights

    public ClimaComInsightsDTO consultarComInsights(String nomeCidade) {
        WeatherResponse resp = weatherClient.climaPorCidade(nomeCidade, apiKey, "metric", "pt_br");

        String nomeNormalizado = resp.name();
        Cidade cidade = cidadeRepository.findByNomeIgnoreCase(nomeNormalizado)
                .orElseGet(() -> {
                    Cidade c = new Cidade();
                    c.setNome(nomeNormalizado);
                    return cidadeRepository.save(c);
                });

        ObservacaoClima o = new ObservacaoClima();
        o.setCidade(cidade);
        o.setObservadoEm(LocalDateTime.now());
        o.setTempC(BigDecimal.valueOf(resp.main().temp()));
        o.setSensacaoTerm(BigDecimal.valueOf(resp.main().feels_like()));
        o.setUmidade(resp.main().humidity());
        obsRepository.save(o);

        double temp      = resp.main().temp();
        double feelsLike = resp.main().feels_like();
        int humidity     = resp.main().humidity();

        double windMs = (resp.wind() != null) ? resp.wind().speed() : Double.NaN;
        long sunrise  = (resp.sys() != null) ? resp.sys().sunrise() : 0L;
        long sunset   = (resp.sys() != null) ? resp.sys().sunset() : 0L;
        Integer tz    = resp.timezone();

            // montar insights
        String conforto = insightsService.calcularConfortoTermico(temp, humidity);
        String hidrat   = insightsService.recomendacaoHidratacao(temp);
        String vento    = insightsService.alertaDeVento(windMs);
        String horarios = insightsService.melhoresHorariosDoDia(temp, sunrise, sunset, tz);

        InsightsDTO insights = new InsightsDTO(conforto, hidrat, vento, horarios);

        // montar clima
        ClimaBasicoDTO clima = new ClimaBasicoDTO(resp.name(), temp, feelsLike, humidity);

        // resposta final
        return new ClimaComInsightsDTO(clima, insights);
    }
}
