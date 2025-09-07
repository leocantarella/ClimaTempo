package com.climatempo.model;


import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name="observacao_clima",
        indexes=@Index(name="idx_cidade_observada", columnList="cidade_id, observado_em DESC"))
public class ObservacaoClima {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Cidade cidade;

    @Column(name = "observado_em", nullable = false)
    private LocalDateTime observadoEm;

    @Column(name = "temp_c", nullable = false)
    private BigDecimal tempC;

    @Column(name = "sensacao_termica", nullable = false)
    private BigDecimal sensacaoTerm;

    @Column(name = "umidade", nullable = false)
    private int umidade;


    public ObservacaoClima(Long id, Cidade cidade, LocalDateTime observadoEm, BigDecimal tempC, BigDecimal sensacaoTerm, int umidade) {
        this.id = id;
        this.cidade = cidade;
        this.observadoEm = observadoEm;
        this.tempC = tempC;
        this.sensacaoTerm = sensacaoTerm;
        this.umidade = umidade;
    }

    public ObservacaoClima() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Cidade getCidade() {
        return cidade;
    }

    public void setCidade(Cidade cidade) {
        this.cidade = cidade;
    }

    public LocalDateTime getObservadoEm() {
        return observadoEm;
    }

    public void setObservadoEm(LocalDateTime observadoEm) {
        this.observadoEm = observadoEm;
    }

    public BigDecimal getTempC() {
        return tempC;
    }

    public void setTempC(BigDecimal tempC) {
        this.tempC = tempC;
    }

    public BigDecimal getSensacaoTerm() {
        return sensacaoTerm;
    }

    public void setSensacaoTerm(BigDecimal sensacaoTerm) {
        this.sensacaoTerm = sensacaoTerm;
    }

    public int getUmidade() {
        return umidade;
    }

    public void setUmidade(int umidade) {
        this.umidade = umidade;
    }
}
