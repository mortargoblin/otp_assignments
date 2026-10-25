package com.otp.temperature.dao;

import com.otp.temperature.db.DBConnection;
import com.otp.temperature.model.TemperatureUnit;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TemperatureUnitDAO {

  private final DBConnection db;

  public TemperatureUnitDAO(DBConnection db) {
    this.db = db;
  }

  public List<TemperatureUnit> findAll() throws SQLException {
    List<TemperatureUnit> units = new ArrayList<>();
    try (Connection conn = db.getConnection();
        PreparedStatement ps = conn.prepareStatement("SELECT id, code, name FROM temperature_unit ORDER BY id");
        ResultSet rs = ps.executeQuery()) {
      while (rs.next()) {
        units.add(map(rs));
      }
    }
    return units;
  }

  public Optional<TemperatureUnit> findByCode(String code) throws SQLException {
    try (Connection conn = db.getConnection();
        PreparedStatement ps = conn.prepareStatement("SELECT id, code, name FROM temperature_unit WHERE code = ?")) {
      ps.setString(1, code.toUpperCase());
      try (ResultSet rs = ps.executeQuery()) {
        return rs.next() ? Optional.of(map(rs)) : Optional.empty();
      }
    }
  }

  public TemperatureUnit insert(String code, String name) throws SQLException {
    try (Connection conn = db.getConnection();
        PreparedStatement ps = conn.prepareStatement(
            "INSERT INTO temperature_unit (code, name) VALUES (?, ?)", PreparedStatement.RETURN_GENERATED_KEYS)) {
      ps.setString(1, code.toUpperCase());
      ps.setString(2, name);
      ps.executeUpdate();
      try (ResultSet keys = ps.getGeneratedKeys()) {
        keys.next();
        return new TemperatureUnit(keys.getInt(1), code.toUpperCase(), name);
      }
    }
  }

  /** Inserts Celsius, Fahrenheit and Kelvin if they are missing. */
  public void seedDefaults() throws SQLException {
    String[][] defaults = {{"C", "Celsius"}, {"F", "Fahrenheit"}, {"K", "Kelvin"}};
    for (String[] unit : defaults) {
      if (findByCode(unit[0]).isEmpty()) {
        insert(unit[0], unit[1]);
      }
    }
  }

  private static TemperatureUnit map(ResultSet rs) throws SQLException {
    return new TemperatureUnit(rs.getInt("id"), rs.getString("code"), rs.getString("name"));
  }
}
