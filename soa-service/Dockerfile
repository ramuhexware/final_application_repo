# Build stage
FROM maven:3.9.6-eclipse-temurin-8 AS build
WORKDIR /app

# Cache Maven dependencies by copying pom.xml first
COPY pom.xml .
RUN --mount=type=cache,target=/root/.m2 mvn dependency:go-offline -B

# Copy source code and build package
COPY src ./src
RUN --mount=type=cache,target=/root/.m2 mvn clean package -DskipTests

# Run stage
FROM eclipse-temurin:8-jre-alpine
WORKDIR /app

COPY --from=build /app/target/soa-service-0.0.1-SNAPSHOT.jar soa-service.jar

EXPOSE 8087
ENTRYPOINT ["java", "-jar", "soa-service.jar"]
