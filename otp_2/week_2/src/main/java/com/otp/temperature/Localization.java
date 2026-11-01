package com.otp.temperature;

import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

/** Supported languages and access to the MessagesBundle_xx_XX.properties files (UTF-8). */
public final class Localization {

  public static final String BUNDLE = "MessagesBundle";

  public static final Locale ENGLISH = Locale.of("en", "US");
  public static final Locale FINNISH = Locale.of("fi", "FI");
  public static final Locale SWEDISH = Locale.of("sv", "SE");
  public static final Locale JAPANESE = Locale.of("ja", "JP");
  public static final Locale ARABIC = Locale.of("ar", "AE");

  public static final List<Locale> SUPPORTED = List.of(ENGLISH, FINNISH, SWEDISH, JAPANESE, ARABIC);

  private Localization() {
  }

  /** Loads the bundle for the locale. Java 9+ reads .properties bundles as UTF-8. */
  public static ResourceBundle bundle(Locale locale) {
    // NoFallback control: an unsupported locale gets English, not the JVM default locale
    return ResourceBundle.getBundle(BUNDLE, isSupported(locale) ? locale : ENGLISH,
        ResourceBundle.Control.getNoFallbackControl(ResourceBundle.Control.FORMAT_PROPERTIES));
  }

  public static boolean isSupported(Locale locale) {
    return SUPPORTED.contains(locale);
  }

  public static boolean isRightToLeft(Locale locale) {
    return "ar".equals(locale.getLanguage());
  }

  /** The language's own name, e.g. "Suomi", so users can find their language in any UI language. */
  public static String nativeName(Locale locale) {
    String name = locale.getDisplayLanguage(locale);
    return name.isEmpty() ? locale.toString() : Character.toUpperCase(name.charAt(0)) + name.substring(1);
  }
}
