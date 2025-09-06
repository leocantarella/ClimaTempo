package com.climatempo.dto;

public record ForecastResponse(java.util.List<Item> list) {
    public record Item(Main main, Wind wind, Double pop, Rain rain, long dt) {}
    public record Main(double temp, double feels_like, int humidity) {}
    public record Wind(double speed) {}           // m/s (metric)
    public record Rain(@com.fasterxml.jackson.annotation.JsonProperty("3h") Double threeH) {}
}