package com.mealmatch.model;

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
  private Integer age;
  private Sex sex;

  public User(Integer id, String completeName, String email, String username, String password, Integer age, Sex sex) {
    if (id != null && id <= 0) {
      throw new IllegalArgumentException("ID deve ser um número positivo.");
    }
    this.id = id;
    this.completeName = completeName;
    setEmail(email);
    this.username = username;
    setPassword(password);
    setAge(age);
    this.sex = sex;
  }

  public User(){}

  public Integer getId() {
    return id;
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

  public Integer getAge() {
    return age;
  }

  public void setAge(Integer age) {
    this.age = age;
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

}
