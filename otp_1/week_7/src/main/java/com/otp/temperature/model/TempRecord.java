package com.otp.temperature.model;

import java.time.LocalDateTime;

public class TempRecord {

  private int id;
  private final double inputValue;
  private final TemperatureUnit fromUnit;
  private final TemperatureUnit toUnit;
  private final double resultValue;
  private LocalDateTime createdAt;

  public TempRecord(double inputValue, TemperatureUnit fromUnit, TemperatureUnit toUnit, double resultValue) {
    this(0, inputValue, fromUnit, toUnit, resultValue, null);
  }

  public TempRecord(int id, double inputValue, TemperatureUnit fromUnit, TemperatureUnit toUnit,
      double resultValue, LocalDateTime createdAt) {
    this.id = id;
    this.inputValue = inputValue;
    this.fromUnit = fromUnit;
    this.toUnit = toUnit;
    this.resultValue = resultValue;
    this.createdAt = createdAt;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public double getInputValue() {
    return inputValue;
  }

  public TemperatureUnit getFromUnit() {
    return fromUnit;
  }

  public TemperatureUnit getToUnit() {
    return toUnit;
  }

  public double getResultValue() {
    return resultValue;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }
}
