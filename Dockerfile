# Stage 1: Build the application
FROM maven:3.8.5-openjdk-17 AS build

# Set the working directory
WORKDIR /app

# Copy the pom.xml and download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy the source code and build the application
COPY src ./src
COPY config/config.yml ./config/config.yml

RUN mvn package -DskipTests

# Stage 2: Create the runtime image
FROM openjdk:17-jdk-slim


# Set the working directory
WORKDIR /app



# Copy the built jar file from the build stage
COPY --from=build /app/target/taxi-fleet-1.0-SNAPSHOT.jar app.jar
COPY config/config.yml ./config/config.yml

# Expose the application port (if applicable)
EXPOSE 8080

# Set the entry point to run the application
CMD java -jar app.jar server config/config.yml