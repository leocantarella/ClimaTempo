package com.climatempo.ClimaTempo.controller;


import com.climatempo.ClimaTempo.dto.WeatherResponse;
import com.climatempo.ClimaTempo.service.WeatherService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/weather")
public class WeatherController {

    private final WeatherService weatherService;

    public WeatherController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @GetMapping("/{cidade}")
    public ResponseEntity<WeatherResponse> getWeather(@PathVariable String cidade){
        return ResponseEntity.ok(weatherService.climaPorCidade(cidade));
    }
}
