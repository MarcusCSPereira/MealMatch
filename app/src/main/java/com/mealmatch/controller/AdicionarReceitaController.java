package com.mealmatch.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

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
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class AdicionarReceitaController implements Initializable {
    @FXML
    private ImageView botaoVoltar;
 
    @FXML
    private Button adicionaPasso;
    
    @FXML
    private Button tempoDePreparoButton;

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

    private String[] unidadesDeMedida = { "g" , "ml" , "kg" , "L"};

    private int dificuldade;

    // Lista para armazenar os ingredientes
    ObservableList<String> listaIngredientes = FXCollections.observableArrayList();

    // Lista para armazenar o passo a passo do modo de preparo
    ObservableList<String> passosModoPreparo = FXCollections.observableArrayList();

    private String receitaNome;

    private float tempoDePreparo;
    

    Scene scene;
    Stage stage;

    

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        unidadeDeMedida.getItems().addAll(unidadesDeMedida); // adicionando as unidades de medida dos ingredientes
        listaView.setItems(listaIngredientes);
        listaDePreparo.setItems(passosModoPreparo);
        receitaNome = nomeDaReceita.getText();
        System.out.println(receitaNome);

         // Cria um TextFormatter que permite apenas números inteiros
    TextFormatter<Integer> formatter = new TextFormatter<>(change -> {
        // Verifica se o novo texto é um número inteiro
        if (change.getText().matches("\\d*")) { // Apenas dígitos
            return change;
        }
        return null; // Bloqueia a alteração caso não seja número
    });

    // Aplica o formatter no TextField
    tempoDePrearoField.setTextFormatter(formatter);
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
    void adiciona_ingrediente(ActionEvent event) {
      String quantidade = quantidade_ingrediente.getText();
    String unidade = unidadeDeMedida.getValue();
    String ingrediente = ingrediente_adicionado.getText();

     if (quantidade.isEmpty() || unidade == null || ingrediente.isEmpty()) {
        System.out.println("Preencha todos os campos!");
        return;
    }

      String ingredienteFormatado = quantidade + " " + unidade + " de " + ingrediente;
    listaIngredientes.add(ingredienteFormatado);

    ingrediente_adicionado.clear();
    quantidade_ingrediente.clear();
    unidadeDeMedida.setValue(null);

    }

    // fiz com os valores 1 , 2 e 3 ja que vi que outro metodo usava essa medida
       @FXML
    void definirDificuldade(ActionEvent event) {
      
      if(dificuldadeFacil.isSelected()){
        dificuldade = 1;
      } else if(dificuldadeMedio.isSelected()){
        dificuldade = 2;
      } else if( dificuldadeDificil.isSelected()){
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

   @FXML
    void adicionarTempoDePreparo(ActionEvent event) {
      String entrada = tempoDePrearoField.getText();

      int tempo = Integer.parseInt(entrada);

      tempoDePreparo = tempo;
      
      System.out.println("Tempo total de preparo: " + tempoDePreparo);
    }



}
