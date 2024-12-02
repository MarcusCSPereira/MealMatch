package com.mealmatch.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import com.mealmatch.jdbc.connection.ConnectionFactory;
import com.mealmatch.jdbc.dao.UserDAO;
import com.mealmatch.utils.EmailSender;
import com.mealmatch.utils.ControleDeSessao;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class TelaLoginController implements Initializable {

  @FXML
  private Hyperlink cadastro_hlk;

  @FXML
  private Hyperlink esqueci_senha_hlk;

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

  Scene scene;
  Stage stage;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    Platform.runLater(() -> screen.requestFocus());

    // Mostra ou oculta o link "Esqueci a Senha" com base no campo de usuário
    field_usuario.textProperty().addListener((observable, oldValue, newValue) -> controleEsqueciASenha(field_usuario));

    // Quando o usuario der ENTER no campo de usuario, muda o foco para o campo de
    // senha
    field_usuario.setOnKeyPressed(event -> {
      switch (event.getCode()) {
        case ENTER:
          field_senha.requestFocus();
          break;
        default:
          break;
      }
    });

    // Quando o usuario der ENTER no campo de senha, tenta fazer o Login
    field_senha.setOnKeyPressed(event -> {
      switch (event.getCode()) {
        case ENTER:
          try {
            login(new ActionEvent(field_senha, field_senha));
          } catch (IOException e) {
            e.printStackTrace();
          } // Chama o método de login
          break;
        default:
          break;
      }
    });
  }

  private void controleEsqueciASenha(TextField field_usuario) {
    if (field_usuario.getText().isEmpty()) {
      Platform.runLater(() -> esqueci_senha_hlk.setVisible(false));
    } else {
      Platform.runLater(() -> esqueci_senha_hlk.setVisible(true));
    }
  }

  @FXML
  public void login(ActionEvent event) throws IOException {
    String nomeUsuario = field_usuario.getText();
    String senha = field_senha.getText();

    if (validarLogin(nomeUsuario, senha)) {
      UserDAO userDao = new UserDAO(ConnectionFactory.getConnection()); // Certifique-se de passar a conexão
      Integer userId = userDao.getUserIdByUsername(nomeUsuario);
      // Armazena o ID no SessionManager
      ControleDeSessao.getInstance().setUserId(userId);
      FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/tela_receitas.fxml"));
      Parent root = loader.load();
      scene = new Scene(root);
      stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
      stage.setScene(scene);
      stage.show();
    }
  }

  @FXML
  public void solicitar_senha() {
    // Obtém o e-mail do usuário a partir do banco de dados
    String recipientEmail = getEmail(field_usuario.getText());

    if (recipientEmail == null || recipientEmail.isEmpty()) {
      // Alerta de erro caso o e-mail não seja encontrado
      Alert alert = new Alert(Alert.AlertType.ERROR);
      alert.setTitle("Erro");
      alert.setHeaderText("Usuário não encontrado");
      alert.setContentText("Não foi possível encontrar um e-mail cadastrado para o usuário informado.");
      alert.showAndWait();
      return;
    }

    // Cria e inicia a thread para envio do e-mail
    EmailSender emailSender = new EmailSender(recipientEmail);
    Thread emailThread = new Thread(emailSender);
    emailThread.setDaemon(true); // Permite que a aplicação seja fechada mesmo com a thread em execução
    emailThread.start();

    // Alerta informando que o e-mail está sendo enviado
    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    alert.setTitle("Recuperação de Senha");
    alert.setHeaderText(null);
    alert.setContentText("Um e-mail de recuperação de senha está sendo enviado para o e-mail cadastrado.");
    alert.showAndWait();
  }

  // Simula a busca do e-mail no banco de dados com base no nome do usuário
  private String getEmail(String nomeUsuario) {
    UserDAO userDao = new UserDAO(ConnectionFactory.getConnection()); // Certifique-se de passar a conexão
    try {
      return userDao.buscaEmail(nomeUsuario);
    } catch (RuntimeException e) {
      return null;
    }
  }

  @FXML
  public void ir_para_cadastro(ActionEvent event) throws IOException {
    FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/tela_cadastro.fxml"));
    Parent root = loader.load();
    scene = new Scene(root);
    stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
    stage.setScene(scene);
    stage.show();
  }

  @FXML
  public void fechar_programa() {
    System.exit(0);
  }

  public boolean validarLogin(String nomeUsuario, String senha) {
    UserDAO userDao = new UserDAO(ConnectionFactory.getConnection()); // Certifique-se de passar a conexão

    if (userDao.validarLogin(nomeUsuario, senha)) {
      return true;
    } else {
      Alert alert = new Alert(Alert.AlertType.ERROR);
      alert.setTitle("Erro");
      alert.setHeaderText("Login inválido");
      alert.setContentText("Usuário ou senha incorretos.");
      alert.showAndWait();
      return false;
    }
  }
}
