package com.otp.temperature.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class TempRecordTest {

  private final TemperatureUnit c = new TemperatureUnit(1, "C", "Celsius");
  private final TemperatureUnit f = new TemperatureUnit(2, "F", "Fahrenheit");

  @Test
  void newRecordHasNoIdOrTimestamp() {
    TempRecord record = new TempRecord(0, c, f, 32);
    assertEquals(0, record.getId());
    assertNull(record.getCreatedAt());
    assertEquals(0, record.getInputValue());
    assertEquals(32, record.getResultValue());
    assertEquals(c, record.getFromUnit());
    assertEquals(f, record.getToUnit());
  }

  @Test
  void settersUpdateIdAndTimestamp() {
    LocalDateTime time = LocalDateTime.of(2026, 10, 4, 12, 0);
    TempRecord record = new TempRecord(0, c, f, 32);
    record.setId(7);
    record.setCreatedAt(time);
    assertEquals(7, record.getId());
    assertEquals(time, record.getCreatedAt());
  }
}
