# WEX Transaction Service

A service for storing purchase transactions and converting their amounts to different currencies using exchange rates from the U.S. Treasury Reporting Rates of Exchange API.

## API Endpoints

### Store Purchase Transaction

Store a new purchase transaction with description, date, and amount in USD.

```http
POST /api/purchases
```

#### Request Body

```json
{
  "description": "Sample Purchase",
  "transactionDate": "2025-10-16",
  "amount": 200.5
}
```

#### Validation Rules

- Description: Maximum 50 characters
- Transaction Date: Must be a valid date
- Amount: Must be a positive number, rounded to 2 decimal places

### Convert Purchase to Different Currency

Retrieve a stored purchase with its amount converted to a specified currency.

```http
GET /api/purchases/{id}/convert?currency={currencyCode}
```

#### Parameters

- `id`: Purchase transaction ID (UUID)
- `currency`: Target currency code (See supported currencies below)

#### Example Response

```json
{
  "id": "ab281124-53e6-4481-a03e-8672abcb6398",
  "description": "Sample Purchase",
  "transactionDate": "2025-10-15",
  "originalAmount": 100.5,
  "convertedAmount": 139.9,
  "targetCurrency": "CAD",
  "exchangeRate": 1.392,
  "exchangeRateDate": "2025-09-30"
}
```

## Supported Currencies

The following currencies are supported for conversion:

| Currency Code | Description          |
| ------------- | -------------------- |
| EUR           | European Euro        |
| GBP           | United Kingdom Pound |
| CAD           | Canada Dollar        |
| JPY           | Japan Yen            |

For review and test pourposes will not add all currencies.

## Currency Conversion Rules

- Exchange rates are fetched from the U.S. Treasury Reporting Rates of Exchange API
- The exchange rate used must be from a date less than or equal to the purchase date
- The exchange rate must not be older than 6 months from the purchase date
- If no valid exchange rate is found, an error will be returned

## Error Responses

The API may return the following errors:

- 400 Bad Request: Invalid input data
- 404 Not Found: Purchase transaction not found
- 422 Unprocessable Entity: No valid exchange rate found within 6 months
- 500 Internal Server Error: Unexpected server error

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

### Running test suit

```bash
docker compose exec wex-api mvn test
```
