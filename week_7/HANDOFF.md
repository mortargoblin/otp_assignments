# Week 7 handoff (for the next Claude instance)

Assignment: In_Class_Tempreture_2026_Update.pdf (in this folder). Turn the
temperature converter into a JavaFX app with a database of 2+ related tables,
plus unit tests, JaCoCo, Jenkinsfile, Dockerfile, an image on Docker Hub, and
screenshots of the app running on an X server.

## Done (on the previous machine)
- JavaFX app: `Main` (UI, thermometer image, conversion, history table),
  `Launcher` (fat-jar entry point), `TempCalculator`.
- DB: `db/DBConnection` (reads DB_URL / DB_USER / DB_PASSWORD; defaults
  `jdbc:mariadb://localhost:3306/temperature_db`, `temp_user` / `temp_pass`),
  `src/main/resources/schema.sql` with tables `temperature_unit` and
  `temp_record` (two foreign keys to temperature_unit). The app creates the
  tables and seeds C/F/K on startup.
- DAOs: `TemperatureUnitDAO`, `TempRecordDAO`. Models: `TemperatureUnit`, `TempRecord`.
- Tests for all of the above. DAO/DB tests use in-memory H2 in MariaDB mode,
  so no DB server is needed. `mvn clean install` passes; JaCoCo reports 59%
  (the uncovered part is the JavaFX UI wiring).
- `Dockerfile` (multi-stage, runtime installs the GTK/X libs, runs with `-Dprism.order=sw`),
  `docker-compose.yml` (MariaDB service `db` plus the app), `Makefile`, and
  `Jenkinsfile` (copied from week 6 with dir changed to week_7; image
  `mortargoblin/temperature-converter-fx:latest`; Docker Hub credentials id `Docker_Hub`).

## Not done or never verified (Docker and Jenkins weren't available)
1. Docker image build: `docker build -t mortargoblin/temperature-converter-fx:latest .`
   It has never been built. Watch the apt package names in the runtime stage
   (`libgtk-3-0t64`, `libglib2.0-0t64` are Ubuntu noble names).
2. Run locally: `make run` (starts compose MariaDB, then `mvn javafx:run`).
   Check that conversions show up in the history table and survive a restart.
3. Run as a Docker image: `make docker-run` (Linux X server; may need
   `xhost +local:` or an Xauthority mount). On Windows, use Xming with
   DISPLAY=host.docker.internal:0.0 (the image default) and a DB_URL that points
   at a reachable MariaDB.
4. Jenkins: create a Pipeline job from SCM (this repo, branch master, script path
   `week_7/Jenkinsfile`). Jenkins needs Maven, Docker access, the JUnit and
   Coverage plugins, and a `Docker_Hub` username/password credential. Run it
   and fix any failures.
5. Confirm the image appears at https://hub.docker.com/r/mortargoblin/temperature-converter-fx
6. Take screenshots of the app running from the Docker image on the X server.

## Submission
GitHub repo link, Docker Hub image URL, screenshots.
