package com.mealmatch.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import com.mealmatch.utils.ControleDeSessao;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class LicenseController implements Initializable{

    Stage stage;
    Scene scene;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        
    }

    @FXML
    private void fechar(ActionEvent event) throws IOException{
        ControleDeSessao controleDeSessao = ControleDeSessao.getInstance();
        controleDeSessao.clearSession();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/tela_cadastro.fxml"));
        Parent root = loader.load();
        scene = new Scene(root);
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

}
