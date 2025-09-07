package com.climatempo.dto;

public class ClimaBasicoDTO {
    private String cidade;
    private double temperatura;
    private double sensacaoTermica;
    private int umidade;


    public ClimaBasicoDTO() {}

    public ClimaBasicoDTO(String cidade, double temperatura, double sensacaoTermica, int umidade) {
        this.cidade = cidade;
        this.temperatura = temperatura;
        this.sensacaoTermica = sensacaoTermica;
        this.umidade = umidade;
    }

    public String getCidade() { return cidade; }
    public void setCidade(String cidade) { this.cidade = cidade; }

    public double getTemperatura() { return temperatura; }
    public void setTemperatura(double temperatura) { this.temperatura = temperatura; }

    public double getSensacaoTermica() { return sensacaoTermica; }
    public void setSensacaoTermica(double sensacaoTermica) { this.sensacaoTermica = sensacaoTermica; }

    public int getUmidade() { return umidade; }
    public void setUmidade(int umidade) { this.umidade = umidade; }
}
