package com.otp.temperature;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TempCalculatorTest {

  private static final double DELTA = 0.0001;
  private final TempCalculator calc = new TempCalculator();

  @Test
  void fahrenheitToCelsius() {
    assertEquals(0, calc.fahrenheitToCelsius(32), DELTA);
    assertEquals(100, calc.fahrenheitToCelsius(212), DELTA);
  }

  @Test
  void celsiusToFahrenheit() {
    assertEquals(32, calc.celsiusToFahrenheit(0), DELTA);
    assertEquals(-40, calc.celsiusToFahrenheit(-40), DELTA);
  }

  @Test
  void kelvinConversions() {
    assertEquals(0, calc.kelvinToCelsius(273.15), DELTA);
    assertEquals(373.15, calc.celsiusToKelvin(100), DELTA);
  }

  @Test
  void extremeTemperature() {
    assertTrue(calc.isExtremeTemperature(-41));
    assertTrue(calc.isExtremeTemperature(51));
    assertFalse(calc.isExtremeTemperature(-40));
    assertFalse(calc.isExtremeTemperature(50));
  }

  @Test
  void convertBetweenAllUnits() {
    assertEquals(212, calc.convert(100, "C", "F"), DELTA);
    assertEquals(273.15, calc.convert(32, "f", "k"), DELTA);
    assertEquals(0, calc.convert(273.15, "K", "C"), DELTA);
    assertEquals(25, calc.convert(25, "C", "C"), DELTA);
  }

  @Test
  void convertRejectsUnknownUnit() {
    assertThrows(IllegalArgumentException.class, () -> calc.convert(1, "X", "C"));
    assertThrows(IllegalArgumentException.class, () -> calc.convert(1, "C", "X"));
  }

  @Test
  void convertRejectsBelowAbsoluteZero() {
    assertThrows(IllegalArgumentException.class, () -> calc.convert(-1, "K", "C"));
  }
}
