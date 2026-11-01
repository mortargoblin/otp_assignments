package com.otp.temperature;

import com.otp.temperature.model.TempRecord;
import com.otp.temperature.model.TemperatureUnit;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/** Conversion logic with localized parsing, formatting and messages. Has no JavaFX dependency. */
public class ConversionService {

  static final String LRM = "\u200E";

  private final TemperatureConverter converter;
  private final ResourceBundle messages;
  private final Locale locale;
  private final DateTimeFormatter timeFormat;

  public ConversionService(TemperatureConverter converter, Locale locale) {
    this.converter = converter;
    this.locale = locale;
    this.messages = Localization.bundle(locale);
    this.timeFormat = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT, FormatStyle.MEDIUM).withLocale(locale);
  }

  public ResourceBundle getMessages() {
    return messages;
  }

  public Locale getLocale() {
    return locale;
  }

  /** Accepts a dot, a comma (fi/sv) or an Arabic decimal separator. */
  public double parse(String text) {
    if (text == null || text.isBlank()) {
      throw new IllegalArgumentException(messages.getString("error.empty"));
    }
    String normalized = text.trim().replace(',', '.').replace('٫', '.');
    try {
      return Double.parseDouble(normalized);
    } catch (NumberFormatException e) {
      throw new IllegalArgumentException(MessageFormat.format(messages.getString("error.number"), text.trim()));
    }
  }

  public TempRecord convert(String text, TemperatureUnit from, TemperatureUnit to) {
    double value = parse(text);
    if (converter.isBelowAbsoluteZero(converter.toCelsius(value, from.getCode()))) {
      throw new IllegalArgumentException(messages.getString("error.absoluteZero"));
    }
    return new TempRecord(value, from, to, converter.convert(value, from.getCode(), to.getCode()));
  }

  /** Localized unit name from the bundle, falling back to the name stored in the database. */
  public String unitName(TemperatureUnit unit) {
    try {
      return messages.getString("unit." + unit.getCode());
    } catch (MissingResourceException e) {
      return unit.getName();
    }
  }

  public String format(double value, TemperatureUnit unit) {
    String symbol = unit.getCode().equals("K") ? "K" : "°" + unit.getCode();
    String text = String.format(locale, "%.2f %s", value, symbol);
    // in right-to-left text, left-to-right marks keep "-12.50 °C" in reading order
    return Localization.isRightToLeft(locale) ? LRM + text + LRM : text;
  }

  public String formatTime(LocalDateTime time) {
    // cldr arabic patterns contain rtl/arabic letter marks that javafx draws as boxes
    return time == null ? "" : timeFormat.format(time).replaceAll("[\u200F\u061C]", "");
  }

  public String describe(TempRecord record) {
    String text = MessageFormat.format(messages.getString("result.text"),
        format(record.getInputValue(), record.getFromUnit()), format(record.getResultValue(), record.getToUnit()));
    double celsius = converter.toCelsius(record.getInputValue(), record.getFromUnit().getCode());
    if (converter.isExtremeTemperature(celsius)) {
      text += " " + messages.getString("warning.extreme");
    }
    return text;
  }

  public String message(String key, Object... args) {
    return MessageFormat.format(messages.getString(key), args);
  }
}
