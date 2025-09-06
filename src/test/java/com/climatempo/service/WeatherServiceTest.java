package com.climatempo.service;

import com.climatempo.client.WeatherClient;
import com.climatempo.dto.WeatherResponse;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

public class WeatherServiceTest {

    @Test
    void retornarClimaCidade() {


        //Criação de um WeatherClient simulado
        WeatherClient client = mock(WeatherClient.class);

        //Injeção desse client no service
        WeatherService service = new WeatherService((client));

        // injeta a apiKey
        ReflectionTestUtils.setField(service, "apiKey", "KEY_FAKE");

        //Configuração das respostas do mock
        var main = new WeatherResponse.Main(17.45, 17.29, 78);
        var resp = new WeatherResponse(main, "São Paulo");
        when(client.climaPorCidade("Sao Paulo", "KEY_FAKE", "metric")).thenReturn(resp);

        //Execução do método real
        var result = service.climaPorCidade("Sao Paulo");

        //Validação do retorno
        assertThat(result.name()).isEqualTo("São Paulo");
        assertThat(result.main().temp()).isEqualTo(17.45);

        //Verifica se o mock foi chamado corretamente
        verify(client).climaPorCidade("Sao Paulo", "KEY_FAKE", "metric");


    }

}
