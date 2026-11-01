package com.otp.temperature;

import com.otp.temperature.db.DBConnection;

import java.sql.SQLException;
import java.util.UUID;

/** Fresh in-memory H2 database in MariaDB mode, with the real schema applied. */
public final class TestDatabase {

  private TestDatabase() {
  }

  public static DBConnection create() throws SQLException {
    String url = "jdbc:h2:mem:" + UUID.randomUUID() + ";MODE=MariaDB;DB_CLOSE_DELAY=-1";
    DBConnection db = new DBConnection(url, "sa", "");
    db.initSchema();
    return db;
  }
}
