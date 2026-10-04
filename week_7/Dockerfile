# build stage: compile and package the fat jar
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -q dependency:go-offline
COPY src ./src
RUN mvn -q clean package -DskipTests

# runtime stage: jre plus the native libraries javafx needs to open a window on an x server
FROM eclipse-temurin:21-jre
RUN apt-get update && apt-get install -y --no-install-recommends \
      libgtk-3-0t64 libxtst6 libxxf86vm1 libgl1 libglib2.0-0t64 fonts-dejavu-core \
    && rm -rf /var/lib/apt/lists/*
WORKDIR /app
COPY --from=build /app/target/temperature-converter-fx.jar app.jar
# point display at your x server (xming on windows: host.docker.internal:0.0)
ENV DISPLAY=host.docker.internal:0.0
ENV DB_URL=jdbc:mariadb://host.docker.internal:3306/temperature_db
ENTRYPOINT ["java", "-Dprism.order=sw", "-jar", "app.jar"]
