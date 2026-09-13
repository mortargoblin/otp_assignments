package com.otp.temperature;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TemperatureConverterTest {

  // en pidä unit-testeistä :(

  private TemperatureConverter converter;

  @BeforeEach
  void setUp() {
    converter = new TemperatureConverter();
  }

  @Test
  void fahrenheitToCelsius_freezingPoint() {
    assertEquals(0.0, converter.fahrenheitToCelsius(32), 0.0001);
  }

  @Test
  void fahrenheitToCelsius_boilingPoint() {
    assertEquals(100.0, converter.fahrenheitToCelsius(212), 0.0001);
  }

  @Test
  void fahrenheitToCelsius_negativeValue() {
    assertEquals(-17.7778, converter.fahrenheitToCelsius(0), 0.001);
  }

  @Test
  void celsiusToFahrenheit_freezingPoint() {
    assertEquals(32.0, converter.celsiusToFahrenheit(0), 0.0001);
  }

  @Test
  void celsiusToFahrenheit_boilingPoint() {
    assertEquals(212.0, converter.celsiusToFahrenheit(100), 0.0001);
  }

  @Test
  void celsiusToFahrenheit_negativeValue() {
    assertEquals(-40.0, converter.celsiusToFahrenheit(-40), 0.0001);
  }

  @Test
  void isExtremeTemperature_belowLowerBound() {
    assertTrue(converter.isExtremeTemperature(-41));
  }

  @Test
  void isExtremeTemperature_aboveUpperBound() {
    assertTrue(converter.isExtremeTemperature(51));
  }

  @Test
  void isExtremeTemperature_atLowerBoundEdge() {
    assertFalse(converter.isExtremeTemperature(-40));
  }

  @Test
  void isExtremeTemperature_atUpperBoundEdge() {
    assertFalse(converter.isExtremeTemperature(50));
  }

  @Test
  void isExtremeTemperature_normalRange() {
    assertFalse(converter.isExtremeTemperature(20));
  }
}
