package com.mealmatch.controller;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import com.mealmatch.jdbc.dao.IngredienteDAO;
import com.mealmatch.jdbc.dao.ReceitaDAO;
import com.mealmatch.jdbc.database.ConnectionFactory;
import com.mealmatch.model.Ingrediente;
import com.mealmatch.model.Receita;
import com.mealmatch.utils.ControleDeSessao;
import com.mealmatch.utils.ImageSelector;

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
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class TelaAdicionarReceitaController implements Initializable {
  @FXML
  private ImageView botaoVoltar;

  @FXML
  private ImageView close_image;

  @FXML
  private ImageView add_receita_image;

  @FXML
  private RadioButton acucar_toogle;

  @FXML
  private RadioButton vegana_toogle;

  @FXML
  private RadioButton vegetariana_toogle;

  @FXML
  private RadioButton frutos_do_mar_toogle;

  @FXML
  private RadioButton lactose_toogle;

  @FXML
  private RadioButton gluten_toogle;

  @FXML
  private Button adicionaPasso;

  @FXML
  private ImageView receita_image;

  @FXML
  private TextField ingrediente_adicionado;

  @FXML
  private TextField tempoDePrearoField;

  @FXML
  private TextField quantidade_ingrediente;

  @FXML
  private TextField campoModoPreparo;

  @FXML
  private TextField nomeDaReceita;

  @FXML
  private Button botaoAdicionar;

  @FXML
  private ChoiceBox<String> unidadeDeMedida;

  @FXML
  private ListView<String> listaView;

  @FXML
  private ListView<String> listaDePreparo;

  @FXML
  private RadioButton dificuldadeDificil;

  @FXML
  private RadioButton dificuldadeFacil;

  @FXML
  private ToggleGroup dificuldadeReceita;

  @FXML
  private RadioButton dificuldadeMedio;

  private String[] unidadesDeMedida = { "g", "ml", "kg", "L" };

  private int dificuldade;

  // Lista para armazenar os ingredientes
  ObservableList<String> listaIngredientes = FXCollections.observableArrayList();

  // Lista para armazenar o passo a passo do modo de preparo
  ObservableList<String> passosModoPreparo = FXCollections.observableArrayList();

  private byte[] imagemEmBytes;
  List<Ingrediente> listaObjetoIngredientes;

  Scene scene;
  Stage stage;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    unidadeDeMedida.getItems().addAll(unidadesDeMedida); // adicionando as unidades de medida dos ingredientes
    listaView.setItems(listaIngredientes);// setando a lista de ingredientes
    listaDePreparo.setItems(passosModoPreparo);// setando a lista de preparo

    listaObjetoIngredientes = new ArrayList<>();// inicializando a lista de objetos ingrediente
  }

  @FXML
  void criar_receita(MouseEvent event) throws IOException {
    Receita receita = new Receita();
    receita.setNome(nomeDaReceita.getText());
    receita.setDificuldade(dificuldade);
    receita.setModoPreparo(formatarModoPreparo());
    receita.setTempoPreparo(formatarTempoPreparo());
    receita.setImagem(new Image(new ByteArrayInputStream(imagemEmBytes)));
    receita.setIdUsuarioDonoReceita(ControleDeSessao.getInstance().getUserId());
    adicionarReceita(receita);
    voltar_tela(event);
  }

  // Método para adicionar a receita no banco de dados
  private void adicionarReceita(Receita receita) {

    //Primeiro adiciona a receita no banco de dados junto com a relacao de usuario dono da receita
    ReceitaDAO receitaDao = new ReceitaDAO(ConnectionFactory.getConnection());
    int idReceita = receitaDao.adicionarReceitaCompleta(receita, imagemEmBytes,ControleDeSessao.getInstance().getUserId());

    //Depois adiciona os ingredientes na receita por meio da relacao de receita e ingrediente
    for (int i = 0; i < listaIngredientes.size(); i++) {
      Ingrediente ingrediente = listaObjetoIngredientes.get(i);

      String[] partes = listaIngredientes.get(i).split("\\(");
      String quantidadeStr = partes[1].split("-")[0].trim();
      String unidade = partes[1].split("-")[1].replace(")", "").trim();

      Double quantidade = Double.parseDouble(quantidadeStr);

      receitaDao.adicionarIngredienteNaReceita(idReceita, ingrediente.getId_ingrediente(), quantidade, unidade);
    }
  }

  // Método para formatar o tempo de preparo
  private int formatarTempoPreparo() {
    String tempoPreparo = tempoDePrearoField.getText(); // Obtém o texto do campo
    int totalSegundos = 0;

    // Divide a string nos espaços para processar os elementos
    String[] partes = tempoPreparo.split(" ");

    for (int i = 0; i < partes.length; i += 2) {
      int valor = Integer.parseInt(partes[i]); // Converte o número para inteiro
      String unidade = partes[i + 1]; // Obtém a unidade (h, min, s)

      // Converte para segundos de acordo com a unidade
      if (unidade.equals("h")) {
        totalSegundos += valor * 3600; // 1 hora = 3600 segundos
      } else if (unidade.equals("min")) {
        totalSegundos += valor * 60; // 1 minuto = 60 segundos
      } else if (unidade.equals("s")) {
        totalSegundos += valor; // Segundos permanecem como estão
      }
    }

    return totalSegundos; // Retorna o tempo total em segundos
  }

  // Método para formatar o modo de preparo retornando uma string unica com todos os passos separados por quebra de linha
  private String formatarModoPreparo() {
    StringBuilder resultado = new StringBuilder();

    for (String passosModoDePreparo : listaDePreparo.getItems()) {
      resultado.append(passosModoDePreparo).append("\n");
    }

    return resultado.toString();
  }

  // Método para selecionar a imagem da receita
  @FXML
  void selecionar_image_receita(MouseEvent event) {
    try {
      // Chama o seletor de imagem
      File selectedFile = ImageSelector.selecionarImagem(((Node) event.getSource()).getScene().getWindow());

      // Se um arquivo válido foi selecionado, exibe no ImageView
      if (selectedFile != null) {
        receita_image.setImage(ImageSelector.carregarImagem(selectedFile));

        // converte a imagem para binário para salvar a imagem no banco de dados
        byte[] imageBytes = ImageSelector.converterImagemParaBytes(selectedFile);
        imagemEmBytes = imageBytes;

      }
    } catch (IOException e) {
      System.out.println("Erro ao selecionar imagem: " + e.getMessage());
    }
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

  @FXML
  void fechar_programa(MouseEvent event) {
    System.exit(0);
  }


  @FXML
  void adiciona_ingrediente(ActionEvent event) throws SQLException {
    String quantidade = quantidade_ingrediente.getText();
    String unidade = unidadeDeMedida.getValue();
    String ingredienteString = ingrediente_adicionado.getText();

    if (quantidade.isEmpty() || unidade == null || ingredienteString.isEmpty()) {
      System.out.println("Preencha todos os campos!");
      return;
    }

    IngredienteDAO ingredienteDAO = new IngredienteDAO(ConnectionFactory.getConnection());
    Ingrediente ingrediente = ingredienteDAO.getIngredienteByNome(ingredienteString);

    if (ingrediente == null) {
      Alert alert = new Alert(AlertType.ERROR);
      alert.setTitle("Erro");
      alert.setHeaderText("Ingrediente não encontrado na nossa base de dados");
      alert.setContentText(
          "Por favor, verifique o nome do ingrediente e tente novamente, caso o erro persista não temos esse ingrediente em nossa base de dados");
      alert.showAndWait();
      return;
    }

    listaObjetoIngredientes.add(ingrediente);

    String ingredienteFormatado = ingredienteString + " ( " + quantidade + " - " + unidade + " )";
    Platform.runLater(() -> {
      listaIngredientes.add(ingredienteFormatado);
      ingrediente_adicionado.clear();
      quantidade_ingrediente.clear();
      unidadeDeMedida.setValue(null);
    });

  }

  @FXML
  void definirDificuldade(ActionEvent event) {

    if (dificuldadeFacil.isSelected()) {
      dificuldade = 1;
    } else if (dificuldadeMedio.isSelected()) {
      dificuldade = 2;
    } else if (dificuldadeDificil.isSelected()) {
      dificuldade = 3;
    }
    System.out.println("Dificuldade escolhida: " + dificuldade);
  }

  @FXML
  void adicionarPasso(ActionEvent event) {
    String passo = campoModoPreparo.getText();

    if (passo.isEmpty()) {
      System.out.println("Digite o próximo passo antes de adicionar!");
      return;
    }

    passosModoPreparo.add((passosModoPreparo.size() + 1) + ". " + passo); // Adiciona passo com numeração
    atualizarPromptText();
    campoModoPreparo.clear(); // Limpa o campo após adicionar
  }

  @FXML
  void removerPasso(ActionEvent event) {
    String passoSelecionado = listaDePreparo.getSelectionModel().getSelectedItem();
    if (passoSelecionado != null) {
      passosModoPreparo.remove(passoSelecionado);
      atualizarNumeracao(); // Atualiza a numeração após a remoção
      atualizarPromptText();
    } else {
      System.out.println("Selecione um passo para remover!");
    }
  }

  private void atualizarNumeracao() {
    for (int i = 0; i < passosModoPreparo.size(); i++) {
      String passoSemNumero = passosModoPreparo.get(i).substring(passosModoPreparo.get(i).indexOf(". ") + 2);
      passosModoPreparo.set(i, (i + 1) + ". " + passoSemNumero);
    }
  }

  private void atualizarPromptText() {
    int proximoPasso = passosModoPreparo.size() + 1;
    switch (proximoPasso) {
      case 1:
        campoModoPreparo.setPromptText("Primeiro passo");
        break;
      case 2:
        campoModoPreparo.setPromptText("Segundo passo");
        break;
      case 3:
        campoModoPreparo.setPromptText("Terceiro passo");
        break;
      default:
        campoModoPreparo.setPromptText(proximoPasso + "º passo");
    }
  }

}
