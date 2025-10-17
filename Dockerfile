# Build stage
FROM maven:3.8.4-openjdk-17-slim AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Run stage
FROM openjdk:17-slim
WORKDIR /app

# Install required tools and certificates
RUN apt-get update && \
    apt-get install -y ca-certificates curl iputils-ping net-tools maven && \
    # Download and install Treasury API certificate
    curl -k -o /usr/local/share/ca-certificates/fiscal-treasury.crt --ssl-no-revoke https://api.fiscaldata.treasury.gov/ && \
    update-ca-certificates && \
    rm -rf /var/lib/apt/lists/*

# Copy source files and pom.xml for testing
COPY pom.xml .
COPY src ./src

COPY --from=build /app/target/*.jar app.jar
COPY --from=build /app/src/main/resources/application.properties application.properties

# Create volume mount point for data persistence
VOLUME /app/data

EXPOSE 8080

# Set the startup command with SSL debugging if needed
ENTRYPOINT ["java", \
    "-Djavax.net.debug=ssl,handshake", \
    "-Djdk.tls.client.protocols=TLSv1.2,TLSv1.3", \
    "-jar", "app.jar"]
