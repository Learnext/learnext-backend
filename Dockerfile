# =========================
# Stage 1: Build with Maven
# =========================
FROM eclipse-temurin:25-jdk AS builder

WORKDIR /app

COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn

RUN ./mvnw dependency:go-offline -q

COPY src ./src

RUN ./mvnw clean package -DskipTests


# =========================
# Stage 2: Run the app
# =========================
FROM eclipse-temurin:25-jdk

WORKDIR /app

COPY --from=builder /app/target/*.jar app.jar

EXPOSE 1201

ENTRYPOINT ["java", "-jar", "app.jar"]