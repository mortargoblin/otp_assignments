package com.otp.temperature;

import java.util.Locale;

public class Main {

  public static void main(String[] args) {
    TemperatureConverter converter = new TemperatureConverter();

    if (args.length == 2) {
      double value = Double.parseDouble(args[0]);
      System.out.println(convert(converter, value, args[1]));
      return;
    }

    System.out.println("Temperature Converter");
    System.out.println("Usage: java -jar temperature-converter.jar <value> <C|F|K>");
    System.out.println();
    System.out.println(convert(converter, 100, "C"));
    System.out.println(convert(converter, 32, "F"));
    System.out.println(convert(converter, 300, "K"));
  }

  static String convert(TemperatureConverter converter, double value, String unit) {
    double celsius;
    switch (unit.toUpperCase()) {
      case "C" -> celsius = value;
      case "F" -> celsius = converter.fahrenheitToCelsius(value);
      case "K" -> celsius = converter.kelvinToCelsius(value);
      default -> throw new IllegalArgumentException("Unknown unit: " + unit);
    }
    double fahrenheit = converter.celsiusToFahrenheit(celsius);
    String result = String.format(Locale.ROOT, "%.2f %s = %.2f C = %.2f F", value, unit.toUpperCase(), celsius, fahrenheit);
    if (converter.isExtremeTemperature(celsius)) {
      result += " (extreme!)";
    }
    return result;
  }
}
