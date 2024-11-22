package com.mealmatch.model;

public class Ingrediente {
  private int id_ingrediente;
  private String nomeIngrediente;
  private TabelaNutricionalIngrediente tabelaNutricional;

  public Ingrediente(){
    this.tabelaNutricional = new TabelaNutricionalIngrediente(this.id_ingrediente);
  }

  public Ingrediente(String nomeIngrediente, TabelaNutricionalIngrediente tabela){
    this.nomeIngrediente = nomeIngrediente;
    this.tabelaNutricional = tabela;
    this.tabelaNutricional = new TabelaNutricionalIngrediente(this.id_ingrediente);
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

  public TabelaNutricionalIngrediente getTabela(){
    return this.tabelaNutricional;
  }
  public void setTabela(TabelaNutricionalIngrediente tabelaNutricional) {
    this.tabelaNutricional = tabelaNutricional;
  }
}

