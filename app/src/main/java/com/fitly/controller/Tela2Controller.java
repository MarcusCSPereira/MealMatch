package com.fitly.controller;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class Tela2Controller implements Initializable {

  @FXML
  private Button btt02;

  public void voltar(ActionEvent event) throws Exception {
    FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/tela.fxml"));
    Parent root = loader.load();
    Scene scene = new Scene(root);

    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
    stage.setScene(scene);
    stage.show();
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {

  }
  
}
