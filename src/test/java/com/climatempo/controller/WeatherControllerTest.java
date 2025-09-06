package com.climatempo.controller; // <= AJUSTE para o seu pacote real

import com.climatempo.dto.WeatherResponse;
import com.climatempo.service.WeatherService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean; //
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = WeatherController.class)
class WeatherControllerTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    WeatherService service;

    @Test

    //Esse método deve retornar um Json básico com as informações que foram configuradas no mock
    void deveRetornarJsonBasico() throws Exception {
        var main = new WeatherResponse.Main(17.45, 17.29, 78);
        var resp = new WeatherResponse(main, "São Paulo");

        when(service.climaPorCidade("Sao Paulo")).thenReturn(resp);

        mvc.perform(get("/api/weather/{city}", "Sao Paulo"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("São Paulo"))
                .andExpect(jsonPath("$.main.temp").value(17.45));
    }
}
