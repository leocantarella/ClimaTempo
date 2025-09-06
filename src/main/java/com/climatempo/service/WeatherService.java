package com.climatempo.service;

import com.climatempo.client.WeatherClient;
import com.climatempo.dto.WeatherResponse;
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
        return weatherClient.climaPorCidade(cidade, apiKey, "metric");
    }



}
