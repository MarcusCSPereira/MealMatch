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

    String sql = "SELECT id_receita, nome_receita, modo_preparo, tempo_preparo, dificuldade, imagemreceita, numero_likes, numero_dislikes FROM receita WHERE nome_receita LIKE ?";
    List<Receita> receitas = new ArrayList<>();

    // Primeiro busca as receitas sem os ingredientes
    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
      stmt.setString(1, "%" + upperCaseName.trim() + "%");
      try (ResultSet rs = stmt.executeQuery()) {
        while (rs.next()) {
          Receita receita = new Receita(
              rs.getInt("id_receita"),
              formatName(rs.getString("nome_receita")),
              rs.getString("modo_preparo"),
              rs.getInt("tempo_preparo"),
              rs.getInt("dificuldade"),
              rs.getBytes("imagemreceita") != null ? new Image(new ByteArrayInputStream(rs.getBytes("imagemreceita")))
                  : null,
              rs.getInt("numero_likes"),
              rs.getInt("numero_dislikes"));
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
    String sql = "SELECT id_restricao FROM receita_restricao WHERE id_receita = ?";
    ArrayList<Integer> restricoes = new ArrayList<>();
    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
      stmt.setInt(1, receita.getId());
      try (ResultSet rs = stmt.executeQuery()) {
        while (rs.next()) {
          restricoes.add(rs.getInt("id_restricao"));
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return restricoes;
  }

  // Encontra os ingredientes de uma receita
  public HashMap<Ingrediente, ReceitaIngrediente> findIngredientes(Receita receita) throws SQLException {
    String sql = "SELECT i.nome_ingrediente ,i.id_ingrediente ,i.caloria ,i.carboidrato ,i.gordura , i.proteina, ir.id_receita, ir.id_ingrediente ,ir.quantidade, ir.unidade_medida FROM receita AS r JOIN receita_ingrediente AS ir ON r.id_receita = ir.id_receita JOIN ingrediente AS i ON ir.id_ingrediente = i.id_ingrediente WHERE r.id_Receita = ?";
    HashMap<Ingrediente, ReceitaIngrediente> ingredientes_receita = new HashMap<>();
    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
      stmt.setInt(1, receita.getId());
      try (ResultSet rs = stmt.executeQuery()) {
        while (rs.next()) {
          Ingrediente ingrediente = new Ingrediente(
              rs.getInt("id_ingrediente"),
              formatName(rs.getString("nome_ingrediente")),
              rs.getDouble("carboidrato"),
              rs.getDouble("gordura"),
              rs.getDouble("proteina"),
              rs.getDouble("caloria"));
          ReceitaIngrediente receitaIngrediente = new ReceitaIngrediente(
              rs.getInt("id_receita"),
              rs.getInt("id_ingrediente"),
              rs.getDouble("quantidade"),
              rs.getString("unidade_medida"));
          ingredientes_receita.put(ingrediente, receitaIngrediente);
        }
      }
    }
    return ingredientes_receita; // Retorna o HashMap de receitas
  }

  // Encontra o id do usuário dono da receita
  public int findUserIDOwnerReceipe(Receita receita) {
    String sql = "SELECT id_usuario FROM receita join criar_receita on receita.id_receita = criar_receita.id_receita WHERE criar_receita.id_receita = ?";
    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
      stmt.setInt(1, receita.getId());
      try (ResultSet rs = stmt.executeQuery()) {
        if (rs.next()) {
          return rs.getInt("id_usuario");
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
    String sqlReceita = "INSERT INTO receita (nome_receita, numero_likes, numero_dislikes, modo_preparo, dificuldade, tempo_preparo, imagemreceita) "
        +
        "VALUES (?, ?, ?, ?, ?, ?, ?)";
    String sqlCriaReceita = "INSERT INTO criar_receita (id_usuario, id_receita) VALUES (?, ?)";

    int id_ReceitaGerada = 0;
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
            id_ReceitaGerada = generatedKeys.getInt(1);
          } else {
            throw new SQLException("Falha ao inserir receita, ID não gerado.");
          }
        }
      }

      // Inserção na tabela 'criareceita'
      try (PreparedStatement statementCriaReceita = connection.prepareStatement(sqlCriaReceita)) {
        statementCriaReceita.setInt(1, idUsuario);
        statementCriaReceita.setInt(2, id_ReceitaGerada);

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
    return id_ReceitaGerada;

  }

  // Adiciona um ingrediente na receita baseado na relação entre a receita e o
  // ingrediente
  public void adicionarIngredienteNaReceita(int id_Receita, int idIngrediente, Double quantidade, String unidade) {
    String sql = "INSERT INTO receita_ingrediente (id_receita, id_ingrediente, quantidade, unidade_medida) VALUES (?, ?, ?, ?)";

    try (PreparedStatement statement = connection.prepareStatement(sql)) {
      statement.setInt(1, id_Receita);
      statement.setInt(2, idIngrediente);
      statement.setDouble(3, quantidade);
      statement.setString(4, unidade);

      statement.execute();
      System.out.println("Ingrediente adicionado à receita com sucesso!");
    } catch (SQLException e) {
      System.err.println("Erro ao adicionar ingrediente na receita: " + e.getMessage());
    }
  }

  public void adicionarRestricaoNaReceita(int id_Receita, int restricao) {
    String sql = "INSERT INTO receita_restricao (id_restricao, id_receita) VALUES (?, ?)";

    try (PreparedStatement statement = connection.prepareStatement(sql)) {
      statement.setInt(1, restricao);
      statement.setInt(2, id_Receita);

      statement.execute();
      System.out.println("Restrição adicionada à receita com sucesso!");
    } catch (SQLException e) {
      System.err.println("Erro ao adicionar restrição na receita: " + e.getMessage());
    }
  }

  // Adiciona ou atualiza a reação do usuário
  public void reagirReceita(int idUsuario, int id_Receita, int reacao) throws SQLException {
    String sql = "INSERT INTO reagir_receita (id_usuario, id_receita, reacao) " +
        "VALUES (?, ?, ?) " +
        "ON CONFLICT (id_usuario, id_receita) " +
        "DO UPDATE SET reacao = EXCLUDED.reacao;";

    try (PreparedStatement statement = connection.prepareStatement(sql)) {
      statement.setInt(1, idUsuario);
      statement.setInt(2, id_Receita);
      statement.setInt(3, reacao);
      statement.executeUpdate();
    }
  }

  // Atualiza o contador de likes ou dislikes na receita
  public void atualizarLikesDislikes(int id_Receita, String tipo, int valor) throws SQLException {
    String coluna = tipo.equals("like") ? "numero_likes" : "numero_dislikes";
    String sql = "UPDATE receita SET " + coluna + " = " + coluna + " + ? WHERE id_receita = ?;";

    try (PreparedStatement statement = connection.prepareStatement(sql)) {
      statement.setInt(1, valor); // +1 ou -1
      statement.setInt(2, id_Receita);
      statement.executeUpdate();
    }
  }

  public void marcarFavorito(int idUsuario, int id_Receita) throws SQLException {
    String sql = "INSERT INTO favoritar_receita (id_usuario, id_receita) VALUES (?, ?)";
    try (PreparedStatement statement = connection.prepareStatement(sql)) {
      statement.setInt(1, idUsuario);
      statement.setInt(2, id_Receita);
      statement.executeUpdate();
    }
  }

  public void desmarcarFavorito(int idUsuario, int id_Receita) throws SQLException {
    String sql = "DELETE FROM favoritar_receita WHERE id_usuario = ? AND id_receita = ?";
    try (PreparedStatement statement = connection.prepareStatement(sql)) {
      statement.setInt(1, idUsuario);
      statement.setInt(2, id_Receita);
      statement.executeUpdate();
    }
  }

  // Retorna a reação do usuário e se a receita é favoritada
  public Map<String, Object> getReacaoEFavorito(int idUsuario, int id_Receita) throws SQLException {
    String sql = "SELECT " +
        "COALESCE(rr.reacao, 0) AS reacao, " +
        "CASE WHEN f.id_usuario IS NOT NULL THEN true ELSE false END AS favoritado " +
        "FROM receita r " +
        "LEFT JOIN reagir_receita rr ON rr.id_receita = r.id_receita AND rr.id_usuario = ? " +
        "LEFT JOIN favoritar_receita f ON f.id_receita = r.id_receita AND f.id_usuario = ? " +
        "WHERE r.id_receita = ?;";

    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
      stmt.setInt(1, idUsuario);
      stmt.setInt(2, idUsuario);
      stmt.setInt(3, id_Receita);

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
    String sql = "SELECT r.id_receita, r.nome_receita, r.modo_preparo, r.tempo_preparo, r.dificuldade, r.imagemreceita, r.numero_likes, r.numero_dislikes FROM receita r JOIN receita_ingrediente ri ON r.id_receita = ri.id_receita JOIN ingrediente i ON ri.id_ingrediente = i.id_ingrediente WHERE i.nome_ingrediente IN ( "
        + ingredientesList.stream().map(ing -> "?").collect(Collectors.joining(","))
        + " ) GROUP BY r.id_receita, r.nome_receita, r.modo_preparo, r.tempo_preparo, r.dificuldade, r.imagemreceita, r.numero_likes, r.numero_dislikes HAVING COUNT(DISTINCT i.nome_ingrediente) = ?";

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
              rs.getInt("id_receita"),
              formatName(rs.getString("nome_receita")),
              rs.getString("modo_preparo"),
              rs.getInt("tempo_preparo"),
              rs.getInt("dificuldade"),
              rs.getBytes("imagemreceita") != null ? new Image(new ByteArrayInputStream(rs.getBytes("imagemreceita")))
                  : null,
              rs.getInt("numero_likes"),
              rs.getInt("numero_dislikes"));
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

  public void removerIngredienteDaReceita(int id_Receita, int idIngrediente) throws SQLException {
    String sql = "DELETE FROM receita_ingrediente WHERE id_receita = ? AND id_ingrediente = ?";
    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
      stmt.setInt(1, id_Receita);
      stmt.setInt(2, idIngrediente);
      stmt.executeUpdate();
      System.out.println("Ingrediente removido com sucesso da receita.");
    }
  }

  public void atualizarReceita(Receita receita) throws SQLException {
    String sqlReceita = "UPDATE receita SET nome_receita = ?, modo_preparo = ?, tempo_preparo = ?, dificuldade = ? WHERE id_receita = ?";
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
    String sqlDelete = "DELETE FROM receita_restricao WHERE id_receita = ?";
    try (PreparedStatement stmt = connection.prepareStatement(sqlDelete)) {
      stmt.setInt(1, receita.getId());
      stmt.executeUpdate();
    }

    String sqlInsert = "INSERT INTO receita_restricao (id_restricao, id_receita) VALUES (?, ?)";
    try (PreparedStatement stmt = connection.prepareStatement(sqlInsert)) {
      for (Integer restricao : receita.getRestricoes()) {
        stmt.setInt(1, restricao);
        stmt.setInt(2, receita.getId());
        stmt.addBatch();
      }
      stmt.executeBatch();
    }
  }

  public void adicionarIngredienteNaReceita(int id_Receita, String nomeIngrediente, Double quantidade, String unidade)
      throws SQLException {
    IngredienteDAO ingredienteDAO = new IngredienteDAO(connection);
    Ingrediente ingrediente = ingredienteDAO.getIngredienteByNome(nomeIngrediente);

    if (ingrediente == null) {
      throw new SQLException("Ingrediente não encontrado na base de dados.");
    }

    String sql = "INSERT INTO receita_ingrediente (id_receita, id_ingrediente, quantidade, unidade_medida) VALUES (?, ?, ?, ?)";
    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
      stmt.setInt(1, id_Receita);
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

  public List<Receita> buscarReceitasFavoritasComFiltro(int idUsuario, String termoBusca, boolean buscarPorIngredientes)
      throws SQLException {
    String sql;
    List<Receita> receitas = new ArrayList<>();

    if (buscarPorIngredientes) {
      // Busca por ingredientes favoritos
      sql = "SELECT DISTINCT r.id_receita, r.nome_receita, r.modo_preparo, r.tempo_preparo, r.dificuldade, r.imagemreceita, r.numero_likes, r.numero_dislikes "
          +
          "FROM receita r " +
          "JOIN favoritar_receita f ON r.id_receita = f.id_receita " +
          "JOIN receita_ingrediente ri ON r.id_receita = ri.id_receita " +
          "JOIN ingrediente i ON ri.id_ingrediente = i.id_ingrediente " +
          "WHERE f.id_usuario = ? AND i.nome_ingrediente LIKE ? " +
          "GROUP BY r.id_receita";
    } else {
      // Busca por nome de receita favorita
      sql = "SELECT r.id_receita, r.nome_receita, r.modo_preparo, r.tempo_preparo, r.dificuldade, r.imagemreceita, r.numero_likes, r.numero_dislikes "
          +
          "FROM receita r " +
          "JOIN favoritar_receita f ON r.id_receita = f.id_receita " +
          "WHERE f.id_usuario = ? AND r.nome_receita LIKE ?";
    }

    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
      stmt.setInt(1, idUsuario);
      stmt.setString(2, "%" + termoBusca.trim().toUpperCase() + "%");

      try (ResultSet rs = stmt.executeQuery()) {
        while (rs.next()) {
          Receita receita = new Receita(
              rs.getInt("id_receita"),
              formatName(rs.getString("nome_receita")),
              rs.getString("modo_preparo"),
              rs.getInt("tempo_preparo"),
              rs.getInt("dificuldade"),
              rs.getBytes("imagemreceita") != null ? new Image(new ByteArrayInputStream(rs.getBytes("imagemreceita")))
                  : null,
              rs.getInt("numero_likes"),
              rs.getInt("numero_dislikes"));
          receitas.add(receita);
        }
      }
    }

    // Associa informações adicionais às receitas
    for (Receita receita : receitas) {
      receita.setIngredientesMapping(findIngredientes(receita));
      receita.gerarStringIngredientes();
      receita.gerarTabela();
      receita.gerarValorNutricional();
      receita.setIdUsuarioDonoReceita(findUserIDOwnerReceipe(receita));
      receita.setRestricoes(findRestricoes(receita));
    }

    return receitas;
  }

}
