package com.mealmatch;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

//import java.sql.Connection;

//import com.mealmatch.jdbc.database.ConnectionFactory;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {

        //Por enquanto, não use a conexão com o banco de dados, para não gastar o banco de dados.
        //Connection connection = ConnectionFactory.getConnection();
        //connection.close();

        if (!checkInternetConnection()) {
          showAlertNoInternet();
          return;
        }

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

    private boolean checkInternetConnection() {
      try {
        URL url = new URL("http://www.google.com");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("HEAD");
        connection.setConnectTimeout(3000); // Timeout de 3 segundos
        connection.connect();
        return (connection.getResponseCode() == HttpURLConnection.HTTP_OK);
      } catch (IOException e) {
        return false;
      }
    }

    private void showAlertNoInternet() {
      Alert alert = new Alert(AlertType.ERROR);
      alert.setTitle("Conexão com a Internet");
      alert.setHeaderText("Sem conexão com a internet");
      alert.setContentText("Verifique sua conexão com a internet e tente novamente.");
      alert.showAndWait();
    }
}
