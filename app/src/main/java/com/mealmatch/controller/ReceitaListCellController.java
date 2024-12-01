package com.mealmatch.controller;

import java.io.IOException;
import com.mealmatch.model.Receita;
import com.mealmatch.utils.ControleDeSessao;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.scene.input.MouseEvent;

public class ReceitaListCellController extends ListCell<Receita> {

  @FXML
  private Button detail_button;

  @FXML
  private ImageView check_dislike_button;

  @FXML
  private ImageView check_like_button;

  @FXML
  private RadioButton dificil_dificuldade_toogle;

  @FXML
  private ToggleGroup dificuldade_receita;

  @FXML
  private ImageView dislike_button;

  @FXML
  private RadioButton facil_dificuldade_toogle;

  @FXML
  private ImageView favorite_button;

  @FXML
  private ImageView unfavorite_button;

  @FXML
  private ImageView like_button;

  @FXML
  private RadioButton medio_dificuldade_toggle;

  @FXML
  private Label nome_receita_label;

  @FXML
  private Label numero_dislike_label;

  @FXML
  private Label numero_likes_label;

  @FXML
  private ImageView receita_image;

  @FXML
  private Pane screen_pane;

  @FXML
  private ImageView edit_receipe_image;

  @FXML
  private Label tempo_receita_label;

  @FXML
  private Label valor_nutricional_label;

  Scene scene;
  Stage stage;

  @FXML
  void editar_receita(MouseEvent event) throws IOException {
    FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/tela_EditarReceita.fxml"));
    Parent root = loader.load();
    TelaEditarReceitaController detailsController = loader.getController();
    detailsController.setReceita(getItem());
    scene = new Scene(root);
    stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
    stage.setScene(scene);
    stage.show();
    System.out.println("Editar receita de id: " + getItem().getId());
  }

  @FXML
  void unfavorite_receipe(MouseEvent event) {
    System.out.println("Desfavoritou receita de id: " + getItem().getId());
    favorite_button.setVisible(true);
    unfavorite_button.setVisible(false);
  }

  @FXML
  void favorite_receipe(MouseEvent event) {
    System.out.println("Favoritou receita de id: " + getItem().getId());
    unfavorite_button.setVisible(true);
    favorite_button.setVisible(false);
  }

  @FXML
  void like_receipe(MouseEvent event) {
    if (!check_like_button.isVisible() && !check_dislike_button.isVisible()) {
      likeRecipe();
    } else if (check_dislike_button.isVisible()) {
      toggleDislikeToLike();
    } else {
      removeLike();
    }
  }

  @FXML
  void dislike_receipe(MouseEvent event) {
    if (!check_dislike_button.isVisible() && !check_like_button.isVisible()) {
      dislikeRecipe();
    } else if (check_like_button.isVisible()) {
      toggleLikeToDislike();
    } else {
      removeDislike();
    }
  }

  @FXML
  void receipe_details(ActionEvent event) throws IOException {
    FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/tela_detalhes_receita.fxml"));
    Parent root = loader.load();
    TelaDetalhesReceitaController detailsController = loader.getController();
    detailsController.setReceita(getItem());
    scene = new Scene(root);
    stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
    stage.hide();
    Stage novoStage = new Stage();
    novoStage.setOnHidden(e -> stage.show());
    novoStage.setScene(scene);
    novoStage.show();
  }

  public ReceitaListCellController() {
    super();
    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/views/listcell_receita.fxml"));
    fxmlLoader.setController(this);
    try {
      fxmlLoader.load();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Override
  protected void updateItem(Receita receita, boolean empty) {
    super.updateItem(receita, empty);

    if (empty || receita == null) {
      setGraphic(null);
    } else {
      receita_image.setImage(receita.getImagem());
      receita_image.setPreserveRatio(true);
      nome_receita_label.setText(receita.getNome());
      tempo_receita_label.setText(String.valueOf(formatarTempo(receita.getTempoPreparo())));
      valor_nutricional_label.setText(String.valueOf(receita.getValorNutricional() + " Kcal"));
      numero_likes_label.setText(String.valueOf(receita.getNumeroLikes()));
      numero_dislike_label.setText(String.valueOf(receita.getNumeroDislikes()));
      selecionarDificuldade(receita.getDificuldade());
      configureEditButton(receita);
      setGraphic(screen_pane);
    }
  }

  // Controla a aparição do botão de editar receita caso o usuário seja o dono da
  // receita
  private void configureEditButton(Receita receita) {
    if (ControleDeSessao.getInstance().getUserId() != receita.getIdUsuarioDonoReceita()
        || receita.getIdUsuarioDonoReceita() == 0) {
      edit_receipe_image.setVisible(false);
    } else {
      edit_receipe_image.setVisible(true);
    }
  }

  // Formata o tempo de preparo da receita para aparecer no formato h m s
  private String formatarTempo(int tempoPreparoEmSegundos) {
    int horas = tempoPreparoEmSegundos / 3600;
    int minutos = (tempoPreparoEmSegundos % 3600) / 60; 
    int segundos = tempoPreparoEmSegundos % 60; 
    return horas + " h " + minutos + " m " + segundos + " s";
  }

  // Seleciona a dificuldade da receita de acordo com o valor do banco de dados
  private void selecionarDificuldade(int dificuldade) {
    switch (dificuldade) {
      case 1:
        facil_dificuldade_toogle.setSelected(true);
        break;
      case 2:
        medio_dificuldade_toggle.setSelected(true);
        break;
      case 3:
        dificil_dificuldade_toogle.setSelected(true);
        break;
      default:
        break;
    }
  }

  private void likeRecipe() {
    System.out.println("Like na receita de id: " + getItem().getId());
    setLikeVisibility(true);
    updateLikes(1);
  }

  private void dislikeRecipe() {
    System.out.println("Dislike na receita de id: " + getItem().getId());
    setDislikeVisibility(true);
    updateDislikes(1);
  }

  private void toggleDislikeToLike() {
    System.out.println("Retira Deslike e coloca Like na receita de id: " + getItem().getId());
    setLikeVisibility(true);
    setDislikeVisibility(false);
    updateLikes(1);
    updateDislikes(-1);
  }

  private void toggleLikeToDislike() {
    System.out.println("Retira Like e coloca Deslike na receita de id: " + getItem().getId());
    setDislikeVisibility(true);
    setLikeVisibility(false);
    updateDislikes(1);
    updateLikes(-1);
  }

  private void removeLike() {
    System.out.println("Retira Like na receita de id: " + getItem().getId());
    setLikeVisibility(false);
    updateLikes(-1);
  }

  private void removeDislike() {
    System.out.println("Retira Deslike na receita de id: " + getItem().getId());
    setDislikeVisibility(false);
    updateDislikes(-1);
  }

  private void setLikeVisibility(boolean isVisible) {
    check_like_button.setVisible(isVisible);
  }

  private void setDislikeVisibility(boolean isVisible) {
    check_dislike_button.setVisible(isVisible);
  }

  private void updateLikes(int num) {
    numero_likes_label.setText(String.valueOf(Integer.parseInt(numero_likes_label.getText()) + num));
  }

  private void updateDislikes(int num) {
    numero_dislike_label.setText(String.valueOf(Integer.parseInt(numero_dislike_label.getText()) + num));
  }

}
