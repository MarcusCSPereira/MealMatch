package com.mealmatch.jdbc.dao;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.mealmatch.utils.EncriptadorDeSenhas;

public class UserDAOTest {

  private UserDAO userDAO;

  @Mock
  private Connection mockConnection;

  @Mock
  private PreparedStatement mockPreparedStatement;

  @Mock
  private ResultSet mockResultSet;

  @BeforeEach
  void setUp() throws SQLException {
    MockitoAnnotations.initMocks(this); // Inicializa os mocks

    // Configura os comportamentos do mockConnection
    when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);

    // Configura o comportamento do mockPreparedStatement
    when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);

    // Inicializa o UserDAO com a conexão mockada
    userDAO = new UserDAO(mockConnection);
  }

  @Test
  void testValidarLoginComSucesso() throws SQLException {
    // Simula os dados do banco de dados
    when(mockResultSet.next()).thenReturn(true); // Existe um resultado
    when(mockResultSet.getString("senha")).thenReturn(EncriptadorDeSenhas.hashPassword("testPassword"));

    // Executa o método
    boolean resultado = userDAO.validarLogin("testUser", "testPassword");

    // Verifica o resultado esperado
    assertTrue(resultado, "O login deveria ser validado com sucesso.");
  }

  @Test
  void testValidarLoginUsuarioNaoExistente() throws SQLException {
    // Simula que não há resultado no banco de dados
    when(mockResultSet.next()).thenReturn(false); // Nenhuma linha encontrada

    // Executa o método
    boolean resultado = userDAO.validarLogin("invalidUser", "testPassword");

    // Verifica o resultado esperado
    assertFalse(resultado, "O login não deveria ser validado para um usuário inexistente.");
  }

  @Test
  void testValidarLoginSenhaIncorreta() throws SQLException {
    // Simula os dados do banco de dados com senha incorreta
    when(mockResultSet.next()).thenReturn(true); // Existe um resultado
    when(mockResultSet.getString("senha")).thenReturn(EncriptadorDeSenhas.hashPassword("correctPassword"));

    // Executa o método
    boolean resultado = userDAO.validarLogin("testUser", "wrongPassword");

    // Verifica o resultado esperado
    assertFalse(resultado, "O login não deveria ser validado com senha incorreta.");
  }

  @Test
  void testGetUserIdByUsername() throws Exception {
    // Simula o comportamento do ResultSet para buscar o ID
    when(mockResultSet.next()).thenReturn(true);
    when(mockResultSet.getInt("id_usuario")).thenReturn(1);

    Integer userId = userDAO.getUserIdByUsername("testUser");

    assertEquals(1, userId);
  }
}