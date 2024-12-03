package com.mealmatch.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import com.mealmatch.enums.DificuldadeEnum;
import com.mealmatch.enums.RestricaoEnum;
import com.mealmatch.model.Ingrediente;
import com.mealmatch.model.NutrienteValor;
import com.mealmatch.model.Receita;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class TelaEditarReceitaController implements Initializable {

  @FXML
  private RadioButton acucar_toggle;

  @FXML
  private ComboBox<Integer> horas_box;

  @FXML
  private ComboBox<Integer> minutos_box;

  @FXML
  private ComboBox<Integer> segundos_box;

  @FXML
  private Button salvar;

  @FXML
  private Button adicionar_ingrediente_button;

  @FXML
  private ImageView back_button_image;

  @FXML
  private ImageView clobe_button_image;

  @FXML
  private RadioButton dificil_toggle;

  @FXML
  private ToggleGroup dificuldade_group;

  @FXML
  private TextField nomeIngredienteField;

  @FXML
  private TextField quantidadeIngredienteField;

  @FXML
  private ChoiceBox<String> unidadeDeMedida;

  @FXML
  private RadioButton facil_toggle;

  @FXML
  private RadioButton frutos_do_mar_toggle;

  @FXML
  private RadioButton gluten_toggle;

  @FXML
  private RadioButton lactose_toggle;

  @FXML
  private ListView<String> list_view_ingredientes;

  @FXML
  private RadioButton medio_toggle;

  @FXML
  private Label nome_receita_label;

  @FXML
  private TableColumn<NutrienteValor, String> nutrientes_column;

  @FXML
  private ImageView receita_image;

  @FXML
  private AnchorPane screen;

  @FXML
  private TableView<NutrienteValor> tabelaNutricional;

  @FXML
  private TextArea text_area_modo_preparo;

  @FXML
  private TableColumn<NutrienteValor, String> valores_column;

  @FXML
  private RadioButton vegana_toggle;

  @FXML
  private RadioButton vegetariana_toggle;

  @FXML
  private Button editarNomeButton;

  @FXML
  private TextField pegarNomeReceita;

  private ObservableList<NutrienteValor> dadosNutricionais;

  private String[] unidadesDeMedida = { "g", "ml", "kg", "L" };
  private Receita receita;

  private Integer[] horas = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21,
      22, 23, 24 };

  private Integer[] minutos = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22,
      23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48,
      49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59 };

  private Integer[] segundos = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22,
      23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48,
      49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59 };

  Scene scene;
  Stage stage;

  @Override
  public void initialize(URL location, ResourceBundle resources) {

    Platform.runLater(() -> screen.requestFocus());
    horas_box.getItems().addAll(horas);
    minutos_box.getItems().addAll(minutos);
    segundos_box.getItems().addAll(segundos);
    unidadeDeMedida.getItems().addAll(unidadesDeMedida);

  }

  public void setReceita(Receita item) {
    this.receita = item;
    Platform.runLater(() -> {
      nome_receita_label.setText(item.getNome());
      receita_image.setImage(item.getImagem());
      preencherListaIngredientes(item.getIngredientesFormatados());
      text_area_modo_preparo.setText(item.getModoPreparo());
      preencherDificuldade(item.getDificuldade());
      preencherTabelaNutricional(item);
      preencherRestricoes(item);
    });
  }

  private void preencherRestricoes(Receita item) {
    ArrayList<Integer> restricoesDaReceita = item.getRestricoes();
    for (Integer restricao : restricoesDaReceita) {
      if (restricao == RestricaoEnum.FRUTOS_DO_MAR.getValor()) {
        frutos_do_mar_toggle.setSelected(true);
      } else if (restricao == RestricaoEnum.VEGANA.getValor()) {
        vegana_toggle.setSelected(true);
      } else if (restricao == RestricaoEnum.VEGETARIANA.getValor()) {
        vegetariana_toggle.setSelected(true);
      } else if (restricao == RestricaoEnum.ACUCAR.getValor()) {
        acucar_toggle.setSelected(true);
      } else if (restricao == RestricaoEnum.LACTOSE.getValor()) {
        lactose_toggle.setSelected(true);
      } else if (restricao == RestricaoEnum.GLUTEN.getValor()) {
        gluten_toggle.setSelected(true);

      }
    }
  }

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

  // Método auxiliar para deixar a primeira letra de uma string em Maiúsculo
  private String capitalizeFirstLetter(String text) {
    if (text == null || text.isEmpty()) {
      return text;
    }
    return text.substring(0, 1).toUpperCase() + text.substring(1).toLowerCase();
  }

  @FXML
  void trocarNomeReceita(ActionEvent event) {
    if (receberNovoNomeField(event).isEmpty()) {
      System.out.println("Insira um novo nome valido");
    } else {
      nome_receita_label.setText(receberNovoNomeField(event));
    }

  }

  @FXML
  String receberNovoNomeField(ActionEvent event) {
    String novoNome = pegarNomeReceita.getText();
    return novoNome;
  }

  @FXML
  void adicionarIngrediente(ActionEvent event) {
    String nome = nomeIngredienteField.getText();
    String quantidadeText = quantidadeIngredienteField.getText();
    String unidade = unidadeDeMedida.getValue();

    if (nome.isEmpty() || quantidadeText.isEmpty() || unidade == null) {
      System.out.println("Preencha todos os campos antes de adicionar o ingrediente.");
      return;
    }

    try {
      double quantidade = Double.parseDouble(quantidadeText);

      // Atualize o modelo , deixei comentado pois o construtor de ingrediente
      // possui tabela nuutricional e nao sei como vai ser feito

      // Ingrediente novoIngrediente = new Ingrediente(nome); // Supondo que você
      // tenha uma classe Ingrediente
      // ReceitaIngrediente relacao = new ReceitaIngrediente(novoIngrediente,
      // quantidade, unidade);
      // receita.getIngredientesMapping().put(novoIngrediente, relacao);

      // Atualize a lista
      String itemLista = "\u25CF " + nome + " - " + quantidade + " " + unidade;
      list_view_ingredientes.getItems().add(itemLista);

      // Limpa os campos
      nomeIngredienteField.clear();
      quantidadeIngredienteField.clear();
      unidadeDeMedida.setValue(null);

    } catch (NumberFormatException e) {
      System.out.println("A quantidade deve ser um número válido.");
    }
  }

  @FXML
  void salvarModoPreparo(ActionEvent event) {
    String novoModoPreparo = text_area_modo_preparo.getText();

    if (receita != null) {
      receita.setModoPreparo(novoModoPreparo);
      System.out.println("Modo de preparo atualizado: " + novoModoPreparo);
      // Adicione uma lógica para salvar no banco ou na camada de dados
    } else {
      System.out.println("Nenhuma receita selecionada para atualizar.");
    }
  }

  @FXML
  void removerIngrediente(ActionEvent event) {
    String ingredienteSelecionado = list_view_ingredientes.getSelectionModel().getSelectedItem();

    if (ingredienteSelecionado == null) {
      System.out.println("Selecione um ingrediente para remover.");
      return;
    }
    // Remover do modelo
    String nomeIngrediente = ingredienteSelecionado.split("-")[0].trim().substring(2);
    Ingrediente ingredienteParaRemover = null;
    for (Ingrediente ing : receita.getIngredientesMapping().keySet()) {
      if (ing.getNomeIngrediente().equalsIgnoreCase(nomeIngrediente)) {
        ingredienteParaRemover = ing;
        break;
      }
    }
    if (ingredienteParaRemover != null) {
      receita.getIngredientesMapping().remove(ingredienteParaRemover);
    }

    // Remover da lista
    list_view_ingredientes.getItems().remove(ingredienteSelecionado);
  }

  @FXML
  void salvarDificuldade(ActionEvent event) {
    if (facil_toggle.isSelected()) {
      receita.setDificuldade(1);
    } else if (medio_toggle.isSelected()) {
      receita.setDificuldade(2);
    } else if (dificil_toggle.isSelected()) {
      receita.setDificuldade(3);
    }
    System.out.println("Dificuldade escolhida: " + receita.getDificuldade());
  }

  @FXML
  void fechar_programa(MouseEvent event) {
    System.exit(0);
  }

  @FXML
  void voltar_tela(MouseEvent event) throws IOException {
    stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
    stage.close();
  }

}
