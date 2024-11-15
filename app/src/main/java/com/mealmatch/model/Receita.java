package com.mealmatch.model;

import javafx.scene.image.Image;

public class Receita {
  private Integer id;
  private String nome;
  private String ingredientes;
  private String modoPreparo;
  private int tempoPreparo;
  private int valorNutricional;
  private int dificuldade;
  private Image imagem;
  private int numeroLikes;
  private int numeroDislikes;
  private boolean favorita;

  public Receita(Integer id ,String nome, String ingredientes, String modoPreparo, int tempoPreparo, int valorNutricional, int dificuldade, Image imagem, int numeroLikes, int numeroDislikes, boolean favorita) {
    this.id = id;
    this.nome = nome;
    this.ingredientes = ingredientes;
    this.modoPreparo = modoPreparo;
    this.tempoPreparo = tempoPreparo;
    this.valorNutricional = valorNutricional;
    this.dificuldade = dificuldade;
    this.imagem = imagem;
    this.numeroLikes = numeroLikes;
    this.numeroDislikes = numeroDislikes;
    this.favorita = favorita;
  }

  public Receita() {
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public int getNumeroLikes() {
    return numeroLikes;
  }

  public void setNumeroLikes(int numeroLikes) {
    this.numeroLikes = numeroLikes;
  }

  public int getNumeroDislikes() {
    return numeroDislikes;
  }

  public void setNumeroDislikes(int numeroDislikes) {
    this.numeroDislikes = numeroDislikes;
  }

  public boolean isFavorita() {
    return favorita;
  }

  public void setFavorita(boolean favorita) {
    this.favorita = favorita;
  }

  public String getNome() {
    return nome;
  }
  public void setNome(String nome) {
    this.nome = nome;
  }
  public String getIngredientes() {
    return ingredientes;
  }
  public void setIngredientes(String ingredientes) {
    this.ingredientes = ingredientes;
  }
  public String getModoPreparo() {
    return modoPreparo;
  }
  public void setModoPreparo(String modoPreparo) {
    this.modoPreparo = modoPreparo;
  }
  public int getTempoPreparo() {
    return tempoPreparo;
  }
  public void setTempoPreparo(int tempoPreparo) {
    this.tempoPreparo = tempoPreparo;
  }
  public int getValorNutricional() {
    return valorNutricional;
  }
  public void setValorNutricional(int valorNutricional) {
    this.valorNutricional = valorNutricional;
  }
  public int getDificuldade() {
    return dificuldade;
  }
  public void setDificuldade(int dificuldade) {
    this.dificuldade = dificuldade;
  }
  public Image getImagem() {
    return imagem;
  }
  public void setImagem(Image imagem) {
    this.imagem = imagem;
  }

}
