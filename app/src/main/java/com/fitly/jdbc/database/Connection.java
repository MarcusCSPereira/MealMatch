package com.fitly.jdbc.database;

import java.sql.ResultSet;

public class Connection {
  //Classe de conexão com o banco de dados

  public Connection() {
    //Construtor da classe
  }

  public void open() {
    //Abre a conexão com o banco de dados PostgreSQL
  }

  public void close() {
    //Fecha a conexão com o banco de dados PostgreSQL
  }

  public void execute(String query) {
    //Executa uma query no banco de dados PostgreSQL
  }

  public void execute(String query, Object[] params) {
    //Executa uma query no banco de dados PostgreSQL com parâmetros
  }

  public ResultSet query(String query) {
    //Executa uma query de consulta no banco de dados PostgreSQL
    return null;
  }

  public ResultSet query(String query, Object[] params) {
    //Executa uma query de consulta no banco de dados PostgreSQL com parâmetros
    return null;
  }

}
