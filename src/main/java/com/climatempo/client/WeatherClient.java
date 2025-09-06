package com.climatempo.client;

import com.climatempo.dto.ForecastResponse;
import com.climatempo.dto.WeatherResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "weatherClient", url = "${openweathermap.url}")
public interface WeatherClient {

    @GetMapping("/data/2.5/weather")
    WeatherResponse climaPorCidade(@RequestParam("q") String city,
                               @RequestParam("appid") String apiKey,
                               @RequestParam("units") String units);


    @GetMapping("/data/2.5/forecast")
    ForecastResponse previsaoPorCidade(@RequestParam("q") String cidade,
                                       @RequestParam("appid") String apiKey,
                                       @RequestParam("units") String units);
}