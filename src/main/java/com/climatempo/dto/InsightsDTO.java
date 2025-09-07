package com.climatempo.dto;

public class InsightsDTO {
    private String confortoTermico;
    private String hidratacao;
    private String alertaVento;
    private String horariosRecomendados;

    public InsightsDTO() {}

    public InsightsDTO(String confortoTermico, String hidratacao,
                       String alertaVento, String horariosRecomendados) {
        this.confortoTermico = confortoTermico;
        this.hidratacao = hidratacao;
        this.alertaVento = alertaVento;
        this.horariosRecomendados = horariosRecomendados;
    }

    public String getConfortoTermico() { return confortoTermico; }
    public void setConfortoTermico(String confortoTermico) { this.confortoTermico = confortoTermico; }

    public String getHidratacao() { return hidratacao; }
    public void setHidratacao(String hidratacao) { this.hidratacao = hidratacao; }

    public String getAlertaVento() { return alertaVento; }
    public void setAlertaVento(String alertaVento) { this.alertaVento = alertaVento; }

    public String getHorariosRecomendados() { return horariosRecomendados; }
    public void setHorariosRecomendados(String horariosRecomendados) { this.horariosRecomendados = horariosRecomendados; }
}
