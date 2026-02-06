# Use official Maven image to build the project
FROM maven:3.9.2-eclipse-temurin-17 AS build

# Set working directory
WORKDIR /app

# Copy project files
COPY pom.xml .
COPY src ./src

# Build the Spring Boot app
RUN mvn clean package -DskipTests

# Use OpenJDK runtime for the final image
FROM eclipse-temurin:17-jdk-jammy

WORKDIR /app

# Copy the built jar from the previous stage
COPY --from=build /app/target/BoatSafariManagement-0.0.1-SNAPSHOT.jar app.jar

# Expose the port Spring Boot will run on
EXPOSE 8080

# Run the jar
ENTRYPOINT ["java", "-jar", "app.jar"]
