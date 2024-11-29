package com.mealmatch.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import com.mealmatch.jdbc.dao.UserDAO;
import com.mealmatch.jdbc.database.ConnectionFactory;
import com.mealmatch.model.User;
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
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class TelaPerfilController implements Initializable {

  @FXML
  private Button alterar_senha_button;

  @FXML
  private ImageView back_button_image;

  @FXML
  private ImageView close_button_image;

  @FXML
  private Label email_label;

  @FXML
  private ImageView no_view_senha_image;

  @FXML
  private Label nome_completo_label;

  @FXML
  private Label nome_usuario_label;

  @FXML
  private Label numero_receitas_criadas_label;

  @FXML
  private ImageView profile_button_image;

  @FXML
  private PasswordField senha_passwordfield;

  @FXML
  private TextField senha_textfield;

  @FXML
  private ImageView view_senha_image;

  @FXML
  private AnchorPane screen;

  Scene scene;
  Stage stage;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    Platform.runLater(() -> {
      screen.requestFocus();
      // Recupera o ID do usuário logado
      Integer userId = ControleDeSessao.getInstance().getUserId();

      if (userId != null) {
        // Busca os dados do usuário e preenche os campos
        UserDAO userDao = new UserDAO(ConnectionFactory.getConnection());
        User user = userDao.getUserById(userId);

        if (user != null) {
          nome_completo_label.setText(user.getCompleteName());
          nome_usuario_label.setText(user.getUsername());
          email_label.setText(user.getEmail());
        }
      }
    });
  }

  @FXML
  void alterar_senha(ActionEvent event) {
    Integer userId = ControleDeSessao.getInstance().getUserId();
    if (userId != null) {
      String novaSenha = senha_passwordfield.getText();
      if (checkPassword(novaSenha)) {
        // Atualiza a senha no banco de dados
        UserDAO userDao = new UserDAO(ConnectionFactory.getConnection());
        boolean success = userDao.updatePassword(userId, novaSenha);

        if (success) {
          Alert alert = new Alert(Alert.AlertType.INFORMATION);
          alert.setTitle("Sucesso");
          alert.setHeaderText(null);
          alert.setContentText("Senha alterada com sucesso!");
          alert.showAndWait();
        } else {
          Alert alert = new Alert(Alert.AlertType.ERROR);
          alert.setTitle("Erro");
          alert.setHeaderText("Erro ao alterar a senha");
          alert.setContentText("Não foi possível alterar a senha.");
          alert.showAndWait();
        }
      }
    }
  }

  @FXML
  void escolher_imagem_perfil(MouseEvent event) {

  }

  @FXML
  void fechar_programa(MouseEvent event) {
    System.exit(0);
  }

  @FXML
  void mostrar_senha(MouseEvent event) {
    String senha_usuario = senha_passwordfield.getText();
    senha_textfield.setText(senha_usuario);
    senha_passwordfield.setVisible(false);
    senha_textfield.setVisible(true);
    view_senha_image.setVisible(false);
    no_view_senha_image.setVisible(true);
  }

  @FXML
  void ocultar_senha(MouseEvent event) {
    String senha_usuario = senha_textfield.getText();
    senha_passwordfield.setText(senha_usuario);
    senha_textfield.setVisible(false);
    senha_passwordfield.setVisible(true);
    no_view_senha_image.setVisible(false);
    view_senha_image.setVisible(true);
  }

  @FXML
  void voltar_tela(MouseEvent event) throws IOException {
    FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/tela_receitas.fxml"));
    Parent root = loader.load();
    scene = new Scene(root);
    stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
    stage.setScene(scene);
    stage.show();
  }

  private Boolean checkPassword(String novaSenha) {
    
    // Verifica se a senha tem pelo menos 8 caracteres
    if (novaSenha.length() < 8) {
      Alert alert = new Alert(Alert.AlertType.ERROR);
      alert.setTitle("Erro");
      alert.setHeaderText("A senha deve ter pelo menos 8 caracteres");
      alert.showAndWait();
      return false;
    }

    // Verifica se a senha contém pelo menos uma letra maiúscula
    if (!novaSenha.matches(".*[A-Z].*")) {
      Alert alert = new Alert(Alert.AlertType.ERROR);
      alert.setTitle("Erro");
      alert.setHeaderText("A senha deve conter pelo menos uma letra maiúscula");
      alert.showAndWait();
      return false;
    }

    // Verifica se a senha contém pelo menos um número
    if (!novaSenha.matches(".*[0-9].*")) {
      Alert alert = new Alert(Alert.AlertType.ERROR);
      alert.setTitle("Erro");
      alert.setHeaderText("A senha deve conter pelo menos um número");
      alert.showAndWait();
      return false;
    }

    // Verifica se a senha contém pelo menos um caractere especial
    if (!novaSenha.matches(".*[!@#$%^&*()-+=<>?/].*")) {
      Alert alert = new Alert(Alert.AlertType.ERROR);
      alert.setTitle("Erro");
      alert.setHeaderText("A senha deve conter pelo menos um caractere especial");
      alert.showAndWait();
      return false;
    }

    // Caso todas as verificações sejam aprovadas
    return true;
  }

}
