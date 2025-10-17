# AI Assistant Instructions for WEX Project

This is a containerized project that uses Docker for deployment and running services. Here are the key aspects to understand:

## Project Structure

- `docker-compose.yml` - Defines the multi-container Docker application services and their relationships
- `Dockerfile` - Contains instructions for building the main application container image

## Development Workflow

### Container Management

- Use Docker Compose commands for managing the application:
  ```
  docker-compose up    # Start the services
  docker-compose down  # Stop and remove containers
  docker-compose build # Rebuild containers
  ```

### Development Environment

- The project is containerized to ensure consistent development environments
- All dependencies and runtime requirements are defined in Docker configurations

## Key Files

- `docker-compose.yml`: Service definitions, volumes, networks, and environment configurations
- `Dockerfile`: Application build process, base image, dependencies, and runtime setup

## Project Conventions

- Container-first development approach
- Docker Compose for service orchestration
- Environment variables for configuration

## Note to AI Assistant

- When modifying service configurations, always update both `docker-compose.yml` and `Dockerfile` as needed
- Consider container networking and data persistence when making architectural changes
- Ensure any new services are properly integrated into the Docker Compose setup

## Project Requirements

Your task is to build an application that supports the requirements outlined below. Outside of the requirements outlined, as well as any language limitations specified by the technical implementation notes below and/or by the hiring manager(s), the application is your own design from a technical perspective. This is your opportunity to show us what you know! Have fun, explore new ideas, and as noted in the Questions section below, please let us know if you have any questions regarding the requirements!

## Requirements

### Requirement 1: Store a Purchase Transaction

Your application must be able to accept and store (i.e., persist) a purchase transaction with a description, transaction date, and a purchase amount in United States dollars. When the transaction is stored, it will be assigned a unique identifier.

Field requirements
● Description: must not exceed 50 characters
● Transaction date: must be a valid date format
● Purchase amount: must be a valid positive amount rounded to the nearest cent
● Unique identifier: must uniquely identify the purchase

### Requirement 2: Retrieve a Purchase Transaction in a Specified Country’s Currency

Based upon purchase transactions previously submitted and stored, your application must provide a way to retrieve the stored purchase transactions converted to currencies supported by the Treasury Reporting Rates of Exchange API based
upon the exchange rate active for the date of the purchase.
https://fiscaldata.treasury.gov/datasets/treasury-reporting-rates-exchange/treasury-reporting-rates-of-exchange
The retrieved purchase should include the identifier, the description, the transaction date, the original US dollar purchase amount, the exchange rate used, and the converted amount based upon the specified currency’s exchange rate for the date of the purchase.

## Currency conversion requirements

● When converting between currencies, you do not need an exact date match, but must use a currency conversion rate less than or equal to the purchase date from within the last 6 months.
● If no currency conversion rate is available within 6 months equal to or before the purchase date, an error should be returned stating the purchase cannot be converted to the target currency.
● The converted purchase amount to the target currency should be rounded to two decimal places (i.e., cent).

## Technical Implementation

The technical implementation, including frameworks, libraries, etc. is your own design except for the language. The solution should be implemented in either C# or Java. If you want to use a JVM-based language other than Java, please
gain permission to do so in advance of implementing the solution. You should build this application as if you are building an application to be deployed in a Production environment. This should be interpreted to mean that all functional automated testing you would include for a Production application should be expected. Please note that non-functional test (e.g., performance testing) automation is not needed.
Your application repository should be fully functional without installing separate databases, web servers, or servlet containers (e.g., Jetty, Tomcat, etc).

## Other Notes

This application needs to run even without Docker, but Docker is preferred. If you choose to use Docker, please include a Dockerfile and any other files needed to build and run the application in a Docker container. Please include instructions on how to build and run the application in your README file.

Don't create extra dependencies that require additional installation steps. For example, if you use a database, it should be an embedded database that does not require separate installation and configuration. If you use a build tool, it should be a common build tool such as Maven or Gradle for Java or .NET CLI or MSBuild for C#. If you use a web framework, it should be a common web framework such as Spring Boot for Java or ASP.NET Core for C#.
