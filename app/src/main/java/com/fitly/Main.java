package com.fitly;

import java.sql.Connection;

import com.fitly.jdbc.database.ConnectionFactory;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {

        Connection connection = ConnectionFactory.getConnection();
        connection.close();
        

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/tela.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/styles/style.css").toExternalForm());

        primaryStage.setTitle("Trabalho de Banco De Dados");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
