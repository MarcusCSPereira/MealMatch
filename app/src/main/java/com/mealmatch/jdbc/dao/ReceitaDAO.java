package com.mealmatch.jdbc.dao;

import java.io.ByteArrayInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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

    String sql = "SELECT idreceita, nomereceita, modopreparo, tempopreparo, dificuldade, imagemreceita, numerolikes, numerodislikes FROM receita WHERE nomereceita LIKE ?";
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
              rs.getInt("numerodislikes"));
          receitas.add(receita);
        }
      }
    }

    // Busca os ingredientes de cada receita e associa
    for (Receita receita : receitas) {
      receita.setIngredientesMapping(findIngredientes(receita));// Acha os ingredientes da receita e associa a ela
      receita.gerarStringIngredientes();// Gera a string de ingredientes formatada para processamento na view
      receita.gerarTabela();// Gera a tabela nutricional da receita
      receita.gerarValorNutricional();// Gera o valor nutricional da receita
      receita.setIdUsuarioDonoReceita(findUserIDOwnerReceipe(receita)); // Acha o id do usuário dono da receita
    }

    return receitas;
  }

  // Encontra os ingredientes de uma receita
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

  // Encontra o id do usuário dono da receita
  public int findUserIDOwnerReceipe(Receita receita) {
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

  // Adiciona uma receita completa no banco de dados, incluindo a relação com o
  // usuário e a sua propria relação
  public int adicionarReceitaCompleta(Receita receita, byte[] imagemReceita, int idUsuario) {
    String sqlReceita = "INSERT INTO receita (nomereceita, numerolikes, numerodislikes, modopreparo, dificuldade, tempopreparo, imagemreceita) "
        +
        "VALUES (?, ?, ?, ?, ?, ?, ?)";
    String sqlCriaReceita = "INSERT INTO criarreceita (idusuario, idreceita) VALUES (?, ?)";

    int idReceitaGerada = 0;
    try {
      connection.setAutoCommit(false);

      // Inserção na tabela 'receitas'
      try (PreparedStatement statementReceita = connection.prepareStatement(sqlReceita,
          Statement.RETURN_GENERATED_KEYS)) {
        statementReceita.setString(1, receita.getNome());
        statementReceita.setInt(2, 0); // Inicializando com 0 likes
        statementReceita.setInt(3, 0); // Inicializando com 0 dislikes
        statementReceita.setString(4, receita.getModoPreparo());
        statementReceita.setInt(5, receita.getDificuldade());
        statementReceita.setInt(6, receita.getTempoPreparo());
        statementReceita.setBytes(7, imagemReceita);

        // Executa o comando de inserção
        int rowsInserted = statementReceita.executeUpdate();
        if (rowsInserted == 0) {
          throw new SQLException("Falha ao inserir receita, nenhuma linha afetada.");
        }

        // Recupera o ID gerado para a receita
        try (ResultSet generatedKeys = statementReceita.getGeneratedKeys()) {
          if (generatedKeys.next()) {
            idReceitaGerada = generatedKeys.getInt(1);
          } else {
            throw new SQLException("Falha ao inserir receita, ID não gerado.");
          }
        }
      }

      // Inserção na tabela 'criareceita'
      try (PreparedStatement statementCriaReceita = connection.prepareStatement(sqlCriaReceita)) {
        statementCriaReceita.setInt(1, idUsuario);
        statementCriaReceita.setInt(2, idReceitaGerada);

        // Executa o comando de inserção
        statementCriaReceita.executeUpdate();
      }

      // Confirma a transação
      connection.commit();
      System.out.println("Receita e relação com o usuário inseridas com sucesso!");

    } catch (SQLException e) {
      try {
        // Em caso de erro, desfaz a transação
        if (connection != null) {
          connection.rollback();
        }
      } catch (SQLException rollbackEx) {
        System.err.println("Erro ao realizar rollback: " + rollbackEx.getMessage());
      }
      System.err.println("Erro ao adicionar receita: " + e.getMessage());
      e.printStackTrace();
    }

    try {
      connection.setAutoCommit(true);
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return idReceitaGerada;

  }

  // Adiciona um ingrediente na receita baseado na relação entre a receita e o
  // ingrediente
  public void adicionarIngredienteNaReceita(int idReceita, int idIngrediente, Double quantidade, String unidade) {
    String sql = "INSERT INTO receitaingrediente (idreceita, idingrediente, quantidade, medida) VALUES (?, ?, ?, ?)";

    try (PreparedStatement statement = connection.prepareStatement(sql)) {
      statement.setInt(1, idReceita);
      statement.setInt(2, idIngrediente);
      statement.setDouble(3, quantidade);
      statement.setString(4, unidade);

      statement.execute();
      System.out.println("Ingrediente adicionado à receita com sucesso!");
    } catch (SQLException e) {
      System.err.println("Erro ao adicionar ingrediente na receita: " + e.getMessage());
    }
  }

  // Adiciona ou atualiza a reação do usuário
  public void reagirReceita(int idUsuario, int idReceita, int reacao) throws SQLException {
    String sql = "INSERT INTO reagirreceita (idusuario, idreceita, reacao) " +
        "VALUES (?, ?, ?) " +
        "ON CONFLICT (idusuario, idreceita) " +
        "DO UPDATE SET reacao = EXCLUDED.reacao;";

    try (PreparedStatement statement = connection.prepareStatement(sql)) {
      statement.setInt(1, idUsuario);
      statement.setInt(2, idReceita);
      statement.setInt(3, reacao);
      statement.executeUpdate();
    }
  }

  // Atualiza o contador de likes ou dislikes na receita
  public void atualizarLikesDislikes(int idReceita, String tipo, int valor) throws SQLException {
    String coluna = tipo.equals("like") ? "numerolikes" : "numerodislikes";
    String sql = "UPDATE receita SET " + coluna + " = " + coluna + " + ? WHERE idreceita = ?;";

    try (PreparedStatement statement = connection.prepareStatement(sql)) {
      statement.setInt(1, valor); // +1 ou -1
      statement.setInt(2, idReceita);
      statement.executeUpdate();
    }
  }

  public void marcarFavorito(int idUsuario, int idReceita) throws SQLException {
    String sql = "INSERT INTO favorito (idusuario, idreceita) VALUES (?, ?)";
    try (PreparedStatement statement = connection.prepareStatement(sql)) {
       statement.setInt(1, idUsuario);
       statement.setInt(2, idReceita);
       statement.executeUpdate();
    }
}

public void desmarcarFavorito(int idUsuario, int idReceita) throws SQLException {
    String sql = "DELETE FROM favorito WHERE idusuario = ? AND idreceita = ?";
    try (PreparedStatement statement = connection.prepareStatement(sql)) {
       statement.setInt(1, idUsuario);
       statement.setInt(2, idReceita);
       statement.executeUpdate();
    }
}

  // Retorna a reação do usuário a uma receita
  public int getReacaoUsuario(int idUsuario, int idReceita) throws SQLException {
    String sql = "SELECT reacao FROM reagirreceita WHERE idusuario = ? AND idreceita = ?;";
    try (PreparedStatement statement = connection.prepareStatement(sql)) {
        statement.setInt(1, idUsuario);
        statement.setInt(2, idReceita);
        try (ResultSet resultSet = statement.executeQuery()) {
            if (resultSet.next()) {
                return resultSet.getInt("reacao"); // Retorna "like", "dislike", ou null
            }
        }
    }
    return 0; // Nenhuma reação encontrada
}

public boolean isFavoritada(int idUsuario, int idReceita) throws SQLException {
    String sql = "SELECT 1 FROM favorito WHERE idusuario = ? AND idreceita = ?;";
    try (PreparedStatement statement = connection.prepareStatement(sql)) {
        statement.setInt(1, idUsuario);
        statement.setInt(2, idReceita);
        try (ResultSet resultSet = statement.executeQuery()) {
            return resultSet.next(); // Retorna true se encontrou um registro
        }
    }
}



}
