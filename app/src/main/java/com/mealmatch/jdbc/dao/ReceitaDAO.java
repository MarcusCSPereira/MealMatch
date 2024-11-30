package com.mealmatch.jdbc.dao;

import java.io.ByteArrayInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.mealmatch.model.Receita;

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

    String sql = "SELECT * FROM receita WHERE nomereceita LIKE ?";
    List<Receita> receitas = new ArrayList<>();

    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
      stmt.setString(1, "%" + name.trim() + "%"); // Utiliza o operador LIKE com wildcard
      try (ResultSet rs = stmt.executeQuery()) {
        while (rs.next()) {
          receitas.add(mapResultSetToReceita(rs)); // Adiciona cada receita encontrada à lista
        }
      }
    }
    return receitas; // Retorna a lista de receitas
  }

  private Receita mapResultSetToReceita(ResultSet rs) throws SQLException {
    return new Receita(
        rs.getInt("idreceita"),
        rs.getString("nomereceita"),
        rs.getString("modopreparo"),
        rs.getInt("tempopreparo"),
        rs.getInt("dificuldade"),
        rs.getBytes("imagemreceita") != null ? new Image(new ByteArrayInputStream(rs.getBytes("imagem"))) : null,
        rs.getInt("numerolikes"),
        rs.getInt("numerodislikes"),
        rs.getInt("idtabela"));
  }

}
