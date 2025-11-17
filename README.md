# Order Processing System (OPS)

Order Processing System (OPS) is a Spring Boot application for managing e-commerce orders. It supports creating orders, fetching order details, listing all orders, and canceling pending orders. The system also includes a scheduler to process pending orders automatically and a retry mechanism for failed batches. All table changes are tracked using audit logs.

---

## APIs

### 1. Create Order

**Request:**

```bash

curl --location 'http://localhost:8080/api/ecommerce/order' \
--header 'user-id: 123' \
--header 'Content-Type: application/json' \
--data '[
  {
    "productId": 123,
    "quantity": 12,
    "unitPrice": 10.5
  },
  {
    "productId": 346,
    "quantity": 4,
    "unitPrice": 15
  }
]'
```

**Response:**

```json
{
    "orderId": 8,
    "userId": 123,
    "orderStatus": "PENDING"
}
```

---

### 2. Get Order by Order ID

**Request:**

```bash

curl --location --request GET 'http://localhost:8080/api/ecommerce/order/6' \
--header 'user-id: 123' \
--header 'Content-Type: text/plain'
```

**Response:**

```json
{
    "orderId": 6,
    "userId": 123,
    "orderStatus": "PENDING",
    "orderItemDtoList": [
        {
            "orderItemId": 7,
            "productId": 123,
            "quantity": 12,
            "unitPrice": 10.5
        },
        {
            "orderItemId": 8,
            "productId": 346,
            "quantity": 4,
            "unitPrice": 15.0
        }
    ]
}
```

---

### 3. Get All Orders

**Request:**

```bash

curl --location --request GET 'http://localhost:8080/api/ecommerce/order' \
--header 'user-id: 123' \
--header 'Content-Type: text/plain'
```

**Response:**

```json
[
    {
        "orderId": 6,
        "userId": 123,
        "orderStatus": "PROCESSING",
        "orderItemDtoList": [
            {
                "orderItemId": 7,
                "productId": 123,
                "quantity": 12,
                "unitPrice": 10.5
            },
            {
                "orderItemId": 8,
                "productId": 346,
                "quantity": 4,
                "unitPrice": 15.0
            }
        ]
    },
    {
        "orderId": 7,
        "userId": 123,
        "orderStatus": "PROCESSING",
        "orderItemDtoList": [
            {
                "orderItemId": 9,
                "productId": 123,
                "quantity": 12,
                "unitPrice": 10.5
            },
            {
                "orderItemId": 10,
                "productId": 346,
                "quantity": 4,
                "unitPrice": 15.0
            }
        ]
    },
    {
        "orderId": 8,
        "userId": 123,
        "orderStatus": "PROCESSING",
        "orderItemDtoList": [
            {
                "orderItemId": 11,
                "productId": 123,
                "quantity": 12,
                "unitPrice": 10.5
            },
            {
                "orderItemId": 12,
                "productId": 346,
                "quantity": 4,
                "unitPrice": 15.0
            }
        ]
    }
]
```

---

### 4. Cancel Pending Order

**Request:**

```bash

curl --location --request PUT 'http://localhost:8080/api/ecommerce/order/6' \
--header 'user-id: 123' \
--header 'Content-Type: text/plain' \
--data '{
    "orderItemDtoList": [
        {
            "productId": 123,
            "quantity": 12,
            "unitPrice": 10.5
        }
    ]
}'
```

**Response:**

```json
{
    "orderId": 6,
    "userId": 123,
    "orderStatus": "CANCELED",
    "message": "Order is cancelled successfully"
}
```

---

## Scheduler

* A scheduler runs periodically to move all **pending orders** to **processing status**.
* It uses batch processing with a configurable batch size.
* **Retry mechanism** is in place to retry failed orders from the previous run.
* Each batch run is logged in `cron_job_logs` and failed batches are tracked in `cron_job_batch_retry`.

---

## Audit Logs

* Audit logs are maintained for all table changes .
* Each change stores **previous data** and **current data** as JSON in the `audit_logs` table.

---

## Database Tables

1. **orders** – Stores order details.
2. **order_items** – Stores individual items for each order.
3. **cron_job_logs** – Tracks batch job runs.
4. **audit_logs** – Tracks changes in all tables.
5. **cron_job_batch_retry** – Tracks failed batch retries.

---

## Notes

* Ensure the application connects to a MySQL database.
* `data.sql` and `schema.sql` are available to initialize the database with sample data and schema.
* Scheduler batch size and retry thresholds can be configured in `application.properties`.
