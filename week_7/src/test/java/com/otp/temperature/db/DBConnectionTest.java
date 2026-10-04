package com.otp.temperature.db;

import com.otp.temperature.TestDatabase;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DBConnectionTest {

  @Test
  void envOrDefaultFallsBackForMissingVariable() {
    assertEquals("fallback", DBConnection.envOrDefault("SURELY_NOT_SET_" + System.nanoTime(), "fallback"));
  }

  @Test
  void fromEnvironmentHasUrl() {
    assertNotNull(DBConnection.fromEnvironment().getUrl());
  }

  @Test
  void readSchemaContainsBothTables() {
    String schema = DBConnection.readSchema();
    assertTrue(schema.contains("temperature_unit"));
    assertTrue(schema.contains("temp_record"));
  }

  @Test
  void initSchemaCreatesTablesAndIsRepeatable() throws SQLException {
    DBConnection db = TestDatabase.create();
    assertDoesNotThrow(db::initSchema);
    try (Connection conn = db.getConnection();
        ResultSet rs = conn.createStatement().executeQuery("SELECT COUNT(*) FROM temp_record")) {
      rs.next();
      assertEquals(0, rs.getInt(1));
    }
  }
}
