package com.otp.temperature.model;

public class TemperatureUnit {

  private final int id;
  private final String code;
  private final String name;

  public TemperatureUnit(int id, String code, String name) {
    this.id = id;
    this.code = code;
    this.name = name;
  }

  public int getId() {
    return id;
  }

  public String getCode() {
    return code;
  }

  public String getName() {
    return name;
  }

  @Override
  public String toString() {
    return name + " (" + code + ")";
  }
}
