package com.climatempo.dto;

public record ClimaResponse(DadosClima dados, String cidade) {
    public record DadosClima(double temperatura, double sensacao, int umidade) {}
}
