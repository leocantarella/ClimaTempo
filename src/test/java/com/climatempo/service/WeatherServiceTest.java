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

        WeatherClient client = mock(WeatherClient.class);

        WeatherService service = new WeatherService((client));

        // injeta a apiKey no campo privado anotado com @Value
        ReflectionTestUtils.setField(service, "apiKey", "KEY_FAKE");

        var main = new WeatherResponse.Main(17.45, 17.29, 78);
        var resp = new WeatherResponse(main, "São Paulo");

        when(client.climaPorCidade("Sao Paulo", "KEY_FAKE", "metric")).thenReturn(resp);

        var result = service.climaPorCidade("Sao Paulo");

        assertThat(result.name()).isEqualTo("São Paulo");
        assertThat(result.main().temp()).isEqualTo(17.45);

        verify(client).climaPorCidade("Sao Paulo", "KEY_FAKE", "metric");


    }

}
