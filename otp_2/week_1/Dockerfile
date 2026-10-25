# build stage: compile and package the jar
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -q dependency:go-offline
COPY src ./src
RUN mvn -q clean package -DskipTests

# runtime stage: only the jre and the jar
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/temperature-localization.jar app.jar
# utf-8 locale so finnish/swedish/japanese text shows up right
ENV LANG=C.UTF-8 LC_ALL=C.UTF-8
# interactive console app, run with: docker run -it <image>
ENTRYPOINT ["java", "-jar", "app.jar"]
