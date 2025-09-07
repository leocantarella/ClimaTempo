package com.climatempo.dto;

public class ClimaCardResponse {
    private String cidade;
    private String climaAtual;
    private String insights;

    public ClimaCardResponse() {}

    public ClimaCardResponse(String cidade, String climaAtual, String insights) {
        this.cidade = cidade;
        this.climaAtual = climaAtual;
        this.insights = insights;
    }

    public String getCidade() { return cidade; }
    public void setCidade(String cidade) { this.cidade = cidade; }

    public String getClimaAtual() { return climaAtual; }
    public void setClimaAtual(String climaAtual) { this.climaAtual = climaAtual; }

    public String getInsights() { return insights; }
    public void setInsights(String insights) { this.insights = insights; }
}
