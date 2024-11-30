package com.mealmatch.model;

public class ReceitaIngrediente {
  private int idReceita;
  private int idIngrediente;
  private double quantidade;
  private String unidadeMedida;

  public ReceitaIngrediente(){
  }

  public ReceitaIngrediente(int idReceita, int idIngrediente, double quantidade, String unidadeMedida){
    this.idReceita = idReceita;
    this.idIngrediente = idIngrediente;
    this.quantidade = quantidade;
    this.unidadeMedida = unidadeMedida;
  }

  public int getIdReceita() {
    return idReceita;
  }
  public void setIdReceita(int idReceita) {
    this.idReceita = idReceita;
  }
  public int getIdIngrediente() {
    return idIngrediente;
  }
  public void setIdIngrediente(int idIngrediente) {
    this.idIngrediente = idIngrediente;
  }
  public double getQuantidade() {
    return quantidade;
  }
  public void setQuantidade(double quantidade) {
    this.quantidade = quantidade;
  }
  public String getUnidadeMedida() {
    return unidadeMedida;
  }
  public void setUnidadeMedida(String unidadeMedida) {
    this.unidadeMedida = unidadeMedida;
  }

}
