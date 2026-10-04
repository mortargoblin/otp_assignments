package com.otp.temperature;

import com.otp.temperature.dao.TempRecordDAO;
import com.otp.temperature.dao.TemperatureUnitDAO;
import com.otp.temperature.db.DBConnection;
import com.otp.temperature.model.TempRecord;
import com.otp.temperature.model.TemperatureUnit;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;

public class Main extends Application {

  static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

  private final TempCalculator calculator = new TempCalculator();
  private TemperatureUnitDAO unitDAO;
  private TempRecordDAO recordDAO;

  private final TextField valueField = new TextField();
  private final ComboBox<TemperatureUnit> fromBox = new ComboBox<>();
  private final ComboBox<TemperatureUnit> toBox = new ComboBox<>();
  private final Label resultLabel = new Label();
  private final Label statusLabel = new Label();
  private final TableView<TempRecord> historyTable = new TableView<>();

  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public void start(Stage stage) {
    DBConnection db = DBConnection.fromEnvironment();
    unitDAO = new TemperatureUnitDAO(db);
    recordDAO = new TempRecordDAO(db);

    ImageView icon = new ImageView(new Image(Main.class.getResourceAsStream("/images/thermometer.png")));
    icon.setFitHeight(64);
    icon.setPreserveRatio(true);
    Label title = new Label("Temperature Converter");
    title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");
    HBox header = new HBox(12, icon, title);
    header.setAlignment(Pos.CENTER_LEFT);

    valueField.setPromptText("Value");
    valueField.setPrefColumnCount(8);
    Button convertButton = new Button("Convert");
    convertButton.setDefaultButton(true);
    convertButton.setOnAction(e -> onConvert());
    HBox inputRow = new HBox(8, valueField, fromBox, new Label("→"), toBox, convertButton);
    inputRow.setAlignment(Pos.CENTER_LEFT);

    resultLabel.setStyle("-fx-font-size: 16px;");

    historyTable.getColumns().add(column("Time", r -> r.getCreatedAt() == null ? "" : TIME_FORMAT.format(r.getCreatedAt())));
    historyTable.getColumns().add(column("Input", r -> formatValue(r.getInputValue(), r.getFromUnit())));
    historyTable.getColumns().add(column("Result", r -> formatValue(r.getResultValue(), r.getToUnit())));
    historyTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
    VBox.setVgrow(historyTable, Priority.ALWAYS);

    Button clearButton = new Button("Clear history");
    clearButton.setOnAction(e -> onClearHistory());

    VBox root = new VBox(12, header, inputRow, resultLabel, new Label("History (from database)"),
        historyTable, new HBox(12, clearButton, statusLabel));
    root.setPadding(new Insets(16));

    loadFromDatabase(db);

    stage.setTitle("Temperature Converter");
    stage.getIcons().add(icon.getImage());
    stage.setScene(new Scene(root, 560, 480));
    stage.show();
  }

  private void loadFromDatabase(DBConnection db) {
    try {
      db.initSchema();
      unitDAO.seedDefaults();
      List<TemperatureUnit> units = unitDAO.findAll();
      fromBox.setItems(FXCollections.observableArrayList(units));
      toBox.setItems(FXCollections.observableArrayList(units));
      fromBox.getSelectionModel().select(0);
      toBox.getSelectionModel().select(Math.min(1, units.size() - 1));
      refreshHistory();
      statusLabel.setText("Connected to " + db.getUrl());
    } catch (SQLException e) {
      statusLabel.setText("Database error: " + e.getMessage());
    }
  }

  private void onConvert() {
    TemperatureUnit from = fromBox.getValue();
    TemperatureUnit to = toBox.getValue();
    if (from == null || to == null) {
      resultLabel.setText("No units available (database not connected?)");
      return;
    }
    try {
      TempRecord record = createRecord(calculator, valueField.getText(), from, to);
      resultLabel.setText(describe(calculator, record));
      recordDAO.save(record);
      refreshHistory();
    } catch (IllegalArgumentException e) {
      resultLabel.setText("Invalid input: " + e.getMessage());
    } catch (SQLException e) {
      statusLabel.setText("Could not save: " + e.getMessage());
    }
  }

  private void onClearHistory() {
    try {
      recordDAO.deleteAll();
      refreshHistory();
    } catch (SQLException e) {
      statusLabel.setText("Could not clear: " + e.getMessage());
    }
  }

  private void refreshHistory() throws SQLException {
    historyTable.setItems(FXCollections.observableArrayList(recordDAO.findAll()));
  }

  private static TableColumn<TempRecord, String> column(String title, Function<TempRecord, String> getter) {
    TableColumn<TempRecord, String> col = new TableColumn<>(title);
    col.setCellValueFactory(cell -> new SimpleStringProperty(getter.apply(cell.getValue())));
    return col;
  }

  static double parseValue(String text) {
    if (text == null || text.isBlank()) {
      throw new IllegalArgumentException("enter a number");
    }
    try {
      return Double.parseDouble(text.trim().replace(',', '.'));
    } catch (NumberFormatException e) {
      throw new IllegalArgumentException("'" + text.trim() + "' is not a number");
    }
  }

  static TempRecord createRecord(TempCalculator calculator, String text, TemperatureUnit from, TemperatureUnit to) {
    double value = parseValue(text);
    double result = calculator.convert(value, from.getCode(), to.getCode());
    return new TempRecord(value, from, to, result);
  }

  static String formatValue(double value, TemperatureUnit unit) {
    String symbol = unit.getCode().equals("K") ? " K" : " °" + unit.getCode();
    return String.format(Locale.ROOT, "%.2f%s", value, symbol);
  }

  static String describe(TempCalculator calculator, TempRecord record) {
    String text = formatValue(record.getInputValue(), record.getFromUnit()) + " = "
        + formatValue(record.getResultValue(), record.getToUnit());
    double celsius = calculator.toCelsius(record.getInputValue(), record.getFromUnit().getCode());
    if (calculator.isExtremeTemperature(celsius)) {
      text += " (extreme!)";
    }
    return text;
  }
}
