# ---- Stage 1: build the jar ----
# Uses a Maven image so we don't need Maven installed locally.
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
# Copy pom first and cache dependencies (faster rebuilds).
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn clean package -DskipTests -B

# ---- Stage 2: run the jar on a slim JRE ----
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
