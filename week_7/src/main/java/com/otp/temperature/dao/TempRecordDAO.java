package com.otp.temperature.dao;

import com.otp.temperature.db.DBConnection;
import com.otp.temperature.model.TempRecord;
import com.otp.temperature.model.TemperatureUnit;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TempRecordDAO {

  private static final String SELECT_ALL = """
      SELECT r.id, r.input_value, r.result_value, r.created_at,
             f.id AS f_id, f.code AS f_code, f.name AS f_name,
             t.id AS t_id, t.code AS t_code, t.name AS t_name
      FROM temp_record r
      JOIN temperature_unit f ON r.from_unit_id = f.id
      JOIN temperature_unit t ON r.to_unit_id = t.id
      ORDER BY r.id DESC
      """;

  private final DBConnection db;

  public TempRecordDAO(DBConnection db) {
    this.db = db;
  }

  public TempRecord save(TempRecord record) throws SQLException {
    LocalDateTime now = LocalDateTime.now().withNano(0);
    try (Connection conn = db.getConnection();
        PreparedStatement ps = conn.prepareStatement(
            "INSERT INTO temp_record (input_value, from_unit_id, to_unit_id, result_value, created_at) "
                + "VALUES (?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {
      ps.setDouble(1, record.getInputValue());
      ps.setInt(2, record.getFromUnit().getId());
      ps.setInt(3, record.getToUnit().getId());
      ps.setDouble(4, record.getResultValue());
      ps.setTimestamp(5, Timestamp.valueOf(now));
      ps.executeUpdate();
      try (ResultSet keys = ps.getGeneratedKeys()) {
        keys.next();
        record.setId(keys.getInt(1));
      }
    }
    record.setCreatedAt(now);
    return record;
  }

  public List<TempRecord> findAll() throws SQLException {
    List<TempRecord> records = new ArrayList<>();
    try (Connection conn = db.getConnection();
        PreparedStatement ps = conn.prepareStatement(SELECT_ALL);
        ResultSet rs = ps.executeQuery()) {
      while (rs.next()) {
        TemperatureUnit from = new TemperatureUnit(rs.getInt("f_id"), rs.getString("f_code"), rs.getString("f_name"));
        TemperatureUnit to = new TemperatureUnit(rs.getInt("t_id"), rs.getString("t_code"), rs.getString("t_name"));
        records.add(new TempRecord(rs.getInt("id"), rs.getDouble("input_value"), from, to,
            rs.getDouble("result_value"), rs.getTimestamp("created_at").toLocalDateTime()));
      }
    }
    return records;
  }

  public int deleteAll() throws SQLException {
    try (Connection conn = db.getConnection(); Statement stmt = conn.createStatement()) {
      return stmt.executeUpdate("DELETE FROM temp_record");
    }
  }
}
