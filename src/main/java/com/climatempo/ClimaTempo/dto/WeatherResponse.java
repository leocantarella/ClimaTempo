package com.climatempo.ClimaTempo.dto;

public record WeatherResponse(
        Main main,
        String name
) {
    public record Main(
            double temp,
            double feels_like,
            int humidity
    ) {}
}
