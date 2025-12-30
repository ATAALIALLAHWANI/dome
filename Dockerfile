# Use OpenJDK 17
FROM eclipse-temurin:17-jdk-alpine

# Working directory inside container
WORKDIR /app

# Copy the Spring Boot JAR into the container
COPY target/demo-0.0.1-SNAPSHOT.jar app.jar

# Expose port
EXPOSE 8080

# Run the jar
ENTRYPOINT ["java", "-jar", "app.jar"]
