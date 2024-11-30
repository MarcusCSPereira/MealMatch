package com.mealmatch.jdbc.dao;

import java.io.ByteArrayInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.mealmatch.model.Ingrediente;
import com.mealmatch.model.Receita;
import com.mealmatch.model.ReceitaIngrediente;

import javafx.scene.control.Alert;
import javafx.scene.image.Image;

public class ReceitaDAO {

  private Connection connection;

  public ReceitaDAO(Connection connection) {
    this.connection = connection;
  }

  public List<Receita> findByName(String name) throws SQLException {
    if (name == null || name.trim().isEmpty()) {
      Alert alert = new Alert(Alert.AlertType.ERROR);
      alert.setTitle("Erro");
      alert.setHeaderText("Nome da receita inválido");
      alert.setContentText("O nome da receita não pode ser vazio.");
      alert.showAndWait();
      return new ArrayList<>();
    }

    String sql = "SELECT idreceita, nomereceita, modopreparo, tempopreparo, dificuldade, imagemreceita, numerolikes, numerodislikes, idtabela FROM receita WHERE nomereceita LIKE ?";
    List<Receita> receitas = new ArrayList<>();

    // Primeiro busca as receitas sem os ingredientes
    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
      stmt.setString(1, "%" + name.trim() + "%");
      try (ResultSet rs = stmt.executeQuery()) {
        while (rs.next()) {
          Receita receita = new Receita(
              rs.getInt("idreceita"),
              rs.getString("nomereceita"),
              rs.getString("modopreparo"),
              rs.getInt("tempopreparo"),
              rs.getInt("dificuldade"),
              rs.getBytes("imagemreceita") != null ? new Image(new ByteArrayInputStream(rs.getBytes("imagemreceita")))
                  : null,
              rs.getInt("numerolikes"),
              rs.getInt("numerodislikes"),
              rs.getInt("idtabela"));
          receitas.add(receita);
        }
      }
    }

    // Busca os ingredientes de cada receita e associa
    for (Receita receita : receitas) {
      receita.setIngredientesMapping(findIngredientes(receita));//Acha os ingredientes da receita e associa a ela
      receita.gerarStringIngredientes();// Gera a string de ingredientes formatada para processamento na view
      receita.gerarTabela();// Gera a tabela nutricional da receita
      receita.gerarValorNutricional();// Gera o valor nutricional da receita
      receita.setIdUsuarioDonoReceita(findUserIDOwnerReceipe(receita)); // Acha o id do usuário dono da receita
    }

    return receitas;
  }

  public HashMap<Ingrediente, ReceitaIngrediente> findIngredientes(Receita receita) throws SQLException {
    String sql = "SELECT i.nomeingrediente ,i.idingrediente ,i.caloria ,i.carboidrato ,i.gordura , i.proteina, ir.idreceita, ir.idingrediente ,ir.quantidade, ir.medida FROM receita AS r JOIN receitaingrediente AS ir ON r.idreceita = ir.idreceita JOIN ingrediente AS i ON ir.idingrediente = i.idingrediente WHERE r.idReceita = ?";
    HashMap<Ingrediente, ReceitaIngrediente> ingredientes_receita = new HashMap<>();
    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
      stmt.setInt(1, receita.getId());
      try (ResultSet rs = stmt.executeQuery()) {
        while (rs.next()) {
          Ingrediente ingrediente = new Ingrediente(
              rs.getInt("idingrediente"),
              rs.getString("nomeingrediente"),
              rs.getDouble("carboidrato"),
              rs.getDouble("gordura"),
              rs.getDouble("proteina"),
              rs.getDouble("caloria"));
          ReceitaIngrediente receitaIngrediente = new ReceitaIngrediente(
              rs.getInt("idreceita"),
              rs.getInt("idingrediente"),
              rs.getDouble("quantidade"),
              rs.getString("medida"));
          ingredientes_receita.put(ingrediente, receitaIngrediente);
        }
      }
    }
    return ingredientes_receita; // Retorna o HashMap de receitas
  }

  public int findUserIDOwnerReceipe(Receita receita){
    String sql = "SELECT idusuario FROM receita join criarreceita on receita.idreceita = criarreceita.idreceita WHERE criarreceita.idreceita = ?";
    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
      stmt.setInt(1, receita.getId());
      try (ResultSet rs = stmt.executeQuery()) {
        if (rs.next()) {
          return rs.getInt("idusuario");
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
        return 0;
  }

}
