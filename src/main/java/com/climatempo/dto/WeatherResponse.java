package com.climatempo.dto;

public record WeatherResponse(
        Main main,
        String name,
        Coord coord
) {
    public record Main(
            double temp,
            double feels_like,
            int humidity
    ) {}

    public record Coord(
            double lon,
            double lat
    ){}
}
