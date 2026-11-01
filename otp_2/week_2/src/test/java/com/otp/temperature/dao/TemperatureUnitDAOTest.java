package com.otp.temperature.dao;

import com.otp.temperature.TestDatabase;
import com.otp.temperature.model.TemperatureUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TemperatureUnitDAOTest {

  private TemperatureUnitDAO dao;

  @BeforeEach
  void setUp() throws SQLException {
    dao = new TemperatureUnitDAO(TestDatabase.create());
  }

  @Test
  void emptyAtStart() throws SQLException {
    assertTrue(dao.findAll().isEmpty());
  }

  @Test
  void seedDefaultsIsIdempotent() throws SQLException {
    dao.seedDefaults();
    dao.seedDefaults();
    List<TemperatureUnit> units = dao.findAll();
    assertEquals(List.of("C", "F", "K"), units.stream().map(TemperatureUnit::getCode).toList());
  }

  @Test
  void insertAndFindByCode() throws SQLException {
    TemperatureUnit inserted = dao.insert("r", "Rankine");
    TemperatureUnit found = dao.findByCode("R").orElseThrow();
    assertEquals(inserted.getId(), found.getId());
    assertEquals("Rankine", found.getName());
    assertTrue(dao.findByCode("X").isEmpty());
  }

  @Test
  void duplicateCodeRejected() throws SQLException {
    dao.insert("C", "Celsius");
    assertThrows(SQLException.class, () -> dao.insert("C", "Celsius again"));
  }
}
