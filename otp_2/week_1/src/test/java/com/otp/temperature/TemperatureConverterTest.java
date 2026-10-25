package com.otp.temperature;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TemperatureConverterTest {

  private TemperatureConverter converter;

  @BeforeEach
  void setUp() {
    converter = new TemperatureConverter();
  }

  @ParameterizedTest
  @CsvSource({
      "32, 0",
      "212, 100",
      "0, -17.7778",
      "-40, -40",
      "98.6, 37",
      "122, 50"
  })
  void fahrenheitToCelsius(double fahrenheit, double expectedCelsius) {
    assertEquals(expectedCelsius, converter.fahrenheitToCelsius(fahrenheit), 0.0001);
  }

  @ParameterizedTest
  @CsvSource({
      "0, 32",
      "100, 212",
      "-40, -40",
      "37, 98.6",
      "-17.7778, 0",
      "50, 122"
  })
  void celsiusToFahrenheit(double celsius, double expectedFahrenheit) {
    assertEquals(expectedFahrenheit, converter.celsiusToFahrenheit(celsius), 0.0001);
  }

  @Test
  void isExtremeTemperature_belowLowerBound() {
    assertTrue(converter.isExtremeTemperature(-41));
  }

  @Test
  void isExtremeTemperature_atLowerBound() {
    assertFalse(converter.isExtremeTemperature(-40));
  }

  @Test
  void isExtremeTemperature_justBelowLowerBound() {
    assertTrue(converter.isExtremeTemperature(-40.01));
  }

  @Test
  void isExtremeTemperature_aboveUpperBound() {
    assertTrue(converter.isExtremeTemperature(51));
  }

  @Test
  void isExtremeTemperature_atUpperBound() {
    assertFalse(converter.isExtremeTemperature(50));
  }

  @Test
  void isExtremeTemperature_justAboveUpperBound() {
    assertTrue(converter.isExtremeTemperature(50.01));
  }

  @Test
  void isExtremeTemperature_normalRange() {
    assertFalse(converter.isExtremeTemperature(20));
  }
}
