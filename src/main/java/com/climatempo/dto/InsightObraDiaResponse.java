package com.climatempo.dto;

public record InsightObraDiaResponse(
        java.time.LocalDate dia,
        boolean bom,
        int score,
        java.util.List<String> motivos,
        Metricas metricas
) {
    public record Metricas(
            double tempMin,
            double tempMax,
            double tempDia,
            int umidade,
            double ventoMaxKmH,
            double probChuva,
            double chuvaMm
    ) {}
}