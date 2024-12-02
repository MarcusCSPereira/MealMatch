package com.mealmatch;

import java.util.HashMap;
import java.util.Map;

import com.mealmatch.jdbc.connection.ConnectionFactory;
import com.mealmatch.model.Ingrediente;
import com.mealmatch.model.Receita;
import com.mealmatch.model.ReceitaIngrediente;
import com.mealmatch.utils.TestarConexao;

import java.sql.Connection;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

@SuppressWarnings("unused")
public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {

        TestarConexao.checkInternetConnection();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/tela_login.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/styles/style.css").toExternalForm());

        Image icon = new Image(getClass().getResourceAsStream("/images/icon.png"));
        primaryStage.getIcons().add(icon);

        primaryStage.setTitle("MealMatch");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
