package com.mealmatch.controller;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import com.mealmatch.enums.DificuldadeEnum;
import com.mealmatch.model.NutrienteValor;
import com.mealmatch.model.Receita;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

@SuppressWarnings("unused")
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
  private RadioButton facil_toggle;

  @FXML
  private RadioButton medio_toggle;

  @FXML
  private RadioButton dificil_toggle;

  @FXML
  private ListView<String> list_view_ingredientes;

  @FXML
  private TextArea text_area_modo_preparo;

  @FXML
  private ImageView back_button_image;

  @FXML
  private ImageView clobe_button_image;

  @FXML
  private RadioButton frutos_do_mar_toggle;

  @FXML
  private RadioButton gluten_toggle;

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

  @FXML
  private AnchorPane screen;

  private ObservableList<NutrienteValor> dadosNutricionais;
  private Receita receita;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    Platform.runLater(() -> screen.requestFocus());
  }

  @FXML
  void fechar_programa(MouseEvent event) {
    System.exit(0);
  }

  @FXML
  void voltar_tela(MouseEvent event) {
    // Obtém a janela atual (tela de detalhes)
    Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
    // Fecha a tela de detalhes
    currentStage.close();
  }

  // Método para preencher a tela com os dados da receita, que é chamado ao clicar
  // no Botão ver mais em um item da lista de receitas
  public void setReceita(Receita item) {
    this.receita = item;
    Platform.runLater(() -> {
      nome_receita_label.setText(item.getNome());
      receita_image.setImage(item.getImagem());
      preencherListaIngredientes(item.getIngredientesFormatados());
      text_area_modo_preparo.setText(item.getModoPreparo());
      preencherDificuldade(item.getDificuldade());
      preencherTabelaNutricional(item);
    });
  }

  // Método auxiliar para preencher a tabela nutricional da receita
  private void preencherTabelaNutricional(Receita item) {
    // Configurando as colunas
    nutrientes_column.setCellValueFactory(new PropertyValueFactory<>("nutriente"));
    valores_column.setCellValueFactory(new PropertyValueFactory<>("valor"));
    dadosNutricionais = FXCollections.observableArrayList(
        new NutrienteValor("Proteínas", item.getTabelaNutricional().getProteina() + " g"),
        new NutrienteValor("Carboidratos", item.getTabelaNutricional().getCarboidrato() + " g"),
        new NutrienteValor("Gorduras", item.getTabelaNutricional().getGordura() + " g"),
        new NutrienteValor("Calorias", item.getTabelaNutricional().getCaloria() + " kcal"));
    // Adicionando os dados na tabela
    tabelaNutricional.setItems(dadosNutricionais);
  }

  // Método auxiliar para preencher a lista de ingredientes, recebendo uma string
  // e formatando-a
  private void preencherListaIngredientes(String ingredientes) {

    if (ingredientes == null || ingredientes.isEmpty()) {
      System.out.println("Nenhum ingrediente fornecido.");
      return;
    }

    // Divide os ingredientes pelo delimitador ";"
    String[] ingredientesArray = ingredientes.split(";");

    List<String> ingredientesFormatados = Arrays.stream(ingredientesArray)
        .map(String::trim) // Remove
        .filter(ingrediente -> !ingrediente.isEmpty())
        .map(ingrediente -> "\u25CF" + " " + capitalizeFirstLetter(ingrediente))
        .collect(Collectors.toList());

    // Adiciona os ingredientes formatados à ListView
    list_view_ingredientes.getItems().clear();
    list_view_ingredientes.getItems().addAll(ingredientesFormatados);
  }

  // Método auxiliar para deixar a primeira letra de uma string em Maiúsculo
  private String capitalizeFirstLetter(String text) {
    if (text == null || text.isEmpty()) {
      return text;
    }
    return text.substring(0, 1).toUpperCase() + text.substring(1).toLowerCase();
  }

  // Método auxiliar para preencher a dificuldade da receita utilizando o Enum
  // correspondente
  private void preencherDificuldade(int dificuldade) {
    try {
      // Converte o valor para o Enum correspondente
      DificuldadeEnum dificuldadeEnum = DificuldadeEnum.fromValor(dificuldade);

      switch (dificuldadeEnum) {
        case FACIL:
          facil_toggle.setSelected(true);
          break;
        case MEDIO:
          medio_toggle.setSelected(true);
          break;
        case DIFICIL:
          dificil_toggle.setSelected(true);
          break;
        default:
          throw new IllegalStateException("Dificuldade desconhecida: " + dificuldadeEnum);
      }
    } catch (IllegalArgumentException e) {
      System.err.println("Erro: " + e.getMessage());
    }
  }

}