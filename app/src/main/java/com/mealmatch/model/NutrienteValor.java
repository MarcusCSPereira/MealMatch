package com.mealmatch.model;

public class NutrienteValor {
    private String nutriente;
    private String valor;

    public NutrienteValor(String nutriente, String valor) {
        this.nutriente = nutriente;
        this.valor = valor;
    }

    public String getNutriente() {
        return nutriente;
    }

    public void setNutriente(String nutriente) {
        this.nutriente = nutriente;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }
}