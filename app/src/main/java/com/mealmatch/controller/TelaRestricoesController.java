package com.mealmatch.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;

import java.util.ArrayList;
import java.util.List;

import com.mealmatch.enums.RestricaoEnum;

public class TelaRestricoesController {

    @FXML
    private CheckBox acucarCheck;

    @FXML
    private CheckBox frutosDoMarCheck;

    @FXML
    private CheckBox glutenCheck;

    @FXML
    private CheckBox lactoseCheck;

    @FXML
    private CheckBox veganaCheck;

    @FXML
    private CheckBox vegetarianaCheck;

    // Lista para armazenar as restrições selecionadas
    private List<RestricaoEnum> restricoesSelecionadas = new ArrayList<>();

    @FXML
    void definirRestricoes(ActionEvent event) {
        // Limpar as restrições selecionadas antes de adicionar as novas
        restricoesSelecionadas.clear();

        // Adicionar as restrições selecionadas
        if (acucarCheck.isSelected()) {
            restricoesSelecionadas.add(RestricaoEnum.ACUCAR);
        }
        if (frutosDoMarCheck.isSelected()) {
            restricoesSelecionadas.add(RestricaoEnum.FRUTOS_DO_MAR);
        }
        if (glutenCheck.isSelected()) {
            restricoesSelecionadas.add(RestricaoEnum.GLUTEN);
        }
        if (lactoseCheck.isSelected()) {
            restricoesSelecionadas.add(RestricaoEnum.LACTOSE);
        }
        if (veganaCheck.isSelected()) {
            restricoesSelecionadas.add(RestricaoEnum.VEGANA);
        }
        if (vegetarianaCheck.isSelected()) {
            restricoesSelecionadas.add(RestricaoEnum.VEGETARIANA);
        }

        // Exibe as restrições selecionadas (para debug)
        System.out.println("Restrições selecionadas: " + restricoesSelecionadas);
    }

    // Método público para recuperar as restrições selecionadas
    public List<RestricaoEnum> getRestricoesSelecionadas() {
        return restricoesSelecionadas;
    }
}
