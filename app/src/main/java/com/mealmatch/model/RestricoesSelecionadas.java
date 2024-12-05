package com.mealmatch.model;

import java.util.ArrayList;
import java.util.List;

import com.mealmatch.enums.RestricaoEnum;

public class RestricoesSelecionadas {

    private static RestricoesSelecionadas instance;
    private List<RestricaoEnum> restricoes;

    private RestricoesSelecionadas() {
        restricoes = new ArrayList<>();
    }

    public static RestricoesSelecionadas getInstance() {
        if (instance == null) {
            instance = new RestricoesSelecionadas();
        }
        return instance;
    }

    public List<RestricaoEnum> getRestricoes() {
        return restricoes;
    }

    public void setRestricoes(List<RestricaoEnum> restricoes) {
        this.restricoes = restricoes;
    }
}