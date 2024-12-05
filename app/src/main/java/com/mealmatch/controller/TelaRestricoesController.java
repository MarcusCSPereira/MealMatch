package com.mealmatch.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import com.mealmatch.enums.RestricaoEnum;
import com.mealmatch.model.RestricoesSelecionadas;

public class TelaRestricoesController implements Initializable {

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

  Stage stage;

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

    // Atualizar o estado no Singleton
    RestricoesSelecionadas.getInstance().setRestricoes(restricoesSelecionadas);

    // Exibe as restrições selecionadas (para debug)
    System.out.println("Restrições selecionadas: " + restricoesSelecionadas);
  }

  // Método público para recuperar as restrições selecionadas
  public List<RestricaoEnum> getRestricoesSelecionadas() {
    return restricoesSelecionadas;
  }

  @FXML
  void aplicar_filtros(ActionEvent event) {
    stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
    stage.close();
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    // Método chamado quando a tela é inicializada
    // Carregar restrições previamente salvas no Singleton
    List<RestricaoEnum> restricoesSelecionadas = RestricoesSelecionadas.getInstance().getRestricoes();

    // Atualizar os checkboxes com base nas restrições salvas
    acucarCheck.setSelected(restricoesSelecionadas.contains(RestricaoEnum.ACUCAR));
    frutosDoMarCheck.setSelected(restricoesSelecionadas.contains(RestricaoEnum.FRUTOS_DO_MAR));
    glutenCheck.setSelected(restricoesSelecionadas.contains(RestricaoEnum.GLUTEN));
    lactoseCheck.setSelected(restricoesSelecionadas.contains(RestricaoEnum.LACTOSE));
    veganaCheck.setSelected(restricoesSelecionadas.contains(RestricaoEnum.VEGANA));
    vegetarianaCheck.setSelected(restricoesSelecionadas.contains(RestricaoEnum.VEGETARIANA));

  }
}
