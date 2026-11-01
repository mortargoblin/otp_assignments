package com.otp.temperature;

import com.otp.temperature.model.TempRecord;
import com.otp.temperature.model.TemperatureUnit;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Tests the localized conversion logic that the JavaFX controller delegates to. */
class MainTest {

  private final TemperatureUnit celsius = new TemperatureUnit(1, "C", "Celsius");
  private final TemperatureUnit fahrenheit = new TemperatureUnit(2, "F", "Fahrenheit");
  private final TemperatureUnit kelvin = new TemperatureUnit(3, "K", "Kelvin");

  private ConversionService service(Locale locale) {
    return new ConversionService(new TemperatureConverter(), locale);
  }

  @Test
  void parseAcceptsDotCommaAndArabicSeparator() {
    ConversionService en = service(Localization.ENGLISH);
    assertEquals(12.5, en.parse(" 12.5 "));
    assertEquals(12.5, en.parse("12,5"));
    assertEquals(12.5, en.parse("12٫5"));
  }

  @Test
  void parseErrorsAreLocalized() {
    IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> service(Localization.FINNISH).parse("abc"));
    assertEquals("”abc” ei ole luku.", e.getMessage());
    e = assertThrows(IllegalArgumentException.class, () -> service(Localization.JAPANESE).parse(" "));
    assertEquals("数値を入力してください。", e.getMessage());
    e = assertThrows(IllegalArgumentException.class, () -> service(Localization.ENGLISH).parse(null));
    assertEquals("Please enter a number.", e.getMessage());
  }

  @Test
  void convertCreatesRecord() {
    TempRecord record = service(Localization.ENGLISH).convert("100", celsius, fahrenheit);
    assertEquals(100, record.getInputValue());
    assertEquals(212, record.getResultValue(), 0.0001);
    assertEquals(celsius, record.getFromUnit());
    assertEquals(fahrenheit, record.getToUnit());
  }

  @Test
  void convertBelowAbsoluteZeroIsLocalized() {
    IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
        () -> service(Localization.SWEDISH).convert("-300", celsius, kelvin));
    assertEquals("Temperaturen är under absoluta nollpunkten.", e.getMessage());
  }

  @Test
  void formatUsesLocaleDecimalSeparator() {
    assertEquals("21.50 °C", service(Localization.ENGLISH).format(21.5, celsius));
    assertEquals("21,50 °C", service(Localization.FINNISH).format(21.5, celsius));
    assertEquals("300,00 K", service(Localization.SWEDISH).format(300, kelvin));
  }

  @Test
  void arabicValuesAreMarkedLeftToRight() {
    String text = service(Localization.ARABIC).format(-12.5, fahrenheit);
    assertTrue(text.startsWith(ConversionService.LRM) && text.endsWith(ConversionService.LRM), text);
    assertTrue(text.contains("°F"));
  }

  @Test
  void unitNamesAreLocalizedWithDatabaseFallback() {
    assertEquals("摂氏", service(Localization.JAPANESE).unitName(celsius));
    assertEquals("فهرنهايت", service(Localization.ARABIC).unitName(fahrenheit));
    assertEquals("Rankine", service(Localization.ENGLISH).unitName(new TemperatureUnit(4, "R", "Rankine")));
  }

  @Test
  void describeAddsLocalizedExtremeWarning() {
    ConversionService en = service(Localization.ENGLISH);
    assertEquals("100.00 °C = 212.00 °F (extreme temperature!)", en.describe(en.convert("100", celsius, fahrenheit)));
    assertEquals("32.00 °F = 273.15 K", en.describe(en.convert("32", fahrenheit, kelvin)));
    ConversionService ja = service(Localization.JAPANESE);
    assertTrue(ja.describe(ja.convert("-50", celsius, fahrenheit)).endsWith("（極端な温度です！）"));
  }

  @Test
  void formatTimeIsLocalized() {
    ConversionService fi = service(Localization.FINNISH);
    assertEquals("", fi.formatTime(null));
    String text = fi.formatTime(LocalDateTime.of(2026, 11, 1, 14, 5, 9));
    assertTrue(text.contains("1.11.2026"), text);
    assertTrue(text.contains("14.05.09"), text);
    String arabic = service(Localization.ARABIC).formatTime(LocalDateTime.of(2026, 11, 1, 14, 5, 9));
    assertFalse(arabic.contains("\u200F") || arabic.contains("\u061C"), arabic);
    assertTrue(arabic.contains("2026"), arabic);
  }

  @Test
  void messagesAreFormattedAndBundleIsExposed() {
    ConversionService sv = service(Localization.SWEDISH);
    assertEquals("Databasfel: boom", sv.message("status.dbError", "boom"));
    assertEquals(Localization.SWEDISH, sv.getLocale());
    assertEquals("Omvandla", sv.getMessages().getString("button.convert"));
  }

  @Test
  void offlineUnitsCoverAllConversions() {
    assertEquals(3, MainController.OFFLINE_UNITS.size());
    assertFalse(MainController.OFFLINE_UNITS.stream().anyMatch(u -> u.getCode().isBlank()));
  }
}
