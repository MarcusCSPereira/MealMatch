package com.mealmatch.utils;

public class ControleDeSessao {

  private static ControleDeSessao instance;

  private Integer userId; // ID do usuário logado

  private ControleDeSessao() {
    // Construtor privado para Singleton
  }

  public static ControleDeSessao getInstance() {
    if (instance == null) {
      instance = new ControleDeSessao();
    }
    return instance;
  }

  // Métodos para acessar o ID do usuário
  public Integer getUserId() {
    return userId;
  }

  public void setUserId(Integer userId) {
    this.userId = userId;
  }

  // Opcional: Método para limpar a sessão
  public void clearSession() {
    this.userId = null;
  }
}