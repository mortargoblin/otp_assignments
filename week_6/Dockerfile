# Build stage: compile and package the jar
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -q dependency:go-offline
COPY src ./src
RUN mvn -q clean package -DskipTests

# Runtime stage: only the JRE and the jar
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/temperature-converter.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
