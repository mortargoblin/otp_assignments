CREATE TABLE IF NOT EXISTS temperature_unit (
  id INT AUTO_INCREMENT PRIMARY KEY,
  code CHAR(1) NOT NULL UNIQUE,
  name VARCHAR(20) NOT NULL
);

CREATE TABLE IF NOT EXISTS temp_record (
  id INT AUTO_INCREMENT PRIMARY KEY,
  input_value DOUBLE NOT NULL,
  from_unit_id INT NOT NULL,
  to_unit_id INT NOT NULL,
  result_value DOUBLE NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (from_unit_id) REFERENCES temperature_unit (id),
  FOREIGN KEY (to_unit_id) REFERENCES temperature_unit (id)
);
