package com.climatempo.client;



import com.github.tomakehurst.wiremock.WireMockServer;
import com.climatempo.dto.WeatherResponse;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.test.context.TestPropertySource;
import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@EnableFeignClients(clients = WeatherClient.class)
@TestPropertySource(properties = {
        "weather.api.url=http://localhost:9090" // Feign vai apontar pro WireMock
})
class WeatherClientIT {

    static WireMockServer wm;

    @BeforeAll
    static void up() {
        wm = new WireMockServer(9090);
        wm.start();
    }

    @AfterAll
    static void down() {
        wm.stop();
    }

    @Autowired
    WeatherClient client;

    @Test
    void deveMapearRespostaDoOpenWeather() {
        wm.stubFor(get(urlPathEqualTo("/weather"))
                .withQueryParam("q", equalTo("Sao Paulo"))
                .withQueryParam("appid", equalTo("X"))
                .withQueryParam("units", equalTo("metric"))
                .willReturn(okJson("""
              {
                "main": { "temp": 17.45, "feels_like": 17.29, "humidity": 78 },
                "name": "São Paulo"
              }
            """)));

        WeatherResponse r = client.climaPorCidade("Sao Paulo", "X", "metric");

        assertThat(r.name()).isEqualTo("São Paulo");
        assertThat(r.main().humidity()).isEqualTo(78);
    }
}