package com.mealmatch.controller;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import com.mealmatch.enums.RestricaoEnum;
import com.mealmatch.jdbc.connection.ConnectionFactory;
import com.mealmatch.jdbc.dao.ReceitaDAO;
import com.mealmatch.model.Receita;
import com.mealmatch.model.RestricoesSelecionadas;
import com.mealmatch.utils.ControleDeSessao;

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
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class TelaReceitasController implements Initializable {

  @FXML
  private ToggleGroup busca_de_receitas;

  @FXML
  private ToggleGroup toggleGroupDificuldade;

  @FXML
  private RadioButton difFacil;

  @FXML
  private RadioButton difMedio;

  @FXML
  private Button escolherRestricoesButton;

  @FXML
  private RadioButton difDificil;

  @FXML
  private ImageView close_button;

  @FXML
  private ImageView fav_button;

  @FXML
  private ImageView check_favoritos_image;

  @FXML
  private RadioButton ingrediente_toogle;

  @FXML
  private ImageView logout_button;

  @FXML
  private RadioButton nomeReceita_toggle;

  @FXML
  private ImageView profile_button;

  @FXML
  private ImageView criar_receita_button;

  @FXML
  private ListView<Receita> receitas_listview;

  @FXML
  private ImageView search_button;

  @FXML
  private TextField search_field;

  @FXML
  private AnchorPane screen;

  Scene scene;
  Stage stage;

  private int dificuldadeSelecionada;

  private ObservableList<Receita> receitasObservable;
  private List<Receita> receitasAnteriores = new ArrayList<>();

  @Override
  public void initialize(URL location, ResourceBundle resources) {

    Platform.runLater(() -> screen.requestFocus());

    receitas_listview.setCellFactory(listView -> new ReceitaListCellController());
    busca_de_receitas.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
      controlePromptTextoBusca();
    });

    limparListaReceitas();
    consumirEventoDeSelecaoDeReceita();

    receitas_listview.getStylesheets().add(getClass().getResource("/styles/style.css").toExternalForm());

  }

  private void controlePromptTextoBusca() {
    if (ingrediente_toogle.isSelected()) {
      search_field.clear();
      search_field.setPromptText("Digite os Ingredientes para buscar separados por vírgula. EX: Frango, Manteiga, ...");
    } else if (nomeReceita_toggle.isSelected()) {
      search_field.clear();
      search_field.setPromptText("Digite o nome da receita que deseja buscar");
    }
  }

  private void limparListaReceitas() {
    // Iniciando o ObservableList vazio, para ele receber as receitas após a busca
    receitasObservable = FXCollections.observableArrayList();
    receitas_listview.setItems(receitasObservable);
  }

  private void consumirEventoDeSelecaoDeReceita() {
    // Adicionar filtro de evento para impedir a seleção de células, mas permitir
    // eventos nos componentes internos
    receitas_listview.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
      Node clickedNode = event.getPickResult().getIntersectedNode();
      // Subir na hierarquia para ver se o clique foi em um botão
      while (clickedNode != null && !(clickedNode instanceof ListView)) {
        if (clickedNode instanceof Button) {
          // Permitir eventos de clique no botão e outros componentes internos
          return;
        }
        clickedNode = clickedNode.getParent();
      }
      // Caso o clique não tenha sido em um botão, consumir o evento para evitar
      // seleção
      event.consume();
    });
  }

  private void atualizarListaReceitas(List<Receita> receitasBuscadas) {
    receitasObservable.clear(); // Limpa a lista antes de adicionar novos itens
    if (receitasBuscadas != null && !receitasBuscadas.isEmpty()) {
      receitasObservable.addAll(receitasBuscadas); // Adiciona apenas itens válidos
    }
    receitas_listview.setItems(receitasObservable); // Atualiza o ListView
  }

  @FXML
  void buscar() {
    ReceitaDAO receitaDAO = new ReceitaDAO(ConnectionFactory.getConnection());

    List<Receita> receitasBuscadas = new ArrayList<>();

    // Realiza a busca
    if (nomeReceita_toggle.isSelected()) {
      receitasBuscadas = fetchReceitasByName(receitasBuscadas, receitaDAO);
    } else if (ingrediente_toogle.isSelected()) {
      receitasBuscadas = fetchReceitasByIngredients(receitasBuscadas, receitaDAO);
    }

    receitasBuscadas = getFilteredReceitas(receitasBuscadas);

    receitasBuscadas = getRestrictionsReceitas(receitasBuscadas);

    // Verifica se as listas de resultados são iguais
    if (receitasAnteriores.equals(receitasBuscadas)) {
      System.out.println("Lista de receitas igual à anterior. Evitando atualização.");
      return;
    }

    // Atualiza o histórico de receitas
    receitasAnteriores = new ArrayList<>(receitasBuscadas);

    // Atualiza a ListView com as novas receitas
    atualizarListaReceitas(receitasBuscadas);
  }

  private List<Receita> getFilteredReceitas(List<Receita> receitasBuscadas) {

    dificuldadeSelecionada = -1; // Valor que nao pega nenhum filtro
    if (difFacil.isSelected()) {
      dificuldadeSelecionada = 1;
    } else if (difMedio.isSelected()) {
      dificuldadeSelecionada = 2;
    } else if (difDificil.isSelected()) {
      dificuldadeSelecionada = 3;
    }

    // Aplicacao do filtro
    if (dificuldadeSelecionada != -1) {
      receitasBuscadas = receitasBuscadas.stream()
          .filter(receita -> receita.getDificuldade() == dificuldadeSelecionada)
          .collect(Collectors.toList());
    }

    return receitasBuscadas;
  }

  // Nao esta funcionando , acredito que por que ao fechar a tela nao fica salvo
  // as escolhas do usuario
  private List<Receita> getRestrictionsReceitas(List<Receita> receitas) {
    List<RestricaoEnum> restricoesSelecionadas = RestricoesSelecionadas.getInstance().getRestricoes();

    List<Receita> receitasFiltradas = receitas.stream()
        .filter(receita -> receita.getRestricoes().stream()
            .map(RestricaoEnum::fromInt) // Converte Integer para RestricaoEnum
            .noneMatch(restricoesSelecionadas::contains))
        .collect(Collectors.toList());

    return receitasFiltradas;
  }

  private List<Receita> fetchReceitasByIngredients(List<Receita> receitasBuscadas, ReceitaDAO receitaDAO) {

    String ingredientes = search_field.getText();

    if (!validarEntradaDeIngredientes(ingredientes)) {
      return new ArrayList<>(); // Retorna lista vazia caso a entrada seja inválida
    }

    // Manipula a string de ingredientes, separando em uma lista
    List<String> ingredientesList = Arrays.asList(ingredientes.split("\\s*,\\s*"));

    // Chama o DAO passando a lista de ingredientes
    receitasBuscadas = receitaDAO.buscarReceitasPorIngredientes(ingredientesList);

    if (receitasBuscadas.isEmpty()) {
      exibirAlerta("Nenhuma Receita Encontrada", "Nenhuma receita foi encontrada com esses Ingredientes",
          "Verifique o nome do ingrediente e sua ortografia.");
      return receitasBuscadas;
    }

    return receitasBuscadas;
  }

  private List<Receita> fetchReceitasByName(List<Receita> receitasBuscadas, ReceitaDAO receitaDAO) {
    String nomeReceita = search_field.getText();
    try {
      receitasBuscadas = receitaDAO.findByName(nomeReceita);
      if (receitasBuscadas.isEmpty()) {
        exibirAlerta("Nenhuma Receita Encontrada", "Nenhuma receita foi encontrada",
            "Tente ajustar o nome da receita ou verificar a ortografia.");
      }
    } catch (SQLException e) {
      System.out.println("Erro ao buscar receitas por nome: " + e.getMessage());
      e.printStackTrace();
    }
    return receitasBuscadas;
  }

  private boolean validarEntradaDeIngredientes(String ingredientesInput) {
    if (!ingredientesInput.matches("^[\\p{L}0-9, ]+$")) {
      exibirAlerta("Caracteres Inválidos", "Caracteres especiais não permitidos",
          "Por favor, utilize apenas letras, e use vírgulas para separar os ingredientes.");
      return false;
    }
    return true;
  }

  private void exibirAlerta(String titulo, String cabecalho, String conteudo) {
    javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
    alert.setTitle(titulo);
    alert.setHeaderText(cabecalho);
    alert.setContentText(conteudo);
    alert.showAndWait();
  }

  @FXML
  void acessar_favoritas(MouseEvent event) throws IOException {
    if (check_favoritos_image.isVisible()) {
      check_favoritos_image.setVisible(false);
      // Atualizar a lista de receitas, retirando o filtro de favoritas
    } else {
      // Atualizar a lista de receitas, aplicando o filtro de favoritas
      check_favoritos_image.setVisible(true);
    }
  }

  @FXML
  void acessar_perfil(MouseEvent event) throws IOException {
    FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/tela_perfil.fxml"));
    Parent root = loader.load();
    scene = new Scene(root);
    stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
    stage.hide();
    Stage novoStage = new Stage();
    novoStage.setOnHidden(e -> stage.show());
    novoStage.setScene(scene);
    novoStage.show();
  }

  @FXML
  void telaEscolherRestricoes(ActionEvent event) throws IOException {
    FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/tela_restricoes.fxml"));
    Parent root = loader.load();
    scene = new Scene(root);
    stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
    // stage.hide();
    Stage novoStage = new Stage();

    novoStage.setOnHidden(e -> {
      stage.show();
      buscar();
    });
    novoStage.initOwner(stage);
    novoStage.initModality(Modality.WINDOW_MODAL);
    novoStage.setScene(scene);
    novoStage.showAndWait();
  }

  @FXML
  void adicionar_receita(MouseEvent event) throws IOException {
    FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/tela_criarReceita.fxml"));
    Parent root = loader.load();
    scene = new Scene(root);
    stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
    stage.hide();
    Stage novoStage = new Stage();
    novoStage.setOnHidden(e -> {
      stage.show();
    });
    novoStage.setScene(scene);
    novoStage.show();
  }

  @FXML
  void fechar_programa() {
    System.exit(0);
  }

  @FXML
  void sair_conta(MouseEvent event) throws IOException {
    ControleDeSessao controleDeSessao = ControleDeSessao.getInstance();
    controleDeSessao.clearSession();
    FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/tela_login.fxml"));
    Parent root = loader.load();
    scene = new Scene(root);
    stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
    stage.setScene(scene);
    stage.show();
  }

}
