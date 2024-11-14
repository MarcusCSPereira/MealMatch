package com.mealmatch.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class TelaReceitasController implements Initializable{

    @FXML
    private ToggleGroup busca_de_receitas;

    @FXML
    private ImageView close_button;

    @FXML
    private ImageView fav_button;

    @FXML
    private RadioButton ingrediente_toogle;

    @FXML
    private ImageView logout_button;

    @FXML
    private RadioButton nomeReceita_toggle;

    @FXML
    private ImageView profile_button;

    @FXML
    private ListView<?> receitas_listview;

    @FXML
    private ImageView search_button;

    @FXML
    private TextField search_field;

    @FXML
    private AnchorPane screen;

    Scene scene;
    Stage stage;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
      Platform.runLater(() -> screen.requestFocus());
    }

    @FXML
    void acessar_favoritas(MouseEvent event) throws IOException {
      FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/tela_favoritas.fxml"));
      Parent root = loader.load();
      scene = new Scene(root);
      stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
      stage.setScene(scene);
      stage.show();
    }

    @FXML
    void acessar_perfil(MouseEvent event) throws IOException {
      FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/tela_perfil.fxml"));
      Parent root = loader.load();
      scene = new Scene(root);
      stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
      stage.setScene(scene);
      stage.show();
    }

    @FXML
    void buscar(MouseEvent event) {

    }

    @FXML
    void fechar_programa() {
      System.exit(0);
    }

    @FXML
    void sair_conta(MouseEvent event) throws IOException {
      FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/tela_login.fxml"));
      Parent root = loader.load();
      scene = new Scene(root);
      stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
      stage.setScene(scene);
      stage.show();
    }

}
