package com.climatempo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name = "cidade", uniqueConstraints = @UniqueConstraint(columnNames = {"name"}))
public class Cidade {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nome;

    private Double lat;
    private Double lon;


    public Cidade(Long id, String nome, Double lat, Double lon) {
        this.id = id;
        this.nome = nome;
        this.lat = lat;
        this.lon = lon;
    }

    public Cidade() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLon() {
        return lon;
    }

    public void setLon(Double lon) {
        this.lon = lon;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
}
