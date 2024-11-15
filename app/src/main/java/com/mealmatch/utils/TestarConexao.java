package com.mealmatch.utils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class TestarConexao {

    // Método para verificar a conexão com a internet
    public static boolean checkInternetConnection() {
        try {
          URL url = new URL("http://www.google.com");
          HttpURLConnection connection = (HttpURLConnection) url.openConnection();
          connection.setRequestMethod("HEAD");
          connection.setConnectTimeout(3000);
          connection.connect();
          return connection.getResponseCode() == 200;
        } catch (IOException e) {
          showAlertNoInternet();
          System.exit(0);
          return false;
        }
    }

    // Método para exibir alerta de erro
    private static void showAlertNoInternet() {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Conexão com a Internet");
        alert.setHeaderText("Sem conexão com a internet");
        alert.setContentText("Verifique sua conexão com a internet e tente novamente.");
        alert.showAndWait();
    }
}