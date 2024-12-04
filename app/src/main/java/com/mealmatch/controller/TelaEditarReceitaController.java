package com.mealmatch.controller;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import com.mealmatch.enums.DificuldadeEnum;
import com.mealmatch.enums.RestricaoEnum;
import com.mealmatch.jdbc.connection.ConnectionFactory;
import com.mealmatch.jdbc.dao.IngredienteDAO;
import com.mealmatch.jdbc.dao.ReceitaDAO;
import com.mealmatch.model.Ingrediente;
import com.mealmatch.model.NutrienteValor;
import com.mealmatch.model.Receita;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
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
  private ImageView salvar_receita_image;

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
      formatarTempo(item.getTempoPreparo());
    });
  }

  @FXML
  void salvar_receita(MouseEvent event) throws IOException {
    receita.setNome(nome_receita_label.getText());
    receita.setModoPreparo(text_area_modo_preparo.getText());
    receita.setTempoPreparo(formatarTempoPreparo());

    if (facil_toggle.isSelected()) {
      receita.setDificuldade(1);
    } else if (medio_toggle.isSelected()) {
      receita.setDificuldade(2);
    } else if (dificil_toggle.isSelected()) {
      receita.setDificuldade(3);
    }

    receita.setRestricoes(obterRestricoesSelecionadas());

    try {
      ReceitaDAO receitaDAO = new ReceitaDAO(ConnectionFactory.getConnection());
      receitaDAO.atualizarReceita(receita);
      voltar_tela(event);
    } catch (SQLException e) {
      System.out.println("Erro ao atualizar receita: " + e.getMessage());
    }
  }

  // Formata o tempo de preparo da receita para aparecer no formato h m s
  private void formatarTempo(int tempoPreparoEmSegundos) {
    Platform.runLater(() -> {
      horas_box.setValue(tempoPreparoEmSegundos / 3600);
      minutos_box.setValue((tempoPreparoEmSegundos % 3600) / 60);
      segundos_box.setValue(tempoPreparoEmSegundos % 60);
    });
  }

  private int formatarTempoPreparo() { // Obtém o texto do campo
    int totalSegundos = 0;

    // Converte horas, minutos e segundos para segundos
    totalSegundos += horas_box.getValue() * 3600;
    totalSegundos += minutos_box.getValue() * 60;
    totalSegundos += segundos_box.getValue();

    return totalSegundos; // Retorna o tempo total em segundos
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

  private ArrayList<Integer> obterRestricoesSelecionadas() {
    ArrayList<Integer> restricoes = new ArrayList<>();
    if (frutos_do_mar_toggle.isSelected())
      restricoes.add(RestricaoEnum.FRUTOS_DO_MAR.getValor());
    if (vegana_toggle.isSelected())
      restricoes.add(RestricaoEnum.VEGANA.getValor());
    if (vegetariana_toggle.isSelected())
      restricoes.add(RestricaoEnum.VEGETARIANA.getValor());
    if (acucar_toggle.isSelected())
      restricoes.add(RestricaoEnum.ACUCAR.getValor());
    if (lactose_toggle.isSelected())
      restricoes.add(RestricaoEnum.LACTOSE.getValor());
    if (gluten_toggle.isSelected())
      restricoes.add(RestricaoEnum.GLUTEN.getValor());
    return restricoes;
  }

  @FXML
  void adicionarIngrediente(ActionEvent event) {
    String nome = formatName(nomeIngredienteField.getText());
    String quantidadeText = quantidadeIngredienteField.getText();
    String unidade = unidadeDeMedida.getValue();

    if (nome.isEmpty() || quantidadeText.isEmpty() || unidade == null) {
      System.out.println("Preencha todos os campos antes de adicionar o ingrediente.");
      return;
    }

    try {
      double quantidade = Double.parseDouble(quantidadeText);

      ReceitaDAO receitaDAO = new ReceitaDAO(ConnectionFactory.getConnection());
      receitaDAO.adicionarIngredienteNaReceita(receita.getId(), nome, quantidade, unidade);

      String itemLista = "\u25CF " + nome + " - " + quantidade + " " + unidade;
      list_view_ingredientes.getItems().add(itemLista);

      nomeIngredienteField.clear();
      quantidadeIngredienteField.clear();
      unidadeDeMedida.setValue(null);
    } catch (NumberFormatException e) {
      System.out.println("A quantidade deve ser um número válido.");
    } catch (SQLException e) {
      System.out.println("Erro ao adicionar ingrediente: " + e.getMessage());
    }
  }

  @FXML
  void salvarModoPreparo(ActionEvent event) {
    String novoModoPreparo = text_area_modo_preparo.getText();

    if (receita != null) {
      receita.setModoPreparo(novoModoPreparo);
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

    try {
      // Remove o "•" no início e o texto entre parênteses
      String nomeIngrediente = ingredienteSelecionado.replaceAll("^[^\\w]*", "").split("\\(")[0].trim();

      ReceitaDAO receitaDAO = new ReceitaDAO(ConnectionFactory.getConnection());
      IngredienteDAO ingredienteDAO = new IngredienteDAO(ConnectionFactory.getConnection());

      // Busca o ingrediente no banco de dados pelo nome
      Ingrediente ingrediente = ingredienteDAO.getIngredienteByNome(nomeIngrediente);

      if (ingrediente != null) {
        // Remove a relação da tabela receitaingrediente
        receitaDAO.removerIngredienteDaReceita(receita.getId(), ingrediente.getId_ingrediente());

        // Remove o ingrediente do HashMap da receita
        receita.getIngredientesMapping().remove(ingrediente);

        // Remove o item da ListView
        list_view_ingredientes.getItems().remove(ingredienteSelecionado);

      } else {
        System.out.println("Ingrediente não encontrado no banco de dados.");
      }
    } catch (SQLException e) {
      System.out.println("Erro ao remover ingrediente: " + e.getMessage());
      e.printStackTrace();
    }
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

  // Método para formatar a string: primeira letra maiúscula, demais minúsculas
  private String formatName(String nome) {
    if (nome == null || nome.isEmpty()) {
      return nome;
    }
    return nome.substring(0, 1).toUpperCase() + nome.substring(1).toLowerCase();
  }

}
