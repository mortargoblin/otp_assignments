package com.otp.temperature;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MainTest {

  private final TemperatureConverter converter = new TemperatureConverter();

  @Test
  void convert_celsius() {
    assertEquals("100.00 C = 100.00 C = 212.00 F (extreme!)", Main.convert(converter, 100, "c"));
  }

  @Test
  void convert_fahrenheit() {
    assertEquals("32.00 F = 0.00 C = 32.00 F", Main.convert(converter, 32, "F"));
  }

  @Test
  void convert_kelvin() {
    assertEquals("273.15 K = 0.00 C = 32.00 F", Main.convert(converter, 273.15, "K"));
  }

  @Test
  void convert_unknownUnit() {
    assertThrows(IllegalArgumentException.class, () -> Main.convert(converter, 1, "X"));
  }

  @Test
  void main_withArgs() {
    String out = captureOutput(() -> Main.main(new String[] {"32", "F"}));
    assertEquals("32.00 F = 0.00 C = 32.00 F", out.trim());
  }

  @Test
  void main_withoutArgs() {
    String out = captureOutput(() -> Main.main(new String[0]));
    assertTrue(out.startsWith("Temperature Converter"));
  }

  private String captureOutput(Runnable action) {
    PrintStream original = System.out;
    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    System.setOut(new PrintStream(buffer));
    try {
      action.run();
    } finally {
      System.setOut(original);
    }
    return buffer.toString();
  }
}
