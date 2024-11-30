package com.mealmatch.jdbc.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.mealmatch.model.Ingrediente;
import com.mealmatch.model.TabelaNutricionalIngrediente;

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

  public Ingrediente mapResultSetToIngrediente(ResultSet rs) throws SQLException {
    return new Ingrediente(
      rs.getInt("idingrediente"),
      rs.getString("nomeingrediente"),
      new TabelaNutricionalIngrediente(rs.getInt("caloria"), rs.getInt("carboidrato"), rs.getInt("proteina"), rs.getInt("gordura"), rs.getInt("idingrediente"))
    );
  }
  
}
