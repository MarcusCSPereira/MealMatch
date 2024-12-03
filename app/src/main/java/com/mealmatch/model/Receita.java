package com.mealmatch.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javafx.scene.image.Image;

public class Receita {
  private Integer id;
  private String nome;
  private Integer idUsuarioDonoReceita; // Id do usuário que criou a receita, pode ser 0 caso seja uma receita padrão do
                                        // sistema ou ter um valor caso seja uma receita de um usuário
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
  TabelaNutricional tabelaNutricional;

  public Receita() {
  }

  public Receita(Integer idReceita, String nomeReceita, String modoPreparo, int tempoPreparo, int dificuldade,
      Image imagemReceita, int numeroLikes, int numeroDislikes) {
    this.id = idReceita;
    this.nome = nomeReceita;
    this.modoPreparo = modoPreparo;
    this.tempoPreparo = tempoPreparo;
    this.dificuldade = dificuldade;
    this.imagem = imagemReceita;
    this.numeroLikes = numeroLikes;
    this.numeroDislikes = numeroDislikes;
    this.tabelaNutricional = new TabelaNutricional(idReceita);
  }

  public void gerarTabela() {
    @SuppressWarnings("unused")
    int count = 0;
    for (Map.Entry<Ingrediente, ReceitaIngrediente> tupla : ingredientesMapping.entrySet()) {
      Ingrediente ingrediente = tupla.getKey(); // Pega o objeto ingrediente
      ReceitaIngrediente receitaIngrediente = tupla.getValue(); // Pega o objeta que liga os dois
      count++;

      double quantidade = receitaIngrediente.getQuantidade();
      String unidadeMedida = receitaIngrediente.getUnidadeMedida();

      if (unidadeMedida.equals("L") || unidadeMedida.equals("kg")) // Converte tudo para grama ou ml
        quantidade = quantidade * 1000;

      double caloriaAdicionar = gerarProporcao(ingrediente.getCaloria(), quantidade);
      this.tabelaNutricional.setCaloria(this.tabelaNutricional.getCaloria() + caloriaAdicionar);

      double carboidratoAdicionar = gerarProporcao(ingrediente.getCarboidrato(), quantidade);
      this.tabelaNutricional.setCarboidrato(this.tabelaNutricional.getCarboidrato() + carboidratoAdicionar);

      double gorduraAdicionar = gerarProporcao(ingrediente.getGordura(), quantidade);
      this.tabelaNutricional.setGordura(this.tabelaNutricional.getGordura() + gorduraAdicionar);

      double proteinaAdicionar = gerarProporcao(ingrediente.getProteina(), quantidade);
      this.tabelaNutricional.setProteina(this.tabelaNutricional.getProteina() + proteinaAdicionar);
      this.massa += quantidade;
    }

    formatarValoresNutricionais();

  }

  // Método para gerar o valor nutricional da receita
  public void gerarValorNutricional() {
    this.valorNutricional = (int) (truncarParaUmaCasaDecimal(this.tabelaNutricional.getCaloria()));
  }

  // Método para formatar os valores nutricionais para uma casa decimal
  private void formatarValoresNutricionais() {
    this.tabelaNutricional.setCaloria(truncarParaUmaCasaDecimal(this.tabelaNutricional.getCaloria()));
    this.tabelaNutricional.setCarboidrato(truncarParaUmaCasaDecimal(this.tabelaNutricional.getCarboidrato()));
    this.tabelaNutricional.setGordura(truncarParaUmaCasaDecimal(this.tabelaNutricional.getGordura()));
    this.tabelaNutricional.setProteina(truncarParaUmaCasaDecimal(this.tabelaNutricional.getProteina()));
  }

  // Método para truncar um valor para uma casa decimal
  double truncarParaUmaCasaDecimal(double valor) {
    return new BigDecimal(valor)
        .setScale(1, RoundingMode.DOWN) // Trunca para 1 casa decimal
        .doubleValue();
  }

  // Método para gerar a proporção de um valor
  private static double gerarProporcao(double valor, double quantidade) {
    return valor * quantidade / 100;
  }

  // Método para gerar a string dos ingredientes
  public void gerarStringIngredientes() {
    StringBuilder ingredientesStringBuilder = new StringBuilder();

    for (Map.Entry<Ingrediente, ReceitaIngrediente> entry : ingredientesMapping.entrySet()) {
      Ingrediente ingrediente = entry.getKey();
      ReceitaIngrediente receitaIngrediente = entry.getValue();

      if (ingredientesStringBuilder.length() > 0)
        ingredientesStringBuilder.append(" ; "); // Adiciona o delimitador

      // Formata o nome do ingrediente (quantidade unidade de medida)
      ingredientesStringBuilder.append(ingrediente.getNomeIngrediente())
          .append(" (")
          .append(receitaIngrediente.getQuantidade())
          .append(" ")
          .append(receitaIngrediente.getUnidadeMedida())
          .append(")");
    }

    this.ingredientesFormatados = ingredientesStringBuilder.toString();
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

  public TabelaNutricional getTabelaNutricional() {
    return tabelaNutricional;
  }

  public void setTabelaNutricional(TabelaNutricional tabelaNutricional) {
    this.tabelaNutricional = tabelaNutricional;
  }

  public Integer getIdUsuarioDonoReceita() {
    return idUsuarioDonoReceita;
  }

  public void setIdUsuarioDonoReceita(Integer idUsuarioDonoReceita) {
    this.idUsuarioDonoReceita = idUsuarioDonoReceita;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;

    Receita receita = (Receita) o;

    return id == receita.id &&
        Objects.equals(nome, receita.nome) &&
        Objects.equals(tempoPreparo, receita.tempoPreparo) &&
        Objects.equals(valorNutricional, receita.valorNutricional);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, nome, tempoPreparo, valorNutricional);
  }

}
