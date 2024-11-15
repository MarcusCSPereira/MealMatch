package com.mealmatch.controller;

import com.mealmatch.model.Receita;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
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
    private Label tempo_receita_label;

    @FXML
    private Label valor_nutricional_label;

    @FXML
    void dislike_receipe(MouseEvent event) {
      System.out.println("Dislike na receita de id: " + getItem().getId());
    }

    @FXML
    void favorite_receipe(MouseEvent event) {
      System.out.println("Favoritar receita de id: " + getItem().getId());
    }

    @FXML
    void like_receipe(MouseEvent event) {
      System.out.println("Like na receita de id: " + getItem().getId());
    }

    @FXML
    void receipe_details(ActionEvent event) {
      System.out.println("Detalhes da receita de id: " + getItem().getId());
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
            tempo_receita_label.setText(String.valueOf(receita.getTempoPreparo()));
            valor_nutricional_label.setText(String.valueOf(receita.getValorNutricional()));
            numero_likes_label.setText(String.valueOf(receita.getNumeroLikes()));
            numero_dislike_label.setText(String.valueOf(receita.getNumeroDislikes()));
            selecionarDificuldade(receita.getDificuldade());
          
            setGraphic(screen_pane);
        }
    }

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
    
}
