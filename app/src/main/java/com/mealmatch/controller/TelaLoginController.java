package com.mealmatch.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class TelaLoginController implements Initializable{

    @FXML
    private Hyperlink cadastro_hlk;

    @FXML
    private ImageView close_btt;

    @FXML
    private PasswordField field_senha;

    @FXML
    private TextField field_usuario;

    @FXML
    private Label label01;

    @FXML
    private Button login_btt;

    @FXML
    private AnchorPane screen;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
      Platform.runLater(() -> screen.requestFocus());
    }

    Scene scene;
    Stage stage;

    @FXML
    public void login (ActionEvent event) throws IOException {
        String nomeUsuario = field_usuario.getText();
        String senha = field_senha.getText();

        if (validarLogin(nomeUsuario, senha)) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/tela_receitas.fxml"));
            Parent root = loader.load();
            scene = new Scene(root);
            stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } else {
            System.out.println("Usuário ou senha inválidos!");
        }
    }

    @FXML
    public void ir_para_cadastro (ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/tela_cadastro.fxml"));
        Parent root = loader.load();
        scene = new Scene(root);
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    public void fechar_programa () {
        System.exit(0);
    }

    private boolean validarLogin(String nomeUsuario, String senha) {
        return nomeUsuario.equals("admin") && senha.equals("admin");
    }
}
