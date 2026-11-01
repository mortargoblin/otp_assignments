package com.otp.temperature.dao;

import com.otp.temperature.TestDatabase;
import com.otp.temperature.db.DBConnection;
import com.otp.temperature.model.TempRecord;
import com.otp.temperature.model.TemperatureUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TempRecordDAOTest {

  private TempRecordDAO dao;
  private TemperatureUnit celsius;
  private TemperatureUnit fahrenheit;

  @BeforeEach
  void setUp() throws SQLException {
    DBConnection db = TestDatabase.create();
    TemperatureUnitDAO unitDAO = new TemperatureUnitDAO(db);
    unitDAO.seedDefaults();
    celsius = unitDAO.findByCode("C").orElseThrow();
    fahrenheit = unitDAO.findByCode("F").orElseThrow();
    dao = new TempRecordDAO(db);
  }

  @Test
  void saveAssignsIdAndTimestamp() throws SQLException {
    TempRecord saved = dao.save(new TempRecord(100, celsius, fahrenheit, 212));
    assertTrue(saved.getId() > 0);
    assertNotNull(saved.getCreatedAt());
  }

  @Test
  void findAllJoinsUnitsNewestFirst() throws SQLException {
    dao.save(new TempRecord(0, celsius, fahrenheit, 32));
    dao.save(new TempRecord(212, fahrenheit, celsius, 100));
    List<TempRecord> records = dao.findAll();
    assertEquals(2, records.size());
    TempRecord newest = records.get(0);
    assertEquals(212, newest.getInputValue());
    assertEquals("F", newest.getFromUnit().getCode());
    assertEquals("Celsius", newest.getToUnit().getName());
    assertEquals(100, newest.getResultValue());
    assertNotNull(newest.getCreatedAt());
  }

  @Test
  void deleteAllClearsHistory() throws SQLException {
    dao.save(new TempRecord(0, celsius, fahrenheit, 32));
    assertEquals(1, dao.deleteAll());
    assertTrue(dao.findAll().isEmpty());
  }

  @Test
  void foreignKeyRejectsUnknownUnit() {
    TemperatureUnit bogus = new TemperatureUnit(999, "X", "Bogus");
    assertThrows(SQLException.class, () -> dao.save(new TempRecord(1, bogus, celsius, 1)));
  }
}
