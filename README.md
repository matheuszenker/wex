# WEX Transaction Service

A Spring Boot application that handles purchase transactions and currency conversion using the Treasury Reporting Rates of Exchange API.

## Prerequisites

- Java 17 or higher
- Maven 3.6 or higher
- Docker (optional)

## Quick Start

### Using Docker

1. Build and run using Docker Compose:
   ```bash
   docker-compose up --build
   ```

The application will be available at `http://localhost:8080`

Note: The Docker build includes the Maven build process, so you don't need to run Maven commands separately when using Docker.
