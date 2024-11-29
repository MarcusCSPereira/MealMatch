package com.mealmatch.utils;

import javafx.application.Platform;
import javafx.scene.control.Alert;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.SimpleEmail;

import com.mealmatch.jdbc.dao.UserDAO;
import com.mealmatch.jdbc.database.ConnectionFactory;

import io.github.cdimascio.dotenv.Dotenv;

public class EmailSender implements Runnable {

  private final String recipientEmail;

  public EmailSender(String recipientEmail) {
    this.recipientEmail = recipientEmail;
  }

  private static Dotenv dotenv = Dotenv.load();

  @Override
  public void run() {
    UserDAO userDao = new UserDAO(ConnectionFactory.getConnection());

    String email = "mealmatch.server@gmail.com"; // E-mail do servidor
    String senha = dotenv.get("EMAIL_PASSWORD_APP");
    String subject = "Recuperação de senha do aplicativo MealMatch";
    String message = "Sua nova senha é: " + userDao.resetPassword(recipientEmail) + "\n\n"
        + "Por favor, altere sua senha assim que possível.";

    try {
      SimpleEmail simpleEmail = new SimpleEmail();
      simpleEmail.setHostName("smtp.gmail.com");
      simpleEmail.setSmtpPort(465);
      simpleEmail.setAuthenticator(new DefaultAuthenticator(email, senha));
      simpleEmail.setSSLOnConnect(true);
      simpleEmail.setFrom(email);
      simpleEmail.setSubject(subject);
      simpleEmail.setMsg(message);
      simpleEmail.addTo(recipientEmail);
      simpleEmail.send();

      /*
       * Alerta de sucesso
       * Platform.runLater(() -> {
       * Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
       * successAlert.setTitle("Sucesso");
       * successAlert.setHeaderText(null);
       * successAlert.setContentText("E-mail enviado com sucesso para: " +
       * recipientEmail);
       * successAlert.showAndWait();
       * });
       */
    } catch (Exception e) {
      e.printStackTrace();
      // Alerta de erro
      Platform.runLater(() -> {
        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
        errorAlert.setTitle("Erro");
        errorAlert.setHeaderText("Erro ao enviar o e-mail");
        errorAlert.setContentText("Não foi possível enviar o e-mail para: " + recipientEmail);
        errorAlert.showAndWait();
      });
    }
  }
}