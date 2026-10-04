package com.otp.temperature.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TemperatureUnitTest {

  @Test
  void gettersAndToString() {
    TemperatureUnit unit = new TemperatureUnit(3, "K", "Kelvin");
    assertEquals(3, unit.getId());
    assertEquals("K", unit.getCode());
    assertEquals("Kelvin", unit.getName());
    assertEquals("Kelvin (K)", unit.toString());
  }
}
