package com.climatempo.ClimaTempo.service;

import com.climatempo.ClimaTempo.client.WeatherClient;
import com.climatempo.ClimaTempo.dto.WeatherResponse;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class WeatherService {

    private final WeatherClient weatherClient;

    public WeatherService(WeatherClient weatherClient) {
        this.weatherClient = weatherClient;
    }

    @Value("${openweathermap.api.key}")
    private String apiKey;

    public WeatherResponse climaPorCidade(String cidade){
        return weatherClient.getWeather(cidade, apiKey, "metric");
    }



}
