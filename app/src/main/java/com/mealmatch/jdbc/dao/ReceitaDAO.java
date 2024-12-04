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
import java.util.Map;
import java.util.stream.Collectors;

import com.mealmatch.model.Ingrediente;
import com.mealmatch.model.Receita;
import com.mealmatch.model.ReceitaIngrediente;

import javafx.scene.image.Image;

public class ReceitaDAO {

  private Connection connection;

  public ReceitaDAO(Connection connection) {
    this.connection = connection;
  }

  public List<Receita> findByName(String name) throws SQLException {

    String upperCaseName = name.toUpperCase();

    String sql = "SELECT idreceita, nomereceita, modopreparo, tempopreparo, dificuldade, imagemreceita, numerolikes, numerodislikes FROM receita WHERE nomereceita LIKE ?";
    List<Receita> receitas = new ArrayList<>();

    // Primeiro busca as receitas sem os ingredientes
    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
      stmt.setString(1, "%" + upperCaseName.trim() + "%");
      try (ResultSet rs = stmt.executeQuery()) {
        while (rs.next()) {
          Receita receita = new Receita(
              rs.getInt("idreceita"),
              formatName(rs.getString("nomereceita")),
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
      receita.setRestricoes(findRestricoes(receita)); // Acha as restrições da receita
    }

    return receitas;
  }

  private ArrayList<Integer> findRestricoes(Receita receita) {
    String sql = "SELECT idrestricao FROM receitarestricao WHERE idreceita = ?";
    ArrayList<Integer> restricoes = new ArrayList<>();
    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
      stmt.setInt(1, receita.getId());
      try (ResultSet rs = stmt.executeQuery()) {
        while (rs.next()) {
          restricoes.add(rs.getInt("idrestricao"));
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return restricoes;
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
              formatName(rs.getString("nomeingrediente")),
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
        statementReceita.setString(1, receita.getNome().toUpperCase());
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

  public void adicionarRestricaoNaReceita(int idReceita, int restricao) {
    String sql = "INSERT INTO receitarestricao (idrestricao, idreceita) VALUES (?, ?)";

    try (PreparedStatement statement = connection.prepareStatement(sql)) {
      statement.setInt(1, restricao);
      statement.setInt(2, idReceita);

      statement.execute();
      System.out.println("Restrição adicionada à receita com sucesso!");
    } catch (SQLException e) {
      System.err.println("Erro ao adicionar restrição na receita: " + e.getMessage());
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

  // Retorna a reação do usuário e se a receita é favoritada
  public Map<String, Object> getReacaoEFavorito(int idUsuario, int idReceita) throws SQLException {
    String sql = "SELECT " +
        "COALESCE(rr.reacao, 0) AS reacao, " +
        "CASE WHEN f.idusuario IS NOT NULL THEN true ELSE false END AS favoritado " +
        "FROM receita r " +
        "LEFT JOIN reagirreceita rr ON rr.idreceita = r.idreceita AND rr.idusuario = ? " +
        "LEFT JOIN favorito f ON f.idreceita = r.idreceita AND f.idusuario = ? " +
        "WHERE r.idreceita = ?;";

    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
      stmt.setInt(1, idUsuario);
      stmt.setInt(2, idUsuario);
      stmt.setInt(3, idReceita);

      try (ResultSet rs = stmt.executeQuery()) {
        if (rs.next()) {
          Map<String, Object> result = new HashMap<>();
          result.put("reacao", rs.getInt("reacao")); // Retorna 0 (nenhuma reação), 1 (dislike), ou 2 (like)
          result.put("favoritado", rs.getBoolean("favoritado")); // Retorna true ou false
          return result;
        }
      }
    }
    return null; // Nenhuma informação encontrada
  }

  public List<Receita> buscarReceitasPorIngredientes(List<String> ingredientesList) {
    String sql = "SELECT r.idreceita, r.nomereceita, r.modopreparo, r.tempopreparo, r.dificuldade, r.imagemreceita, r.numerolikes, r.numerodislikes FROM receita r JOIN receitaingrediente ri ON r.idreceita = ri.idreceita JOIN ingrediente i ON ri.idingrediente = i.idingrediente WHERE i.nomeingrediente IN ( "
        + ingredientesList.stream().map(ing -> "?").collect(Collectors.joining(","))
        + " ) GROUP BY r.idreceita, r.nomereceita, r.modopreparo, r.tempopreparo, r.dificuldade, r.imagemreceita, r.numerolikes, r.numerodislikes HAVING COUNT(DISTINCT i.nomeingrediente) = ?";

    List<Receita> receitas = new ArrayList<>();

    try (PreparedStatement stmt = this.connection.prepareStatement(sql)) {
      // Preenche os parâmetros com os ingredientes
      int index = 1;
      for (String ingrediente : ingredientesList) {
        stmt.setString(index++, ingrediente.trim().toUpperCase());
      }

      // Define o último parâmetro como o número de ingredientes fornecidos
      stmt.setInt(index, ingredientesList.size());

      // Executa a consulta e processa os resultados
      try (ResultSet rs = stmt.executeQuery()) {
        while (rs.next()) {
          Receita receita = new Receita(
              rs.getInt("idreceita"),
              formatName(rs.getString("nomereceita")),
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
      // Busca os ingredientes de cada receita e associa
      for (Receita receita : receitas) {
        receita.setIngredientesMapping(findIngredientes(receita));// Acha os ingredientes da receita e associa a ela
        receita.gerarStringIngredientes();// Gera a string de ingredientes formatada para processamento na view
        receita.gerarTabela();// Gera a tabela nutricional da receita
        receita.gerarValorNutricional();// Gera o valor nutricional da receita
        receita.setIdUsuarioDonoReceita(findUserIDOwnerReceipe(receita)); // Acha o id do usuário dono da receita
        receita.setRestricoes(findRestricoes(receita)); // Acha as restrições da receita
      }
    } catch (SQLException e) {
      throw new RuntimeException("Erro ao buscar receitas por ingredientes: " + e.getMessage(), e);
    }

    return receitas;
  }

  public void removerIngredienteDaReceita(int idReceita, int idIngrediente) throws SQLException {
    String sql = "DELETE FROM receitaingrediente WHERE idreceita = ? AND idingrediente = ?";
    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
      stmt.setInt(1, idReceita);
      stmt.setInt(2, idIngrediente);
      stmt.executeUpdate();
      System.out.println("Ingrediente removido com sucesso da receita.");
    }
  }

  public void atualizarReceita(Receita receita) throws SQLException {
    String sqlReceita = "UPDATE receita SET nomereceita = ?, modopreparo = ?, tempopreparo = ?, dificuldade = ? WHERE idreceita = ?";
    try (PreparedStatement stmt = connection.prepareStatement(sqlReceita)) {
      stmt.setString(1, receita.getNome().toUpperCase());
      stmt.setString(2, receita.getModoPreparo());
      stmt.setInt(3, receita.getTempoPreparo());
      stmt.setInt(4, receita.getDificuldade());
      stmt.setInt(5, receita.getId());
      stmt.executeUpdate();
    }

    // Atualizar as restrições
    atualizarRestricoesDaReceita(receita);

  }

  private void atualizarRestricoesDaReceita(Receita receita) throws SQLException {
    String sqlDelete = "DELETE FROM receitarestricao WHERE idreceita = ?";
    try (PreparedStatement stmt = connection.prepareStatement(sqlDelete)) {
      stmt.setInt(1, receita.getId());
      stmt.executeUpdate();
    }

    String sqlInsert = "INSERT INTO receitarestricao (idrestricao, idreceita) VALUES (?, ?)";
    try (PreparedStatement stmt = connection.prepareStatement(sqlInsert)) {
      for (Integer restricao : receita.getRestricoes()) {
        stmt.setInt(1, restricao);
        stmt.setInt(2, receita.getId());
        stmt.addBatch();
      }
      stmt.executeBatch();
    }
  }

  public void adicionarIngredienteNaReceita(int idReceita, String nomeIngrediente, Double quantidade, String unidade)
      throws SQLException {
    IngredienteDAO ingredienteDAO = new IngredienteDAO(connection);
    Ingrediente ingrediente = ingredienteDAO.getIngredienteByNome(nomeIngrediente);

    if (ingrediente == null) {
      throw new SQLException("Ingrediente não encontrado na base de dados.");
    }

    String sql = "INSERT INTO receitaingrediente (idreceita, idingrediente, quantidade, medida) VALUES (?, ?, ?, ?)";
    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
      stmt.setInt(1, idReceita);
      stmt.setInt(2, ingrediente.getId_ingrediente());
      stmt.setDouble(3, quantidade);
      stmt.setString(4, unidade);
      stmt.executeUpdate();
    }
  }

  // Método para formatar a string: primeira letra maiúscula, demais minúsculas
  private String formatName(String nome) {
    if (nome == null || nome.isEmpty()) {
      return nome;
    }
    return nome.substring(0, 1).toUpperCase() + nome.substring(1).toLowerCase();
  }

}
