package com.mealmatch.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class TelaCadastroController implements Initializable {

    @FXML
    private TextField age_user;

    @FXML
    private TextField complete_username;

    @FXML
    private PasswordField confirm_password;

    @FXML
    private TextField email_user;

    @FXML
    private RadioButton female_user;

    @FXML
    private RadioButton male_user;

    @FXML
    private PasswordField password;

    @FXML
    private ProgressBar register_progress;

    @FXML
    private ToggleGroup sexo;

    @FXML
    private TextField username;

    private final int TOTAL_FIELDS = 7; // Total de campos que precisam ser preenchidos
    private double progressStep = 1.0 / TOTAL_FIELDS;
    
    private double progress = 0;

    Stage stage;
    Scene scene;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Aqui são os listeners para monitorar cada campo e atualizar a progress bar
        addFieldListener(complete_username);
        addFieldListener(email_user);
        addFieldListener(username);
        addFieldListener(age_user);
        addPasswordFieldListener(password);
        addPasswordFieldListener(confirm_password);

        // Listener para o grupo de RadioButtons (sexo)
        sexo.selectedToggleProperty().addListener((observable, oldValue, newValue) -> updateProgress());
    }

    private void addFieldListener(TextField textField) {
        textField.textProperty().addListener((observable, oldValue, newValue) -> updateProgress());
    }

    private void addPasswordFieldListener(PasswordField passwordField) {
        passwordField.textProperty().addListener((observable, oldValue, newValue) -> updateProgress());
    }

    private void updateProgress() {
        int filledFields = 0;

        if (!complete_username.getText().isEmpty()) filledFields++;
        if (!email_user.getText().isEmpty()) filledFields++;
        if (!username.getText().isEmpty()) filledFields++;
        if (!age_user.getText().isEmpty()) filledFields++;
        if (!password.getText().isEmpty()) filledFields++;
        if (!confirm_password.getText().isEmpty()) filledFields++;
        if (sexo.getSelectedToggle() != null) filledFields++;

        progress = filledFields * progressStep;
        register_progress.setProgress(progress);
    }

    @FXML
    void cadastrar_usuario(ActionEvent event) {
        // Aqui vamos adicionar a lógica para verificar se todos os campos foram preenchidos corretamente, validar a senha e outros detalhes antes de salvar no banco de dados!
    }

    @FXML
    void fechar_programa() {
        System.exit(0);
    }

    @FXML
    void voltar_para_login(MouseEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/tela_login.fxml"));
        Parent root = loader.load();
        scene = new Scene(root);
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

}