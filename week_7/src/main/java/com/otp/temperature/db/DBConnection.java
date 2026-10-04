package com.otp.temperature.db;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DBConnection {

  static final String DEFAULT_URL = "jdbc:mariadb://localhost:3306/temperature_db";
  static final String DEFAULT_USER = "temp_user";
  static final String DEFAULT_PASSWORD = "temp_pass";

  private final String url;
  private final String user;
  private final String password;

  public DBConnection(String url, String user, String password) {
    this.url = url;
    this.user = user;
    this.password = password;
  }

  /** Reads DB_URL, DB_USER and DB_PASSWORD, falling back to the local defaults. */
  public static DBConnection fromEnvironment() {
    return new DBConnection(
        envOrDefault("DB_URL", DEFAULT_URL),
        envOrDefault("DB_USER", DEFAULT_USER),
        envOrDefault("DB_PASSWORD", DEFAULT_PASSWORD));
  }

  static String envOrDefault(String name, String fallback) {
    String value = System.getenv(name);
    return value == null || value.isBlank() ? fallback : value;
  }

  public String getUrl() {
    return url;
  }

  public Connection getConnection() throws SQLException {
    return DriverManager.getConnection(url, user, password);
  }

  /** Creates the tables from schema.sql if they do not exist yet. */
  public void initSchema() throws SQLException {
    String script = readSchema();
    try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
      for (String sql : script.split(";")) {
        if (!sql.isBlank()) {
          stmt.execute(sql);
        }
      }
    }
  }

  static String readSchema() {
    try (InputStream in = DBConnection.class.getResourceAsStream("/schema.sql")) {
      if (in == null) {
        throw new IllegalStateException("schema.sql not found on classpath");
      }
      return new String(in.readAllBytes(), StandardCharsets.UTF_8);
    } catch (IOException e) {
      throw new IllegalStateException("Could not read schema.sql", e);
    }
  }
}
