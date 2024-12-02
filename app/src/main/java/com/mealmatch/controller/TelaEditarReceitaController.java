package com.mealmatch.controller;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import com.mealmatch.enums.DificuldadeEnum;
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
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
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
    private TextField novoTempoDePreparo;

    @FXML
    private TableColumn<NutrienteValor, String> valores_column;

    @FXML
    private RadioButton vegana_toggle;

    @FXML
    private RadioButton vegetariana_toggle;

    @FXML
    private Button tempoDePreparoButton;

    @FXML
    private Button editarNomeButton;

     @FXML
    private TextField pegarNomeReceita;

    private ObservableList<NutrienteValor> dadosNutricionais;

    private String[] unidadesDeMedida = { "g", "ml", "kg", "L" };
    @SuppressWarnings("unused")
    private Receita receita;

  Scene scene;
  Stage stage;

  
    @Override
    public void initialize(URL location, ResourceBundle resources) {
  
     // Cria um TextFormatter que permite apenas números inteiros
       novoTempoDePreparo.setTextFormatter(new TextFormatter<>(change -> {
        String newText = change.getControlNewText();
        if (newText.matches("\\d*")) {
            return change;
        }
        return null;
    }));



      Platform.runLater(() -> screen.requestFocus());
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

    unidadeDeMedida.getItems().addAll(unidadesDeMedida);

    }

  public void setReceita(Receita item) {
    this.receita = item;
    Platform.runLater(() -> {
      nome_receita_label.setText(item.getNome() + item.getId());
      receita_image.setImage(item.getImagem());
      preencherListaIngredientes(item.getIngredientesFormatados());
      text_area_modo_preparo.setText(item.getModoPreparo());
      preencherDificuldade(item.getDificuldade());
    });
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
  private String capitalizeFirstLetter(String text){
    if (text == null || text.isEmpty()) {
      return text;
    }
    return text.substring(0, 1).toUpperCase() + text.substring(1).toLowerCase();
  }

     @FXML
    void trocarNomeReceita(ActionEvent event) {
      if(receberNovoNomeField(event).isEmpty()){
        System.out.println("Insira um novo nome valido");
      }else{
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

        //Ingrediente novoIngrediente = new Ingrediente(nome); // Supondo que você tenha uma classe Ingrediente
       // ReceitaIngrediente relacao = new ReceitaIngrediente(novoIngrediente, quantidade, unidade);
      //  receita.getIngredientesMapping().put(novoIngrediente, relacao);

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
    void salvarDificuldade(ActionEvent event){
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
    void confirmarNovoTempoDePreparo(ActionEvent event) {
      String tempoDePreparo = novoTempoDePreparo.getText();

      int valorTempoDePreparo = Integer.parseInt(tempoDePreparo);

      receita.setTempoPreparo(valorTempoDePreparo);

      System.out.println("Novo tempo de preparo: " + receita.getTempoPreparo());

      novoTempoDePreparo.clear();
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
