package com.mealmatch.model;

public class Ingrediente {
  private int id_ingrediente;
  private String nomeIngrediente;
  private double carboidrato;
  private  double gordura;
  private double proteina;
  private double caloria;


  public Ingrediente(){}

  public Ingrediente(Integer id_ingrediente,String nomeIngrediente){
    this.id_ingrediente = id_ingrediente;
    this.nomeIngrediente = nomeIngrediente;
  }

  public int getId_ingrediente() {
    return id_ingrediente;
  }
  public void setId_ingrediente(int id_ingrediente) {
    this.id_ingrediente = id_ingrediente;
  }
  public String getNomeIngrediente() {
    return nomeIngrediente;
  }
  public void setNomeIngrediente(String nomeIngrediente) {
    this.nomeIngrediente = nomeIngrediente;
  }
  public double getCarboidrato() {
    return carboidrato;
  }

  public void setCarboidrato(double carboidrato) {
    this.carboidrato = carboidrato;
  }

  public double getGordura() {
    return gordura;
  }

  public void setGordura(double gordura) {
    this.gordura = gordura;
  }

  public double getProteina() {
    return proteina;
  }

  public void setProteina(double proteina) {
    this.proteina = proteina;
  }

  public double getCaloria() {
    return caloria;
  }

  public void setCaloria(double caloria) {
    this.caloria = caloria;
  }

}

