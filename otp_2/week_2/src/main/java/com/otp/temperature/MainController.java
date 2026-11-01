package com.otp.temperature;

import com.otp.temperature.dao.TempRecordDAO;
import com.otp.temperature.dao.TemperatureUnitDAO;
import com.otp.temperature.model.TempRecord;
import com.otp.temperature.model.TemperatureUnit;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;

import java.sql.SQLException;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

/** Controller for main.fxml. All texts come from the resource bundle given to the FXMLLoader. */
public class MainController {

  /** Used when the database is unreachable so the converter keeps working. */
  static final List<TemperatureUnit> OFFLINE_UNITS = List.of(
      new TemperatureUnit(1, "C", "Celsius"),
      new TemperatureUnit(2, "F", "Fahrenheit"),
      new TemperatureUnit(3, "K", "Kelvin"));

  private final ConversionService service;
  private final TemperatureUnitDAO unitDAO;
  private final TempRecordDAO recordDAO;
  private final String dbUrl;
  private final String dbError;
  private final Consumer<Locale> languageSwitcher;

  @FXML private ComboBox<Locale> languageBox;
  @FXML private TextField valueField;
  @FXML private ComboBox<TemperatureUnit> fromBox;
  @FXML private ComboBox<TemperatureUnit> toBox;
  @FXML private Label resultLabel;
  @FXML private Label statusLabel;
  @FXML private TableView<TempRecord> historyTable;
  @FXML private TableColumn<TempRecord, String> timeColumn;
  @FXML private TableColumn<TempRecord, String> inputColumn;
  @FXML private TableColumn<TempRecord, String> resultColumn;

  public MainController(ConversionService service, TemperatureUnitDAO unitDAO, TempRecordDAO recordDAO,
      String dbUrl, String dbError, Consumer<Locale> languageSwitcher) {
    this.service = service;
    this.unitDAO = unitDAO;
    this.recordDAO = recordDAO;
    this.dbUrl = dbUrl;
    this.dbError = dbError;
    this.languageSwitcher = languageSwitcher;
  }

  @FXML
  private void initialize() {
    languageBox.setItems(FXCollections.observableArrayList(Localization.SUPPORTED));
    languageBox.setConverter(new StringConverter<>() {
      @Override
      public String toString(Locale locale) {
        return locale == null ? "" : Localization.nativeName(locale);
      }

      @Override
      public Locale fromString(String text) {
        return null;
      }
    });
    languageBox.setValue(service.getLocale());
    languageBox.setOnAction(e -> {
      Locale selected = languageBox.getValue();
      if (selected != null && !selected.equals(service.getLocale())) {
        languageSwitcher.accept(selected);
      }
    });

    setupUnitBox(fromBox);
    setupUnitBox(toBox);

    timeColumn.setCellValueFactory(c -> new SimpleStringProperty(service.formatTime(c.getValue().getCreatedAt())));
    inputColumn.setCellValueFactory(c -> new SimpleStringProperty(
        service.format(c.getValue().getInputValue(), c.getValue().getFromUnit())));
    resultColumn.setCellValueFactory(c -> new SimpleStringProperty(
        service.format(c.getValue().getResultValue(), c.getValue().getToUnit())));

    loadUnitsAndHistory();
  }

  private void setupUnitBox(ComboBox<TemperatureUnit> box) {
    box.setCellFactory(list -> new ListCell<>() {
      @Override
      protected void updateItem(TemperatureUnit unit, boolean empty) {
        super.updateItem(unit, empty);
        setText(empty || unit == null ? null : service.unitName(unit));
      }
    });
    box.setButtonCell(box.getCellFactory().call(null));
  }

  private void loadUnitsAndHistory() {
    List<TemperatureUnit> units = OFFLINE_UNITS;
    if (unitDAO == null) {
      statusLabel.setText(service.message("status.offline", dbError));
    } else {
      try {
        units = unitDAO.findAll();
        refreshHistory();
        statusLabel.setText(service.message("status.connected", dbUrl));
      } catch (SQLException e) {
        statusLabel.setText(service.message("status.dbError", e.getMessage()));
      }
    }
    fromBox.setItems(FXCollections.observableArrayList(units));
    toBox.setItems(FXCollections.observableArrayList(units));
    fromBox.getSelectionModel().select(0);
    toBox.getSelectionModel().select(Math.min(1, units.size() - 1));
  }

  @FXML
  private void onConvert() {
    TemperatureUnit from = fromBox.getValue();
    TemperatureUnit to = toBox.getValue();
    try {
      TempRecord record = service.convert(valueField.getText(), from, to);
      resultLabel.setText(service.describe(record));
      if (recordDAO != null) {
        recordDAO.save(record);
        refreshHistory();
      }
    } catch (IllegalArgumentException e) {
      resultLabel.setText(e.getMessage());
    } catch (SQLException e) {
      statusLabel.setText(service.message("status.saveError", e.getMessage()));
    }
  }

  @FXML
  private void onClearHistory() {
    if (recordDAO == null) {
      return;
    }
    try {
      recordDAO.deleteAll();
      refreshHistory();
    } catch (SQLException e) {
      statusLabel.setText(service.message("status.dbError", e.getMessage()));
    }
  }

  private void refreshHistory() throws SQLException {
    historyTable.setItems(FXCollections.observableArrayList(recordDAO.findAll()));
  }
}
