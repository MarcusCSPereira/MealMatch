package com.mealmatch.jdbc.dao;

import com.mealmatch.model.User;
import com.mealmatch.utils.EncriptadorDeSenhas;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import java.io.ByteArrayInputStream;

import javafx.scene.control.Alert;
import javafx.scene.image.Image;

public class UserDAO {

  private Connection connection;

  public UserDAO(Connection connection) {
    this.connection = connection;
  }

  // Método auxiliar para mapear um ResultSet para um objeto User
  private User mapResultSetToUser(ResultSet rs) throws SQLException {
    return new User(
        rs.getInt("idusuario"),
        rs.getString("nomecompleto"),
        rs.getString("email"),
        rs.getString("nomeusuario"),
        rs.getString("senha"),
        rs.getDate("datanascimento"),
        User.Sex.valueOf(rs.getString("sexo")),
        rs.getBytes("fotoperfil") != null ? new Image(new ByteArrayInputStream(rs.getBytes("fotoperfil"))): null);
  }

  // Método para inserir um novo usuário
  public void save(User user) throws SQLException {
    String sql = "INSERT INTO usuario (nomecompleto, nomeusuario, email, senha, sexo, datanascimento) VALUES (?, ?, ?, ?, ?, ?)";
    try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
      stmt.setString(1, user.getCompleteName());
      stmt.setString(2, user.getUsername());
      stmt.setString(3, user.getEmail());
      stmt.setString(4, user.getPassword());
      stmt.setString(5, user.getSex().name());
      stmt.setDate(6, user.getDate()); // Enum convertido para String

      stmt.executeUpdate();

      // Recuperar o ID gerado automaticamente
      try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
        if (generatedKeys.next()) {
          user.setId(generatedKeys.getInt(1));
        }
      }
    }
  }

  // Método para buscar um usuário pelo ID
  public User findById(int id) throws SQLException {
    String sql = "SELECT * FROM usuario WHERE idusuario = ?";
    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
      stmt.setInt(1, id);
      try (ResultSet rs = stmt.executeQuery()) {
        if (rs.next()) {
          return mapResultSetToUser(rs);
        }
      }
    }
    return null; // Se não encontrar o usuário
  }

  // Método para listar todos os usuários
  public List<User> findAll() throws SQLException {
    String sql = "SELECT * FROM usuario";
    List<User> users = new ArrayList<>();
    try (PreparedStatement stmt = connection.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery()) {

      while (rs.next()) {
        users.add(mapResultSetToUser(rs));
      }
    }
    return users;
  }

  // Método para atualizar um usuário
  public void update(User user) throws SQLException {
    String sql = "UPDATE usuario SET nomecompleto = ?, nomeusuario = ?, email = ?, senha = ?, sexo = ?, datanascimento = ? WHERE idusuario = ?";
    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
      stmt.setString(1, user.getCompleteName());
      stmt.setString(2, user.getUsername());
      stmt.setString(3, user.getEmail());
      stmt.setString(4, user.getPassword());
      stmt.setString(5, user.getSex().name());
      stmt.setDate(6, user.getDate());
      stmt.setInt(7, user.getId());

      stmt.executeUpdate();
    }
  }

  // Método para deletar um usuário pelo ID
  public void delete(int id) throws SQLException {
    String sql = "DELETE FROM usuario WHERE idusuario = ?";
    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
      stmt.setInt(1, id);
      stmt.executeUpdate();
    }
  }

  public boolean validarLogin(String nomeUsuario, String senha) {
    String sql = "SELECT senha FROM usuario WHERE nomeusuario = ?";

    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
      // Configura o parâmetro para o nome de usuário
      stmt.setString(1, nomeUsuario);

      try (ResultSet rs = stmt.executeQuery()) {
        if (rs.next()) {
          // Recupera a senha armazenada no banco de dados
          String senhaHashArmazenada = rs.getString("senha");

          // Verifica se a senha informada corresponde ao hash armazenado
          return EncriptadorDeSenhas.checkPassword(senha, senhaHashArmazenada);
        }
      }
    } catch (SQLException e) {
      Alert alert = new Alert(Alert.AlertType.ERROR);
      alert.setTitle("Erro");
      alert.setHeaderText("Erro ao validar login");
      alert.setContentText("Verifique sua conexão com a internet e a conexão com o banco de dados.");
      alert.showAndWait();
      throw new RuntimeException("Erro ao validar login no banco de dados: " + e.getMessage());
    }

    return false; // Retorna falso se o usuário não for encontrado ou a senha for inválida
  }

  public String buscaEmail(String nomeUsuario) {
    String sql = "SELECT email FROM usuario WHERE nomeusuario = ?";

    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
      // Configura o parâmetro para o nome de usuário
      stmt.setString(1, nomeUsuario);

      try (ResultSet rs = stmt.executeQuery()) {
        if (rs.next()) {
          return rs.getString("email");
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
      throw new RuntimeException("Erro ao buscar email no banco de dados: " + e.getMessage());
    }
    throw new RuntimeException("Usuário não encontrado");
  }

  public String resetPassword(String recipientEmail) {
    String sqlSelect = "SELECT email FROM usuario WHERE email = ?";
    String sqlUpdate = "UPDATE usuario SET senha = ? WHERE email = ?";

    try (PreparedStatement selectStmt = connection.prepareStatement(sqlSelect)) {
      // Verifica se o e-mail existe no banco
      selectStmt.setString(1, recipientEmail);

      try (ResultSet rs = selectStmt.executeQuery()) {
        if (rs.next()) {
          // Gera uma nova senha temporária
          String temporaryPassword = generateTemporaryPassword();

          // Criptografa a nova senha com BCrypt
          String hashedPassword = EncriptadorDeSenhas.hashPassword(temporaryPassword);

          // Atualiza o banco de dados com a nova senha
          try (PreparedStatement updateStmt = connection.prepareStatement(sqlUpdate)) {
            updateStmt.setString(1, hashedPassword);
            updateStmt.setString(2, recipientEmail);
            updateStmt.executeUpdate();
          }

          // Retorna a nova senha temporária (para envio por e-mail)
          return temporaryPassword;
        } else {
          throw new RuntimeException("E-mail não encontrado.");
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
      throw new RuntimeException("Erro ao redefinir a senha: " + e.getMessage());
    }
  }

  private String generateTemporaryPassword() {
    String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%&*";
    Random random = new Random();
    StringBuilder temporaryPassword = new StringBuilder();

    for (int i = 0; i < 10; i++) { // Tamanho da senha temporária
      temporaryPassword.append(chars.charAt(random.nextInt(chars.length())));
    }

    return temporaryPassword.toString();
  }

  public Integer getUserIdByUsername(String username) {
    String sql = "SELECT idusuario FROM usuario WHERE nomeusuario = ?";
    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
      stmt.setString(1, username);
      try (ResultSet rs = stmt.executeQuery()) {
        if (rs.next()) {
          return rs.getInt("idusuario");
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return null; // Retorna null se o usuário não for encontrado
  }

  public User getUserById(Integer id) {
    String sql = "SELECT * FROM usuario WHERE idusuario = ?";
    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
      stmt.setInt(1, id);
      try (ResultSet rs = stmt.executeQuery()) {
        if (rs.next()) {
          return new User(
              rs.getInt("idusuario"),
              rs.getString("nomecompleto"),
              rs.getString("email"),
              rs.getString("nomeusuario"),
              rs.getString("senha"),
              rs.getDate("datanascimento"),
              User.Sex.valueOf(rs.getString("sexo")),
              rs.getBytes("fotoperfil") != null ? new Image(new ByteArrayInputStream(rs.getBytes("fotoperfil")))
                  : null);
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return null; // Retorna null se o usuário não for encontrado
  }

  public boolean updatePassword(Integer userId, String novaSenha) {
    String sql = "UPDATE usuario SET senha = ? WHERE idUsuario = ?";
    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
      stmt.setString(1, EncriptadorDeSenhas.hashPassword(novaSenha)); // Criptografa a senha
      stmt.setInt(2, userId);
      return stmt.executeUpdate() > 0; // Retorna true se a atualização foi bem-sucedida
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return false;
  }

  public void updateProfileImage(Integer userId, byte[] imageBytes) {
    String sql = "UPDATE usuario SET fotoperfil = ? WHERE idusuario = ?";
    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
      stmt.setBytes(1, imageBytes);
      stmt.setInt(2, userId);
      stmt.executeUpdate();
    } catch (SQLException e) {
      System.out.println("Erro ao atualizar a imagem de perfil: " + e.getMessage());
      e.printStackTrace();
    }
  }

}