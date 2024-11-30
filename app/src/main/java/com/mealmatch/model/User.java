package com.mealmatch.model;

import java.sql.Date;

import javafx.scene.image.Image;

public class User {

  public enum Sex {
    M,
    F
  }

  private Integer id;
  private String completeName;
  private String email;
  private String username;
  private String password;
  private Date date;
  private Sex sex;
  private Image profileImage;
  private int numeroReceitasCriadas;

  public User(Integer id, String completeName, String email, String username, String password, Date date, Sex sex, Image profileImage) {
    if (id != null && id <= 0) {
      throw new IllegalArgumentException("ID deve ser um número positivo.");
    }
    this.id = id;
    this.completeName = completeName;
    this.email = email;
    this.username = username;
    this.password = password;
    this.date = date;
    this.sex = sex;
    this.profileImage = profileImage;
  }

  public User(){}

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public Date getDate() {
    return date;
  }

  public void setDate(Date date) {
    this.date = date;
  }

  public String getCompleteName() {
    return completeName;
  }

  public void setCompleteName(String completeName) {
    this.completeName = completeName;
  }

  public Sex getSex(){
    return sex;
  }

  public void setSex(Sex sex){
    this.sex = sex;
  }

  public Image getProfileImage() {
    return profileImage;
  }

  public void setProfileImage(Image profileImage) {
    this.profileImage = profileImage;
  }

  public int getNumeroReceitasCriadas() {
    return numeroReceitasCriadas;
  }

  public void setNumeroReceitasCriadas(int numeroReceitasCriadas) {
    this.numeroReceitasCriadas = numeroReceitasCriadas;
  }

}
