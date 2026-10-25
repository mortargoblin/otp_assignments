package com.otp.temperature;

import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Scanner;

public class Main {

  static final String BUNDLE = "MessagesBundle";

  private final Scanner in;
  private final PrintStream out;
  private final TemperatureConverter converter = new TemperatureConverter();
  private Locale locale;
  private ResourceBundle messages;

  Main(Scanner in, PrintStream out) {
    this.in = in;
    this.out = out;
  }

  public static void main(String[] args) {
    // force utf-8 so japanese etc. prints correctly even if the terminal/container locale is plain ascii
    PrintStream out = new PrintStream(new FileOutputStream(FileDescriptor.out), true, StandardCharsets.UTF_8);
    Scanner in = new Scanner(System.in, StandardCharsets.UTF_8);
    new Main(in, out).run();
  }

  void run() {
    // the language menu is shown before a language is chosen, so it is not localized
    out.println("Select language / Valitse kieli / Välj språk / 言語を選択してください:");
    out.println("  1) English");
    out.println("  2) Suomi");
    out.println("  3) Svenska");
    out.println("  4) 日本語");
    out.print("> ");
    locale = selectLocale(in.hasNextLine() ? in.nextLine() : "");
    messages = ResourceBundle.getBundle(BUNDLE, locale);

    out.println();
    out.println(messages.getString("app.title"));

    while (true) {
      out.println();
      out.println("  1) " + messages.getString("menu.f2c"));
      out.println("  2) " + messages.getString("menu.c2f"));
      out.println("  3) " + messages.getString("menu.exit"));
      out.print(messages.getString("menu.choice") + " ");
      if (!in.hasNextLine()) {
        break;
      }
      String choice = in.nextLine().trim();
      if (choice.equals("1")) {
        Double f = readNumber("input.fahrenheit");
        if (f != null) {
          double c = converter.fahrenheitToCelsius(f);
          printResult("result.f2c", f, c, c);
        }
      } else if (choice.equals("2")) {
        Double c = readNumber("input.celsius");
        if (c != null) {
          printResult("result.c2f", c, converter.celsiusToFahrenheit(c), c);
        }
      } else if (choice.equals("3")) {
        break;
      } else {
        out.println(messages.getString("error.choice"));
      }
    }
    out.println(messages.getString("goodbye"));
  }

  static Locale selectLocale(String choice) {
    return switch (choice.trim()) {
      case "2" -> Locale.of("fi", "FI");
      case "3" -> Locale.of("sv", "SE");
      case "4" -> Locale.of("ja", "JP");
      default -> Locale.of("en", "US");
    };
  }

  private Double readNumber(String promptKey) {
    out.print(messages.getString(promptKey) + " ");
    String line = in.hasNextLine() ? in.nextLine().trim() : "";
    try {
      // accept a decimal comma too, as used in finnish and swedish
      return Double.parseDouble(line.replace(',', '.'));
    } catch (NumberFormatException e) {
      out.println(messages.getString("error.number"));
      return null;
    }
  }

  private void printResult(String key, double input, double result, double celsius) {
    out.println(MessageFormat.format(messages.getString(key), format(input), format(result)));
    if (converter.isExtremeTemperature(celsius)) {
      out.println(messages.getString("warning.extreme"));
    }
  }

  private String format(double value) {
    return String.format(locale, "%.2f", value);
  }
}
