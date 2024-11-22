package com.mealmatch.model;

import java.util.HashMap;
import java.util.Map;

import javafx.scene.image.Image;

public class Receita {
  private Integer id=0;
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
  private double massa;
  Map<Ingrediente,ReceitaIngrediente> ingredientesMapping = new HashMap<>();
  TabelaNutricionalReceita tabelaNutricional;


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
    tabelaNutricional = new TabelaNutricionalReceita(id);
  }

  public void gerarTabela(){
    int count = 0;
    for(Map.Entry<Ingrediente,ReceitaIngrediente> tupla: ingredientesMapping.entrySet()){
      Ingrediente ingrediente = tupla.getKey(); // Pega o objeto ingrediente
      ReceitaIngrediente receitaIngrediente = tupla.getValue(); // Pega o objeta que liga os dois
      count++;

      double quantidade = receitaIngrediente.getQuantidade();
      System.out.println("Quantidade de "+ingrediente.getNomeIngrediente()+ " : "+ quantidade);
      String unidadeMedida = receitaIngrediente.getUnidadeMedida();



      if(unidadeMedida == "L" || unidadeMedida == "kg") // Converte tudo para grama ou ml
        quantidade = quantidade * 1000;
      
      double caloriaAdicionar = ingrediente.getTabela().getCaloria() * quantidade / 100;
      this.tabelaNutricional.setCaloria(this.tabelaNutricional.getCaloria()+ caloriaAdicionar);

      double carboidratoAdicionar = ingrediente.getTabela().getCarboidrato() * quantidade / 100;
      this.tabelaNutricional.setCarboidrato(this.tabelaNutricional.getCarboidrato()+ carboidratoAdicionar);

      double gorduraAdicionar = ingrediente.getTabela().getGordura() * quantidade / 100;
      this.tabelaNutricional.setGordura(this.tabelaNutricional.getGordura()+ gorduraAdicionar);

      double proteinaAdicionar = ingrediente.getTabela().getProteina() * quantidade / 100;
      this.tabelaNutricional.setProteina(this.tabelaNutricional.getProteina()+ proteinaAdicionar);
      this.massa+= quantidade;
    }
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

   public double getMassa() {
  return massa;
}

public void setMassa(double massa) {
  this.massa = massa;
}

public Map<Ingrediente, ReceitaIngrediente> getIngredientesMapping() {
  return ingredientesMapping;
}

public void setIngredientesMapping(Map<Ingrediente, ReceitaIngrediente> ingredientesMapping) {
  this.ingredientesMapping = ingredientesMapping;
}

public TabelaNutricionalReceita getTabelaNutricional() {
  return tabelaNutricional;
}

public void setTabelaNutricional(TabelaNutricionalReceita tabelaNutricional) {
  this.tabelaNutricional = tabelaNutricional;
}

}