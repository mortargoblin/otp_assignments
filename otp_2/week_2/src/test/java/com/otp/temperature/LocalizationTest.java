package com.otp.temperature;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LocalizationTest {

  @Test
  void supportsFiveLanguages() {
    assertEquals(5, Localization.SUPPORTED.size());
    assertTrue(Localization.isSupported(Locale.of("ar", "AE")));
    assertFalse(Localization.isSupported(Locale.GERMANY));
  }

  @ParameterizedTest
  @ValueSource(strings = {"en-US", "fi-FI", "sv-SE", "ja-JP", "ar-AE"})
  void everyBundleHasTheSameKeysAsEnglish(String tag) {
    Locale locale = Locale.forLanguageTag(tag);
    ResourceBundle bundle = Localization.bundle(locale);
    assertEquals(locale, bundle.getLocale());
    assertEquals(new TreeSet<>(Localization.bundle(Localization.ENGLISH).keySet()), new TreeSet<>(bundle.keySet()));
  }

  @Test
  void bundlesAreReadAsUtf8() {
    assertEquals("Lämpötilamuunnin", Localization.bundle(Localization.FINNISH).getString("app.title"));
    assertEquals("Temperaturomvandlare", Localization.bundle(Localization.SWEDISH).getString("app.title"));
    assertEquals("温度変換ツール", Localization.bundle(Localization.JAPANESE).getString("app.title"));
    assertEquals("محول درجة الحرارة", Localization.bundle(Localization.ARABIC).getString("app.title"));
  }

  @Test
  void unsupportedLocaleFallsBackToEnglish() {
    assertEquals(Localization.ENGLISH, Localization.bundle(Locale.GERMANY).getLocale());
  }

  @Test
  void onlyArabicIsRightToLeft() {
    assertTrue(Localization.isRightToLeft(Localization.ARABIC));
    Localization.SUPPORTED.stream().filter(l -> l != Localization.ARABIC)
        .forEach(l -> assertFalse(Localization.isRightToLeft(l)));
  }

  @Test
  void nativeNames() {
    assertEquals("English", Localization.nativeName(Localization.ENGLISH));
    assertEquals("Suomi", Localization.nativeName(Localization.FINNISH));
    assertEquals("Svenska", Localization.nativeName(Localization.SWEDISH));
    assertEquals("日本語", Localization.nativeName(Localization.JAPANESE));
    assertEquals("العربية", Localization.nativeName(Localization.ARABIC));
    assertEquals("", Localization.nativeName(Locale.ROOT));
  }

  @Test
  void everyFxmlKeyExistsInEveryBundle() throws IOException {
    String fxml;
    try (InputStream in = LocalizationTest.class.getResourceAsStream("/fxml/main.fxml")) {
      assertNotNull(in, "main.fxml missing");
      fxml = new String(in.readAllBytes(), StandardCharsets.UTF_8);
    }
    Set<String> keys = new TreeSet<>();
    Matcher m = Pattern.compile("\"%([\\w.]+)\"").matcher(fxml);
    while (m.find()) {
      keys.add(m.group(1));
    }
    assertFalse(keys.isEmpty());
    for (Locale locale : Localization.SUPPORTED) {
      ResourceBundle bundle = Localization.bundle(locale);
      for (String key : keys) {
        assertTrue(bundle.containsKey(key), key + " missing for " + locale);
      }
    }
  }
}
