package com.mealmatch.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;

import java.util.ArrayList;
import java.util.List;

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

    @FXML
    void definirRestricoes(ActionEvent event) {
        // Lista para armazenar as restrições selecionadas
        List<String> restricoesSelecionadas = new ArrayList<>();

        // Verifica cada CheckBox e adiciona a restrição caso esteja marcada
        if (acucarCheck.isSelected()) {
            restricoesSelecionadas.add("Açúcar");
        }
        if (frutosDoMarCheck.isSelected()) {
            restricoesSelecionadas.add("Frutos do Mar");
        }
        if (glutenCheck.isSelected()) {
            restricoesSelecionadas.add("Glúten");
        }
        if (lactoseCheck.isSelected()) {
            restricoesSelecionadas.add("Lactose");
        }
        if (veganaCheck.isSelected()) {
            restricoesSelecionadas.add("Vegana");
        }
        if (vegetarianaCheck.isSelected()) {
            restricoesSelecionadas.add("Vegetariana");
        }

        // Exibe as restrições selecionadas (para debug)
        System.out.println("Restrições selecionadas: " + restricoesSelecionadas);

        // Chamada para armazenar ou processar as restrições
        armazenarRestricoes(restricoesSelecionadas);
    }

    private void armazenarRestricoes(List<String> restricoes) {
        // Lógica para armazenar ou processar as restrições selecionadas
        System.out.println("Restrições armazenadas: " + restricoes);
    }
}
