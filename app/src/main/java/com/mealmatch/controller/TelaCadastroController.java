package com.mealmatch.controller;

import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;

import com.mealmatch.jdbc.connection.ConnectionFactory;
import com.mealmatch.jdbc.dao.UserDAO;
import com.mealmatch.model.User;
import com.mealmatch.model.User.Sex;
import com.mealmatch.utils.EncriptadorDeSenhas;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class TelaCadastroController implements Initializable {

  @FXML
  private Hyperlink linkTermos;

  @FXML
  private CheckBox checkTermos;

  @FXML
  private DatePicker date_picker;

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

  @FXML
  private Label verificaSenhas_label;

  @FXML
  private AnchorPane screen;

  ImageView imageView = new ImageView();

  // ########################################################################
  private final int TOTAL_FIELDS = 7;
  private double progressStep = 1.0 / TOTAL_FIELDS;

  private double progress = 0;

  Stage stage;
  Scene scene;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    Platform.runLater(() -> screen.requestFocus());

    controleProgressBar(complete_username, email_user, username, date_picker, password, confirm_password, sexo);

    controleSenhasIguais(password, confirm_password);

  }

  @FXML
  private void linkTermos(ActionEvent event) throws IOException{
    FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/tela_licenca.fxml"));
      Parent root = loader.load();
      scene = new Scene(root);
      stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
      //stage.hide();
      Stage novoStage = new Stage();

      novoStage.setOnHidden(e -> {
  
        stage.show();
      });

      novoStage.initOwner(stage);
      novoStage.initModality(Modality.WINDOW_MODAL);
      novoStage.setScene(scene);
      novoStage.showAndWait();

  }

  private void controleProgressBar(TextField complete_username, TextField email_user, TextField username,
      DatePicker date_picker, PasswordField password, PasswordField confirm_password, ToggleGroup sexo) {
    // Aqui são os listeners para monitorar cada campo e atualizar a progress bar
    addFieldListener(complete_username);
    addFieldListener(email_user);
    addFieldListener(username);
    addFieldListener(date_picker.getEditor());
    addPasswordFieldListener(password);
    addPasswordFieldListener(confirm_password);
    // Listener para o grupo de RadioButtons (sexo)
    sexo.selectedToggleProperty().addListener((observable, oldValue, newValue) -> updateProgressBar());
  }

  private void addFieldListener(TextField textField) {
    textField.textProperty().addListener((observable, oldValue, newValue) -> updateProgressBar());
  }

  private void addPasswordFieldListener(PasswordField passwordField) {
    passwordField.textProperty().addListener((observable, oldValue, newValue) -> updateProgressBar());
  }

  private void updateProgressBar() {
    int filledFields = 0;

    if (!complete_username.getText().isEmpty())
      filledFields++;
    if (!email_user.getText().isEmpty())
      filledFields++;
    if (!username.getText().isEmpty())
      filledFields++;
    if (!date_picker.getEditor().getText().isEmpty())
      filledFields++;
    if (!password.getText().isEmpty())
      filledFields++;
    if (!confirm_password.getText().isEmpty())
      filledFields++;
    if (sexo.getSelectedToggle() != null)
      filledFields++;

    progress = filledFields * progressStep;
    register_progress.setProgress(progress);
  }

  private void controleSenhasIguais(PasswordField password, PasswordField confirm_password) {
    password.textProperty().addListener(
        (observable, oldValue, newValue) -> checaSenhasEmTempoReal(password, confirm_password, verificaSenhas_label));
    confirm_password.textProperty().addListener(
        (observable, oldValue, newValue) -> checaSenhasEmTempoReal(password, confirm_password, verificaSenhas_label));
  }

  private void checaSenhasEmTempoReal(PasswordField password, PasswordField confirm_password,
      Label verificaSenhas_label) {
    if (!password.getText().equals(confirm_password.getText())) {
      verificaSenhas_label.setVisible(true);
    } else {
      verificaSenhas_label.setVisible(false);
    }
  }

  @FXML
  void cadastrar_usuario(ActionEvent event) {
    if (checkSexo() == null || !checkEmail(email_user) || !checkAge(date_picker)
        || !checkPassword(password, confirm_password)
        || verificaSenhas_label.isVisible() || !checkCamposVazios()
        || !checkLicense(checkTermos)) {
      return;
    }

    User user = new User();
    user.setCompleteName(complete_username.getText());
    user.setEmail(email_user.getText());
    user.setUsername(username.getText());
    user.setPassword(EncriptadorDeSenhas.hashPassword(password.getText()));
    user.setDate(Date.valueOf(date_picker.getValue()));
    user.setSex(checkSexo());

    // Salvar no banco de dados
    try {
      UserDAO userDAO = new UserDAO(ConnectionFactory.getConnection());
      userDAO.save(user);
    } catch (SQLException e) {
      e.printStackTrace();
      System.out.println("Erro ao salvar usuário no banco de dados");
    }

    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    alert.setTitle("Cadastro realizado");
    alert.setHeaderText("Usuário cadastrado com sucesso");
    alert.showAndWait();
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

  private Boolean checkCamposVazios(){
    if (progress < 1){
      Alert alert = new Alert(Alert.AlertType.ERROR);
      alert.setTitle("Erro");
      alert.setHeaderText("Preencha todos os campos");
      alert.showAndWait();
      return false;
    } else {
      return true;
    }
  }

  private Sex checkSexo() {
    RadioButton selectedRadioButton = (RadioButton) sexo.getSelectedToggle();
    String toggleGroupValue = selectedRadioButton.getText();
    if (toggleGroupValue.equals("Masculino")) {
      return Sex.M;
    } else if (toggleGroupValue.equals("Feminino")) {
      return Sex.F;
    } else {
      Alert alert = new Alert(Alert.AlertType.ERROR);
      alert.setTitle("Erro");
      alert.setHeaderText("Selecione um sexo");
      alert.showAndWait();
      return null;
    }
  }

  private Boolean checkLicense(CheckBox checkTermos){
    if(checkTermos.isSelected()){
      return true;
    } else {
      Alert alert = new Alert(Alert.AlertType.ERROR);
      alert.setTitle("Atenção!");
      alert.setHeaderText("Concordar com os Termos de Servico é Obrigatório!");
      alert.showAndWait();
      return false;
    }
  }

  private Boolean checkEmail(TextField email) {
    if (email.getText().contains("@") && email.getText().contains(".")) {
      return true;
    } else {
      Alert alert = new Alert(Alert.AlertType.ERROR);
      alert.setTitle("Erro");
      alert.setHeaderText("Email inválido");
      alert.showAndWait();
      return false;
    }
  }

  private Boolean checkAge(DatePicker datePicker) {
    // Verifica se a data está preenchida
    if (datePicker.getValue() == null) {
      Alert alert = new Alert(Alert.AlertType.ERROR);
      alert.setTitle("Erro");
      alert.setHeaderText("Data de nascimento não pode estar vazia");
      alert.showAndWait();
      return false;
    }

    // Data selecionada pelo usuário
    LocalDate birthDate = datePicker.getValue();

    // Data atual
    LocalDate currentDate = LocalDate.now();

    // Calcula a idade
    long age = java.time.temporal.ChronoUnit.YEARS.between(birthDate, currentDate);

    // Verifica se a idade é menor que 5 anos
    if (age < 5) {
      Alert alert = new Alert(Alert.AlertType.ERROR);
      alert.setTitle("Erro");
      alert.setHeaderText("O usuário deve ter pelo menos 5 anos de idade");
      alert.showAndWait();
      return false;
    }

    // Se passar pelas validações
    return true;
  }

  private Boolean checkPassword(PasswordField password, PasswordField confirm_password) {
    String passwordText = password.getText();
    String confirmPasswordText = confirm_password.getText();

    // Verifica se as senhas são iguais
    if (!passwordText.equals(confirmPasswordText)) {
      Alert alert = new Alert(Alert.AlertType.ERROR);
      alert.setTitle("Erro");
      alert.setHeaderText("As senhas não coincidem");
      alert.showAndWait();
      return false;
    }

    // Verifica se a senha tem pelo menos 8 caracteres
    if (passwordText.length() < 8) {
      Alert alert = new Alert(Alert.AlertType.ERROR);
      alert.setTitle("Erro");
      alert.setHeaderText("A senha deve ter pelo menos 8 caracteres");
      alert.showAndWait();
      return false;
    }

    // Verifica se a senha contém pelo menos uma letra maiúscula
    if (!passwordText.matches(".*[A-Z].*")) {
      Alert alert = new Alert(Alert.AlertType.ERROR);
      alert.setTitle("Erro");
      alert.setHeaderText("A senha deve conter pelo menos uma letra maiúscula");
      alert.showAndWait();
      return false;
    }

    // Verifica se a senha contém pelo menos um número
    if (!passwordText.matches(".*[0-9].*")) {
      Alert alert = new Alert(Alert.AlertType.ERROR);
      alert.setTitle("Erro");
      alert.setHeaderText("A senha deve conter pelo menos um número");
      alert.showAndWait();
      return false;
    }

    // Verifica se a senha contém pelo menos um caractere especial
    if (!passwordText.matches(".*[!@#$%^&*()-+=<>?/].*")) {
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