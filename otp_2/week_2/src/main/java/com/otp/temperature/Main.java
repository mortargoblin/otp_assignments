package com.otp.temperature;

import com.otp.temperature.dao.TempRecordDAO;
import com.otp.temperature.dao.TemperatureUnitDAO;
import com.otp.temperature.db.DBConnection;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.NodeOrientation;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.sql.SQLException;
import java.util.Locale;
import java.util.ResourceBundle;

public class Main extends Application {

  private Stage stage;
  private DBConnection db;
  private String dbError;

  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public void start(Stage stage) {
    this.stage = stage;
    db = DBConnection.fromEnvironment();
    connectDatabase(Integer.parseInt(DBConnection.envOrDefault("DB_RETRIES", "1")));
    stage.getIcons().add(new Image(Main.class.getResourceAsStream("/images/thermometer.png")));
    showLanguage(Localization.ENGLISH);
    stage.show();
  }

  /** Creates and seeds the tables; retries because in kubernetes the db may start after the app. */
  private void connectDatabase(int attempts) {
    for (int i = 1; i <= attempts; i++) {
      try {
        db.initSchema();
        new TemperatureUnitDAO(db).seedDefaults();
        dbError = null;
        return;
      } catch (SQLException e) {
        // the converter still works without a database, it just can't keep history
        dbError = e.getMessage();
        System.err.println("database attempt " + i + "/" + attempts + " failed: " + dbError);
        sleepQuietly(5000);
      }
    }
  }

  private static void sleepQuietly(long millis) {
    try {
      Thread.sleep(millis);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }

  /** Reloads the FXML view with the bundle of the given locale (dynamic language switching). */
  void showLanguage(Locale locale) {
    ResourceBundle bundle = Localization.bundle(locale);
    FXMLLoader loader = new FXMLLoader(Main.class.getResource("/fxml/main.fxml"), bundle);
    boolean dbReady = dbError == null;
    loader.setControllerFactory(type -> new MainController(
        new ConversionService(new TemperatureConverter(), locale),
        dbReady ? new TemperatureUnitDAO(db) : null,
        dbReady ? new TempRecordDAO(db) : null,
        dbReady ? db.getUrl() : null,
        dbError,
        this::showLanguage));
    Parent root;
    try {
      root = loader.load();
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
    boolean rtl = Localization.isRightToLeft(locale);
    root.setNodeOrientation(rtl ? NodeOrientation.RIGHT_TO_LEFT : NodeOrientation.LEFT_TO_RIGHT);
    if (rtl) {
      // a font with arabic shaping (installed in the docker image); falls back to the default if missing
      root.setStyle("-fx-font-family: 'DejaVu Sans';");
    }
    stage.setTitle(bundle.getString("app.title"));
    if (stage.getScene() == null) {
      stage.setScene(new Scene(root, 680, 520));
    } else {
      stage.getScene().setRoot(root);
    }
  }
}
