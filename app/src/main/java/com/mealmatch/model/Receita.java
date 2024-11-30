package com.mealmatch.model;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import com.mealmatch.jdbc.dao.ReceitaIngredienteDAO;
import com.mealmatch.jdbc.database.ConnectionFactory;

import javafx.scene.image.Image;

public class Receita {
  private Integer id ;
  private String nome;
  Map<Ingrediente, ReceitaIngrediente> ingredientesMapping = new HashMap<>();
  private String ingredientesFormatados;
  private String modoPreparo;
  private int tempoPreparo;
  private int valorNutricional;
  private int dificuldade;
  private Image imagem;
  private int numeroLikes;
  private int numeroDislikes;
  private double massa;
  TabelaNutricionalReceita tabelaNutricional;

  public Receita(Integer idReceita, String nomeReceita, String modoPreparo, int tempoPreparo, int dificuldade,
      Image imagemReceita, int numeroLikes, int numeroDislikes, int idTabela) {
    this.id = idReceita;
    this.nome = nomeReceita;
    this.modoPreparo = modoPreparo;
    this.tempoPreparo = tempoPreparo;
    this.dificuldade = dificuldade;
    this.imagem = imagemReceita;
    this.numeroLikes = numeroLikes;
    this.numeroDislikes = numeroDislikes;
    // this.tabelaNutricional = TODO: buscar tabela nutricional da receita
    //this.valorNutricional = TODO: calcular valor nutricional da receita

    // Busca os ingredientes da receita utilizando a relação ReceitaIngrediente
    ReceitaIngredienteDAO ReceitaIngredienteDao = new ReceitaIngredienteDAO(ConnectionFactory.getConnection());
    try {
      this.ingredientesMapping = ReceitaIngredienteDao.buscarIngredientesPorReceita(idReceita);

      // Formata os ingredientes
      StringBuilder ingredientesStringBuilder = new StringBuilder();
      for (Map.Entry<Ingrediente, ReceitaIngrediente> entry : ingredientesMapping.entrySet()) {
        Ingrediente ingrediente = entry.getKey();
        ReceitaIngrediente receitaIngrediente = entry.getValue();

        if (ingredientesStringBuilder.length() > 0) {
          ingredientesStringBuilder.append(" ; "); // Adiciona o delimitador
        }

        // Formata: Nome do ingrediente (quantidade unidade de medida)
        ingredientesStringBuilder.append(ingrediente.getNomeIngrediente())
            .append(" (")
            .append(receitaIngrediente.getQuantidade())
            .append(" ")
            .append(receitaIngrediente.getUnidadeMedida())
            .append(")");
      }

      this.ingredientesFormatados = ingredientesStringBuilder.toString(); // Converte para String para exibir na tela de detalhes

    } catch (SQLException e) {
      System.out.println("Erro ao buscar ingredientes da receita");
      e.printStackTrace();
    }
  }

  public void gerarTabela() {
    @SuppressWarnings("unused")
    int count = 0;
    for (Map.Entry<Ingrediente, ReceitaIngrediente> tupla : ingredientesMapping.entrySet()) {
      Ingrediente ingrediente = tupla.getKey(); // Pega o objeto ingrediente
      ReceitaIngrediente receitaIngrediente = tupla.getValue(); // Pega o objeta que liga os dois
      count++;

      double quantidade = receitaIngrediente.getQuantidade();
      System.out.println("Quantidade de " + ingrediente.getNomeIngrediente() + " : " + quantidade);
      String unidadeMedida = receitaIngrediente.getUnidadeMedida();

      if (unidadeMedida == "L" || unidadeMedida == "kg") // Converte tudo para grama ou ml
        quantidade = quantidade * 1000;

      double caloriaAdicionar = ingrediente.getTabela().getCaloria() * quantidade / 100;
      this.tabelaNutricional.setCaloria(this.tabelaNutricional.getCaloria() + caloriaAdicionar);

      double carboidratoAdicionar = ingrediente.getTabela().getCarboidrato() * quantidade / 100;
      this.tabelaNutricional.setCarboidrato(this.tabelaNutricional.getCarboidrato() + carboidratoAdicionar);

      double gorduraAdicionar = ingrediente.getTabela().getGordura() * quantidade / 100;
      this.tabelaNutricional.setGordura(this.tabelaNutricional.getGordura() + gorduraAdicionar);

      double proteinaAdicionar = ingrediente.getTabela().getProteina() * quantidade / 100;
      this.tabelaNutricional.setProteina(this.tabelaNutricional.getProteina() + proteinaAdicionar);
      this.massa += quantidade;
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

  public String getNome() {
    return nome;
  }

  public void setNome(String nome) {
    this.nome = nome;
  }

  public String getIngredientesFormatados() {
    return ingredientesFormatados;
  }

  public void setIngredientesFormatados(String ingredientesFormatados) {
    this.ingredientesFormatados = ingredientesFormatados;
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
