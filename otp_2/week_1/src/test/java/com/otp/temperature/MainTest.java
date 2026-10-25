package com.otp.temperature;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MainTest {

  @Test
  void selectLocale_choices() {
    assertEquals(Locale.of("en", "US"), Main.selectLocale("1"));
    assertEquals(Locale.of("fi", "FI"), Main.selectLocale("2"));
    assertEquals(Locale.of("sv", "SE"), Main.selectLocale(" 3 "));
    assertEquals(Locale.of("ja", "JP"), Main.selectLocale("4"));
  }

  @Test
  void selectLocale_invalidFallsBackToEnglish() {
    assertEquals(Locale.of("en", "US"), Main.selectLocale("x"));
    assertEquals(Locale.of("en", "US"), Main.selectLocale(""));
  }

  @ParameterizedTest
  @ValueSource(strings = {"en_US", "fi_FI", "sv_SE", "ja_JP"})
  void bundles_haveSameKeysAsEnglish(String tag) {
    String[] parts = tag.split("_");
    ResourceBundle english = ResourceBundle.getBundle(Main.BUNDLE, Locale.of("en", "US"));
    ResourceBundle bundle = ResourceBundle.getBundle(Main.BUNDLE, Locale.of(parts[0], parts[1]));
    assertEquals(Locale.of(parts[0], parts[1]), bundle.getLocale());
    assertEquals(english.keySet(), bundle.keySet());
  }

  @Test
  void run_english_fahrenheitToCelsius() {
    String out = run("1\n1\n212\n3\n");
    assertTrue(out.contains("Temperature Converter"));
    assertTrue(out.contains("212.00 °F = 100.00 °C"));
    assertTrue(out.contains("Warning: extreme temperature!"));
    assertTrue(out.contains("Goodbye!"));
  }

  @Test
  void run_finnish_celsiusToFahrenheitWithDecimalComma() {
    String out = run("2\n2\n37,5\n3\n");
    assertTrue(out.contains("Lämpötilamuunnin"));
    assertTrue(out.contains("37,50 °C = 99,50 °F"));
    assertTrue(!out.contains("Varoitus"));
    assertTrue(out.contains("Näkemiin!"));
  }

  @Test
  void run_swedish_extremeCold() {
    String out = run("3\n2\n-41\n3\n");
    assertTrue(out.contains("Temperaturomvandlare"));
    assertTrue(out.contains("Varning: extrem temperatur!"));
  }

  @Test
  void run_japanese_invalidInput() {
    String out = run("4\n9\n1\nabc\n1\n32\n3\n");
    assertTrue(out.contains("温度変換ツール"));
    assertTrue(out.contains("無効な選択です。"));
    assertTrue(out.contains("無効な数値です。"));
    assertTrue(out.contains("32.00 °F = 0.00 °C"));
    assertTrue(out.contains("さようなら!"));
  }

  @Test
  void run_endOfInputExits() {
    assertTrue(run("").contains("Goodbye!"));
    assertTrue(run("1\n1\n").contains("Invalid number."));
  }

  @Test
  void main_readsFromStdin() {
    InputStream originalIn = System.in;
    try {
      System.setIn(new ByteArrayInputStream("1\n3\n".getBytes(StandardCharsets.UTF_8)));
      Main.main(new String[0]);
    } finally {
      System.setIn(originalIn);
    }
  }

  private String run(String input) {
    Scanner in = new Scanner(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8);
    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    new Main(in, new PrintStream(buffer, true, StandardCharsets.UTF_8)).run();
    return buffer.toString(StandardCharsets.UTF_8);
  }
}
