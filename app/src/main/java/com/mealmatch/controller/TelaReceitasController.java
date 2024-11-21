package com.mealmatch.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
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
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class TelaReceitasController implements Initializable {

  @FXML
  private ToggleGroup busca_de_receitas;

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

  private ObservableList<Receita> receitasObservable;

  @Override
  public void initialize(URL location, ResourceBundle resources) {

    Platform.runLater(() -> screen.requestFocus());
    // O que acontece aqui:
    // O método buscar retorna uma lista de receitas. Quando chamamos o método
    // atualizarListaReceitas,
    // ele limpa a lista observável (ObservableList) e adiciona as receitas buscadas
    // nela. Dessa forma, nossa
    // lista original de receitas é transformada em uma ObservableList.

    // Ao usar o código abaixo, o que ocorre é o seguinte:
    // O ListView observa a ObservableList associada a ele. Sempre que ocorre uma
    // alteração na ObservableList
    // (adição, remoção ou atualização de itens), o ListView é notificado e reflete
    // as mudanças automaticamente.

    // Além disso, as células do ListView são renderizadas utilizando o CellFactory configurado, que, neste caso, é o ReceitaListCellController. Esse controller é responsável por definir como cada célula será exibida na lista, ou seja, ele renderiza os itens visuais da lista.
    receitas_listview.setCellFactory(listView -> new ReceitaListCellController());

    // Explicação detalhada dos elementos:
    // 1. setCellFactory: É um método do ListView usado para configurar o
    // "fabricante de células" (CellFactory),
    // que define como cada célula (item visual) do ListView será criada e exibida.
    // 2. listView: É o parâmetro fornecido automaticamente pelo método
    // setCellFactory. Ele representa a própria
    // instância do ListView (neste caso, receitas_listview) que está sendo
    // configurada. Esse parâmetro é
    // fornecido pela API do JavaFX como parte do contrato do Callback.
    // O listView pode ser útil dentro do CellFactory, caso seja necessário acessar
    // métodos ou propriedades
    // do ListView enquanto as células são configuradas.
    // 3. ReceitaListCellController: É uma classe personalizada que estende
    // ListCell<Receita>, e nela implementamos
    // a lógica para renderizar as informações de uma Receita em cada célula da
    // lista. Por exemplo, no método
    // updateItem, usamos o setGraphic() para configurar os componentes visuais da
    // célula.

    // Limpa a lista de receitas buscadas:
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
    // Aqui é um exemplo de busca de receitas, por enquanto, apenas adicionando
    // receitas fictícias, aqui tera o uso do receitas.dao para buscar as receitas
    // no banco de dados
    List<Receita> receitasBuscadas = new ArrayList<>();
    receitasBuscadas.add(new Receita(1, "Salada de Quinoa", "quinoa, tomate, pepino", "preparo", 10, 10, 1,
        new Image(getClass().getResource("/images/frango.png").toExternalForm()), 10, 10, true));
    receitasBuscadas.add(new Receita(2, "Salada de Quinoa", "quinoa, tomate, pepino", "preparo", 10, 10, 1,
        new Image(getClass().getResource("/images/frango.png").toExternalForm()), 10, 10, true));
    receitasBuscadas.add(new Receita(3, "Salada de Quinoa", "quinoa, tomate, pepino", "preparo", 10, 10, 1,
        new Image(getClass().getResource("/images/frango.png").toExternalForm()), 10, 10, true));
    receitasBuscadas.add(new Receita(4, "Salada de Quinoa", "quinoa, tomate, pepino", "preparo", 10, 10, 1,
        new Image(getClass().getResource("/images/frango.png").toExternalForm()), 10, 10, true));
    receitasBuscadas.add(new Receita(5, "Salada de Quinoa", "quinoa, tomate, pepino", "preparo", 10, 10, 1,
        new Image(getClass().getResource("/images/frango.png").toExternalForm()), 10, 10, true));
    receitasBuscadas.add(new Receita(6, "Salada de Quinoa", "quinoa, tomate, pepino", "preparo", 10, 10, 1,
        new Image(getClass().getResource("/images/frango.png").toExternalForm()), 10, 10, true));
    receitasBuscadas.add(new Receita(7, "Salada de Quinoa", "quinoa, tomate, pepino", "preparo", 10, 10, 1,
        new Image(getClass().getResource("/images/frango.png").toExternalForm()), 10, 10, true));
    receitasBuscadas.add(new Receita(8, "Salada de Quinoa", "quinoa, tomate, pepino", "preparo", 10, 10, 1,
        new Image(getClass().getResource("/images/frango.png").toExternalForm()), 10, 10, true));
    receitasBuscadas.add(new Receita(9, "Salada de Quinoa", "quinoa, tomate, pepino", "preparo", 10, 10, 1,
        new Image(getClass().getResource("/images/frango.png").toExternalForm()), 10, 10, true));
    receitasBuscadas.add(new Receita(10, "Salada de Quinoa", "quinoa, tomate, pepino", "preparo", 10, 10, 1,
        new Image(getClass().getResource("/images/frango.png").toExternalForm()), 10, 10, true));
    receitasBuscadas.add(new Receita(11, "Salada de Quinoa", "quinoa, tomate, pepino", "preparo", 10, 10, 1,
        new Image(getClass().getResource("/images/frango.png").toExternalForm()), 10, 10, true));
    receitasBuscadas.add(new Receita(12, "Salada de Quinoa", "quinoa, tomate, pepino", "preparo", 10, 10, 1,
        new Image(getClass().getResource("/images/frango.png").toExternalForm()), 10, 10, true));
    receitasBuscadas.add(new Receita(13, "Salada de Quinoa", "quinoa, tomate, pepino", "preparo", 10, 10, 1,
        new Image(getClass().getResource("/images/frango.png").toExternalForm()), 10, 10, true));
    receitasBuscadas.add(new Receita(14, "Salada de Quinoa", "quinoa, tomate, pepino", "preparo", 10, 10, 1,
        new Image(getClass().getResource("/images/frango.png").toExternalForm()), 10, 10, true));

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
    FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/tela_login.fxml"));
    Parent root = loader.load();
    scene = new Scene(root);
    stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
    stage.setScene(scene);
    stage.show();
  }

}
