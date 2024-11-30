package com.mealmatch.jdbc.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import com.mealmatch.model.Ingrediente;
import com.mealmatch.model.ReceitaIngrediente;
import com.mealmatch.model.TabelaNutricionalIngrediente;

public class ReceitaIngredienteDAO {

  private Connection connection;

  public ReceitaIngredienteDAO(Connection connection) {
    this.connection = connection;
  }

  public ReceitaIngrediente mapResultSetToReceitaIngrediente(ResultSet rs) throws SQLException {
    return new ReceitaIngrediente(
        rs.getInt("idreceita"),
        rs.getInt("idingrediente"),
        rs.getDouble("quantidade"),
        rs.getString("unidademedida"));
  }

  // Método para buscar os ingredientes de uma receita
  public Map<Ingrediente, ReceitaIngrediente> buscarIngredientesPorReceita(Integer idReceita) throws SQLException {
    String sql = "SELECT i.idingrediente, i.nomeingrediente, i.caloria, i.carboidrato, i.gordura, i.proteina, " +
        "ri.quantidade, ri.medida " +
        "FROM ingrediente i " +
        "JOIN receitaingrediente ri ON i.idingrediente = ri.idingrediente " +
        "WHERE ri.idreceita = ?";

    Map<Ingrediente, ReceitaIngrediente> ingredientesMapping = new HashMap<>();

    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
      stmt.setInt(1, idReceita);

      try (ResultSet rs = stmt.executeQuery()) {
        while (rs.next()) {
          // Cria o objeto Ingrediente
          Ingrediente ingrediente = new Ingrediente(
              rs.getInt("idingrediente"),
              rs.getString("nomeingrediente"),
              new TabelaNutricionalIngrediente(
                  rs.getInt("caloria"),
                  rs.getInt("carboidrato"),
                  rs.getInt("proteina"),
                  rs.getInt("gordura"),
                  rs.getInt("idingrediente")
                )
            );

          // Recupera a quantidade associada à relação
          double quantidade = rs.getDouble("quantidade");
          String unidadeMedida = rs.getString("medida");

          // Adiciona ao HashMap
          ingredientesMapping.put(ingrediente, new ReceitaIngrediente(idReceita, ingrediente.getId_ingrediente(), quantidade, unidadeMedida));
        }
      }
    }
    return ingredientesMapping;
  }
}