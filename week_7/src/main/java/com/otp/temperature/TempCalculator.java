package com.otp.temperature;

public class TempCalculator {

  public double fahrenheitToCelsius(double fahrenheit) {
    return (fahrenheit - 32) * 5 / 9;
  }

  public double celsiusToFahrenheit(double celsius) {
    return (celsius * 9 / 5) + 32;
  }

  public double kelvinToCelsius(double kelvin) {
    return kelvin - 273.15;
  }

  public double celsiusToKelvin(double celsius) {
    return celsius + 273.15;
  }

  public boolean isExtremeTemperature(double celsius) {
    return celsius < -40 || celsius > 50;
  }

  public double toCelsius(double value, String unitCode) {
    return switch (unitCode.toUpperCase()) {
      case "C" -> value;
      case "F" -> fahrenheitToCelsius(value);
      case "K" -> kelvinToCelsius(value);
      default -> throw new IllegalArgumentException("Unknown unit: " + unitCode);
    };
  }

  public double fromCelsius(double celsius, String unitCode) {
    return switch (unitCode.toUpperCase()) {
      case "C" -> celsius;
      case "F" -> celsiusToFahrenheit(celsius);
      case "K" -> celsiusToKelvin(celsius);
      default -> throw new IllegalArgumentException("Unknown unit: " + unitCode);
    };
  }

  public double convert(double value, String fromCode, String toCode) {
    double celsius = toCelsius(value, fromCode);
    if (celsius < -273.15) {
      throw new IllegalArgumentException("Temperature below absolute zero");
    }
    return fromCelsius(celsius, toCode);
  }
}
