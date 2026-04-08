# Base image JDK 25
FROM eclipse-temurin:25-jdk

# Tạo thư mục app
WORKDIR /app

# Copy file jar vào container
COPY target/*.jar app.jar

# Expose port (tuỳ app, ví dụ Spring Boot)
EXPOSE 1201

# Chạy ứng dụng
ENTRYPOINT ["java", "-jar", "app.jar"]