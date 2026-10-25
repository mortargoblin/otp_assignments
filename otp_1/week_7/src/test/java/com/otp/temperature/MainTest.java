package com.otp.temperature;

import com.otp.temperature.model.TempRecord;
import com.otp.temperature.model.TemperatureUnit;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MainTest {

  private final TempCalculator calc = new TempCalculator();
  private final TemperatureUnit celsius = new TemperatureUnit(1, "C", "Celsius");
  private final TemperatureUnit fahrenheit = new TemperatureUnit(2, "F", "Fahrenheit");
  private final TemperatureUnit kelvin = new TemperatureUnit(3, "K", "Kelvin");

  @Test
  void parseValueAcceptsDotAndComma() {
    assertEquals(12.5, Main.parseValue(" 12.5 "));
    assertEquals(12.5, Main.parseValue("12,5"));
  }

  @Test
  void parseValueRejectsBadInput() {
    assertThrows(IllegalArgumentException.class, () -> Main.parseValue(null));
    assertThrows(IllegalArgumentException.class, () -> Main.parseValue("  "));
    assertThrows(IllegalArgumentException.class, () -> Main.parseValue("abc"));
  }

  @Test
  void createRecordConverts() {
    TempRecord record = Main.createRecord(calc, "100", celsius, fahrenheit);
    assertEquals(100, record.getInputValue());
    assertEquals(212, record.getResultValue(), 0.0001);
    assertEquals(celsius, record.getFromUnit());
    assertEquals(fahrenheit, record.getToUnit());
  }

  @Test
  void formatValueUsesUnitSymbol() {
    assertEquals("21.50 °C", Main.formatValue(21.5, celsius));
    assertEquals("300.00 K", Main.formatValue(300, kelvin));
  }

  @Test
  void describeMarksExtremeTemperatures() {
    assertEquals("100.00 °C = 212.00 °F (extreme!)", Main.describe(calc, Main.createRecord(calc, "100", celsius, fahrenheit)));
    assertEquals("32.00 °F = 273.15 K", Main.describe(calc, Main.createRecord(calc, "32", fahrenheit, kelvin)));
  }
}
