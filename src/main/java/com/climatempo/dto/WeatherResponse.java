package com.climatempo.dto;

public record WeatherResponse(
        Main main,
        Wind wind,
        Sys sys,
        Integer timezone,
        String name
) {
    public record Main(double temp, double feels_like, int humidity) {}

    public record Wind(double speed, int deg) {}

    public record Sys(long sunrise, long sunset) {}
}
