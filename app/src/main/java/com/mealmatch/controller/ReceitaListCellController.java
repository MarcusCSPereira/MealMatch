package com.mealmatch.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import com.mealmatch.enums.ReacaoEnum;
import com.mealmatch.jdbc.connection.ConnectionFactory;
import com.mealmatch.jdbc.dao.ReceitaDAO;
import com.mealmatch.model.Receita;
import com.mealmatch.utils.ControleDeSessao;

import javafx.animation.PauseTransition;
import javafx.concurrent.Task;
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
import javafx.util.Duration;
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

  private final Map<Integer, Boolean> debounceMap = new HashMap<>(); // Controle de debounce
  private final int DEBOUNCE_DELAY = 1000; // Delay em milissegundos (1 segundo)

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
      configurarReacaoUsuario(receita);

      setGraphic(screen_pane);
    }
  }

  // Busca a reação do usuário para a receita e configura os botões de like e
  // dislike com uso de Threads
  private void configurarReacaoUsuario(Receita receita) {
    int idUsuario = ControleDeSessao.getInstance().getUserId();
    int idReceita = receita.getId();

    Task<Integer> reacaoTask = new Task<Integer>() {
      @Override
      protected Integer call() throws SQLException {
        ReceitaDAO dao = new ReceitaDAO(ConnectionFactory.getConnection());
        return dao.getReacaoUsuario(idUsuario, idReceita); // Retorna 0 (null), 1 (dislike) ou 2 (like)
      }
    };

    // Define o que acontece quando a tarefa é concluída
    reacaoTask.setOnSucceeded(event -> {
      Integer reacao = reacaoTask.getValue(); // Obtém o valor retornado pela consulta

      // Atualiza os botões de acordo com o valor da reação
      if (reacao == ReacaoEnum.LIKE.getValor()) {
        setLikeVisibility(true);
        setDislikeVisibility(false);
      } else if (reacao == ReacaoEnum.DISLIKE.getValor()) {
        setLikeVisibility(false);
        setDislikeVisibility(true);
      } else {
        setLikeVisibility(false);
        setDislikeVisibility(false);
      }
    });

    reacaoTask.setOnFailed(event -> {
      System.out.println("Erro ao buscar reação do usuário para a receita de id: " + idReceita);
      Throwable exception = reacaoTask.getException();
      exception.printStackTrace();
    });

    // Inicia as tarefas em uma nova thread
    Thread thread = new Thread(reacaoTask);
    thread.setDaemon(true); // Faz com que a thread pare ao fechar a aplicação
    thread.start();
  }

  private void handleDebounce(int idReceita, Runnable action) {
    if (debounceMap.getOrDefault(idReceita, false)) {
      System.out.println("Aguarde antes de clicar novamente.");
      return;
    }
    debounceMap.put(idReceita, true); // Bloqueia novas ações para essa receita
    // Executa a ação
    action.run();

    // Libera a ação após o delay
    PauseTransition pause = new PauseTransition(Duration.millis(DEBOUNCE_DELAY));
    pause.setOnFinished(event -> debounceMap.put(idReceita, false));
    pause.play();
  }
  
  @FXML
  void like_receipe(MouseEvent event) {
    handleDebounce(getItem().getId(), () -> {
      if (!check_like_button.isVisible() && !check_dislike_button.isVisible()) {
        likeRecipe();
      } else if (check_dislike_button.isVisible()) {
        toggleDislikeToLike();
      } else {
        removeLike();
      }
    });
  }

  @FXML
  void dislike_receipe(MouseEvent event) {
    handleDebounce(getItem().getId(), () -> {
      if (!check_dislike_button.isVisible() && !check_like_button.isVisible()) {
        dislikeRecipe();
      } else if (check_like_button.isVisible()) {
        toggleLikeToDislike();
      } else {
        removeDislike();
      }
    });
  }

  @FXML
  void editar_receita(MouseEvent event) {
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
    int idUsuario = ControleDeSessao.getInstance().getUserId(); // Usuário logado
    int idReceita = getItem().getId(); // Receita selecionada

    try {
      ReceitaDAO dao = new ReceitaDAO(ConnectionFactory.getConnection());
      dao.reagirReceita(idUsuario, idReceita, ReacaoEnum.LIKE.getValor()); // Registra o like
      dao.atualizarLikesDislikes(idReceita, "like", 1); // Incrementa o contador de likes

      setLikeVisibility(true);
      setDislikeVisibility(false); // Remove o dislike, se existir
      updateLikes(1);
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  private void dislikeRecipe() {
    int idUsuario = ControleDeSessao.getInstance().getUserId(); // Usuário logado
    int idReceita = getItem().getId(); // Receita selecionada

    try {
      ReceitaDAO dao = new ReceitaDAO(ConnectionFactory.getConnection());
      dao.reagirReceita(idUsuario, idReceita, ReacaoEnum.DISLIKE.getValor()); // Registra o dislike
      dao.atualizarLikesDislikes(idReceita, "dislike", 1); // Incrementa o contador de dislikes

      setDislikeVisibility(true);
      setLikeVisibility(false); // Remove o like, se existir
      updateDislikes(1);
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  private void toggleDislikeToLike() {
    int idUsuario = ControleDeSessao.getInstance().getUserId();
    int idReceita = getItem().getId();

    System.out.println("Retira Dislike e coloca Like na receita de id: " + idReceita);

    try {
      ReceitaDAO dao = new ReceitaDAO(ConnectionFactory.getConnection());

      // Atualiza a reação no banco
      dao.reagirReceita(idUsuario, idReceita, ReacaoEnum.LIKE.getValor());

      // Atualiza os contadores na tabela 'receita'
      dao.atualizarLikesDislikes(idReceita, "like", 1); // Incrementa likes
      dao.atualizarLikesDislikes(idReceita, "dislike", -1); // Decrementa dislikes

      // Atualiza o estado visual
      setLikeVisibility(true);
      setDislikeVisibility(false);

      updateDislikes(-1);
      updateLikes(1);
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  private void toggleLikeToDislike() {
    int idUsuario = ControleDeSessao.getInstance().getUserId();
    int idReceita = getItem().getId();

    System.out.println("Retira Like e coloca Dislike na receita de id: " + idReceita);

    try {
      ReceitaDAO dao = new ReceitaDAO(ConnectionFactory.getConnection());

      // Atualiza a reação no banco
      dao.reagirReceita(idUsuario, idReceita, ReacaoEnum.DISLIKE.getValor());

      // Atualiza os contadores na tabela 'receita'
      dao.atualizarLikesDislikes(idReceita, "dislike", 1); // Incrementa dislikes
      dao.atualizarLikesDislikes(idReceita, "like", -1); // Decrementa likes

      // Atualiza o estado visual
      setDislikeVisibility(true);
      setLikeVisibility(false);

      updateLikes(-1);
      updateDislikes(1);
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  private void removeLike() {
    int idUsuario = ControleDeSessao.getInstance().getUserId();
    int idReceita = getItem().getId();

    try {
      ReceitaDAO dao = new ReceitaDAO(ConnectionFactory.getConnection());
      dao.reagirReceita(idUsuario, idReceita, ReacaoEnum.NULO.getValor()); // Remove a reação
      dao.atualizarLikesDislikes(idReceita, "like", -1); // Decrementa o contador de likes

      setLikeVisibility(false);
      updateLikes(-1);
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  private void removeDislike() {
    int idUsuario = ControleDeSessao.getInstance().getUserId();
    int idReceita = getItem().getId();

    try {
      ReceitaDAO dao = new ReceitaDAO(ConnectionFactory.getConnection());
      dao.reagirReceita(idUsuario, idReceita, ReacaoEnum.NULO.getValor()); // Remove a reação
      dao.atualizarLikesDislikes(idReceita, "dislike", -1); // Decrementa o contador de likes

      setDislikeVisibility(false);
      updateDislikes(-1);
    } catch (SQLException e) {
      e.printStackTrace();
    }
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
