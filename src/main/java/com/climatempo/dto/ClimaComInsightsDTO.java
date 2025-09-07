package com.climatempo.dto;

public class ClimaComInsightsDTO {
    private ClimaBasicoDTO clima;
    private InsightsDTO insights;

    public ClimaComInsightsDTO() {}

    public ClimaComInsightsDTO(ClimaBasicoDTO clima, InsightsDTO insights) {
        this.clima = clima;
        this.insights = insights;
    }

    public ClimaBasicoDTO getClima() { return clima; }
    public void setClima(ClimaBasicoDTO clima) { this.clima = clima; }

    public InsightsDTO getInsights() { return insights; }
    public void setInsights(InsightsDTO insights) { this.insights = insights; }
}
