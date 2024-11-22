package com.mealmatch.model;

public class TabelaNutricionalReceita {
  private int idTabelaNutricional;
  private double carboidrato;
  private  double gordura;
  private double proteina;
  private double caloria;
  private int idReceita;

  public TabelaNutricionalReceita(int idReceita){
    this.idReceita = idReceita;
  }

  public TabelaNutricionalReceita(double carboidrato, double gordura, double proteina, double caloria, int idReceita){
    this.carboidrato = carboidrato;
    this.gordura = gordura;
    this.proteina = proteina;
    this.caloria = caloria;
    this.idReceita = idReceita;
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

  @Override
  public String toString(){
    return  "Calorias: "+this.caloria+
            "\nCarboidrato: "+this.carboidrato+
            "\nGordura: "+this.gordura+
            "\nProteina: "+this.proteina;
  }

}
