# Build stage
FROM maven:3.9.6-eclipse-temurin-8 AS build
WORKDIR /app

# Cache Maven dependencies by copying pom.xml first
COPY pom.xml .
RUN --mount=type=cache,target=/root/.m2 mvn dependency:go-offline -B

# Copy source code and build package
COPY src ./src
RUN --mount=type=cache,target=/root/.m2 mvn clean package

# Run stage
FROM tomcat:9.0-jre8-alpine
WORKDIR /usr/local/tomcat

# Clean default webapps
RUN rm -rf webapps/*

# Copy built WAR file to ROOT.war
COPY --from=build /app/target/legacy-service.war webapps/ROOT.war

EXPOSE 8080
CMD ["catalina.sh", "run"]
