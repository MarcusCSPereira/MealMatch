package com.mealmatch.jdbc.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.mealmatch.model.Ingrediente;

public class IngredienteDAO {

  private Connection connection;

  public IngredienteDAO(Connection connection) {
    this.connection = connection;
  }

  public Ingrediente getIngredienteById(int idingrediente) throws SQLException {
    String sql = "SELECT * FROM ingrediente WHERE idingrediente = ?";
    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
      stmt.setInt(1, idingrediente);
      try (ResultSet rs = stmt.executeQuery()) {
        if (rs.next()) {
          return mapResultSetToIngrediente(rs);
        }
      }
    }
    return null;
  }

  // Método de busca de ingredientes por nome com ajuste para maiúsculas
  public Ingrediente getIngredienteByNome(String nome) throws SQLException {
    String sql = "SELECT * FROM ingrediente WHERE nomeingrediente = ?";
    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
      stmt.setString(1, nome.toUpperCase()); // Converte para maiúsculas antes da busca
      try (ResultSet rs = stmt.executeQuery()) {
        if (rs.next()) {
          return mapResultSetToIngrediente(rs);
        }
      }
    }
    return null;
  }

  // Mapeia o ResultSet para um objeto Ingrediente e formata o nome
  private Ingrediente mapResultSetToIngrediente(ResultSet rs) throws SQLException {
    Ingrediente ingrediente = new Ingrediente();
    ingrediente.setCaloria(rs.getDouble("caloria"));
    ingrediente.setCarboidrato(rs.getDouble("carboidrato"));
    ingrediente.setGordura(rs.getDouble("gordura"));
    ingrediente.setProteina(rs.getDouble("proteina"));
    ingrediente.setNomeIngrediente(formatName(rs.getString("nomeingrediente"))); // Formata o nome
    ingrediente.setId_ingrediente(rs.getInt("idingrediente"));

    return ingrediente;
  }

  // Método para formatar a string: primeira letra maiúscula, demais minúsculas
  private String formatName(String nome) {
    if (nome == null || nome.isEmpty()) {
      return nome;
    }
    return nome.substring(0, 1).toUpperCase() + nome.substring(1).toLowerCase();
  }
}