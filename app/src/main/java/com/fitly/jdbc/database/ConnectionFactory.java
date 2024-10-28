package com.fitly.jdbc.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import io.github.cdimascio.dotenv.Dotenv;

public class ConnectionFactory {
  //Classe de conexão com o banco de dados

  private static Dotenv dotenv = Dotenv.load();

  public static Connection getConnection() {
    String url = dotenv.get("DATABASE_URL");
    String user = dotenv.get("DATABASE_USER");
    String password = dotenv.get("DATABASE_PASSWORD");

    try {
      System.out.println("Conectado com sucesso");
      return DriverManager.getConnection(url, user, password);
    } catch (SQLException e) {
      e.printStackTrace();
      System.out.println("Erro ao conectar com o banco de dados");
      throw new RuntimeException("Erro ao conectar com o banco de dados");
    }
  }

}
