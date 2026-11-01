package com.otp.temperature;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TemperatureConverterTest {

  private static final double DELTA = 0.0001;
  private final TemperatureConverter converter = new TemperatureConverter();

  @ParameterizedTest
  @CsvSource({"32, 0", "212, 100", "0, -17.7778", "-40, -40", "98.6, 37"})
  void fahrenheitToCelsius(double fahrenheit, double expectedCelsius) {
    assertEquals(expectedCelsius, converter.fahrenheitToCelsius(fahrenheit), DELTA);
  }

  @ParameterizedTest
  @CsvSource({"0, 32", "100, 212", "-40, -40", "37, 98.6"})
  void celsiusToFahrenheit(double celsius, double expectedFahrenheit) {
    assertEquals(expectedFahrenheit, converter.celsiusToFahrenheit(celsius), DELTA);
  }

  @Test
  void kelvinConversions() {
    assertEquals(0, converter.kelvinToCelsius(273.15), DELTA);
    assertEquals(373.15, converter.celsiusToKelvin(100), DELTA);
  }

  @Test
  void extremeTemperatureBounds() {
    assertTrue(converter.isExtremeTemperature(-40.01));
    assertTrue(converter.isExtremeTemperature(50.01));
    assertFalse(converter.isExtremeTemperature(-40));
    assertFalse(converter.isExtremeTemperature(50));
    assertFalse(converter.isExtremeTemperature(20));
  }

  @Test
  void convertBetweenAllUnits() {
    assertEquals(212, converter.convert(100, "C", "F"), DELTA);
    assertEquals(273.15, converter.convert(32, "f", "k"), DELTA);
    assertEquals(0, converter.convert(273.15, "K", "C"), DELTA);
    assertEquals(-459.67, converter.convert(0, "K", "F"), DELTA);
    assertEquals(25, converter.convert(25, "C", "C"), DELTA);
  }

  @Test
  void absoluteZeroIsAllowedButNotBelow() {
    assertFalse(converter.isBelowAbsoluteZero(-273.15));
    assertTrue(converter.isBelowAbsoluteZero(-273.16));
    assertThrows(IllegalArgumentException.class, () -> converter.convert(-1, "K", "C"));
  }

  @Test
  void unknownUnitRejected() {
    assertThrows(IllegalArgumentException.class, () -> converter.convert(1, "X", "C"));
    assertThrows(IllegalArgumentException.class, () -> converter.convert(1, "C", "X"));
  }
}
