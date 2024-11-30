package com.mealmatch.controller;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import com.mealmatch.jdbc.dao.ReceitaDAO;
import com.mealmatch.jdbc.database.ConnectionFactory;
import com.mealmatch.model.Receita;
import com.mealmatch.utils.ControleDeSessao;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
  private RadioButton difDificil;

  @FXML
  private ImageView close_button;

  @FXML
  private ImageView fav_button;

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

  @Override
  public void initialize(URL location, ResourceBundle resources) {

    Platform.runLater(() -> screen.requestFocus());

    receitas_listview.setCellFactory(listView -> new ReceitaListCellController());

    limparListaReceitas();
    consumirEventoDeSelecaoDeReceita();
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
    receitasObservable.clear(); // Limpa a lista antes de adicionar novos itens, afinal é uma nova busca
    receitasObservable.addAll(receitasBuscadas); // Adiciona as receitas buscadas na lista
  }

  @FXML
  void buscar() {

    List<Receita> receitasBuscadas = new ArrayList<>();
    ReceitaDAO receitaDAO = new ReceitaDAO(ConnectionFactory.getConnection());//Quem busca as Receitas
  
    if (nomeReceita_toggle.isSelected()) {
      // Busca por nome da receita
      String nomeReceita = search_field.getText();
      try {
        receitasBuscadas = receitaDAO.findByName(nomeReceita);
        if (receitasBuscadas.isEmpty()) {
          System.out.println("Nenhuma receita encontrada com o nome: " + nomeReceita);
        }
      } catch (SQLException e) {
        System.out.println("Erro ao buscar receitas por nome: " + e.getMessage());
        e.printStackTrace();
      }
    }

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

    // Atualizar o ListView com as receitas buscadas
    atualizarListaReceitas(receitasBuscadas);
  }

  @FXML
  void acessar_favoritas(MouseEvent event) throws IOException {
    System.out.println("Mostra apenas as receitas favoritadas pelo usuario");
  }

  @FXML
  void acessar_perfil(MouseEvent event) throws IOException {
    FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/tela_perfil.fxml"));
    Parent root = loader.load();
    scene = new Scene(root);
    stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
    stage.setScene(scene);
    stage.show();
  }

  @FXML
  void adicionar_receita(MouseEvent event) throws IOException {
    FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/tela_criarReceita.fxml"));
    Parent root = loader.load();
    scene = new Scene(root);
    stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
    stage.setScene(scene);
    stage.show();
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
