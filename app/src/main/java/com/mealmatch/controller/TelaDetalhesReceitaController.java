package com.mealmatch.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import com.mealmatch.model.NutrienteValor;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class TelaDetalhesReceitaController implements Initializable {

  @FXML
  private TableView<NutrienteValor> tabelaNutricional;

  @FXML
  private TableColumn<NutrienteValor, String> nutrientes_column;

  @FXML
  private TableColumn<NutrienteValor, String> valores_column;

  @FXML
  private RadioButton acucar_toggle;

  @FXML
  private ImageView back_button_image;

  @FXML
  private ImageView clobe_button_image;

  @FXML
  private RadioButton frutos_do_mar_toggle;

  @FXML
  private RadioButton gluten_toggle;

  @FXML
  private Rectangle gluten_toogle;

  @FXML
  private RadioButton lactose_toggle;

  @FXML
  private Label nome_receita_label;

  @FXML
  private ImageView receita_image;

  @FXML
  private RadioButton vegana_toggle;

  @FXML
  private RadioButton vegetariana_toggle;

  private ObservableList<NutrienteValor> dadosNutricionais;

  Stage stage;
  Scene scene;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    // Configurando as colunas
    nutrientes_column.setCellValueFactory(new PropertyValueFactory<>("nutriente"));
    valores_column.setCellValueFactory(new PropertyValueFactory<>("valor"));

    // Criando dados
    dadosNutricionais = FXCollections.observableArrayList(
        new NutrienteValor("Proteínas", "50 g"),
        new NutrienteValor("Carboidratos", "30 g"),
        new NutrienteValor("Gorduras", "20 g"),
        new NutrienteValor("Calorias", "40000 kcal"));
    // Adicionando os dados na tabela
    tabelaNutricional.setItems(dadosNutricionais);
  }

  @FXML
  void fechar_programa(MouseEvent event) {
    System.exit(0);
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
}