# Project Overview - Simplified Guide

## What Is This Project?

This is an **online shopping backend system** - the code that powers an e-commerce website.

When you shop online:
1. Someone adds products to the catalog (Inventory Service)
2. You place an order (Order Service)
3. The stock automatically decreases (Kafka magic!)

---

## The Two Services

```
+------------------------+          +------------------------+
|                        |          |                        |
|   INVENTORY SERVICE    |          |    ORDER SERVICE       |
|   (The Warehouse)      |          |    (The Cashier)       |
|                        |          |                        |
|   - Add products       |          |   - Place orders       |
|   - View stock         |          |   - View orders        |
|   - Update prices      |          |   - Cancel orders      |
|                        |          |                        |
+------------------------+          +------------------------+
          |                                    |
          |        +--------------+            |
          +------->|    KAFKA     |<-----------+
                   |  (Messenger) |
                   +--------------+
                   "When order placed,
                    auto-update stock"
```

### Inventory Service (Port 8081)
- Add/edit/delete products
- Check stock levels
- Set prices

### Order Service (Port 8080)
- Create orders
- View order history
- Track order status

---

## How They Connect

Instead of Order Service calling Inventory Service directly, they use **Kafka** (a message system).

```
1. User places order
          |
          v
    ORDER SERVICE
    "Save order to database"
    "Send message to Kafka: New order!"
          |
          v
        KAFKA
    "Holds the message"
          |
          v
    INVENTORY SERVICE
    "Oh! New order. Let me reduce stock."
```

**Why Kafka?**
- Order Service doesn't wait for Inventory
- If Inventory is busy, message waits in Kafka
- Like leaving a voicemail instead of waiting on hold

---

## The Databases

Each service has its own database:

```
+-------------------+          +-------------------+
|    inventorydb    |          |     orderdb       |
|                   |          |                   |
| products table    |          | orders table      |
| inventory table   |          | order_items table |
+-------------------+          +-------------------+
```

---

## Key Concepts Explained Simply

### 1. Microservices
**What:** Small, independent applications working together.
**Analogy:** A restaurant with separate chef, waiter, and cashier instead of one person doing everything.

### 2. REST API
**What:** A way for apps to talk over the internet.
**Commands:**
- GET = Read data
- POST = Create data
- PUT = Update data
- DELETE = Remove data

### 3. Kafka
**What:** A message delivery system between services.
**Analogy:** A notice board - one person posts, another reads later.

### 4. Spring Boot
**What:** A framework that makes Java apps easier to build.
**Analogy:** A pre-built house frame - you add rooms, not build from scratch.

### 5. JPA/Hibernate
**What:** Saves Java objects to database without writing SQL.
**Instead of:** `INSERT INTO products VALUES (...)`
**You write:** `repository.save(product)`

---

## Project Structure

```
project/
|
+-- services/
|   |
|   +-- order-service/        <-- CASHIER
|   |   +-- controller/       <-- Receives requests
|   |   +-- service/          <-- Business logic
|   |   +-- repository/       <-- Database access
|   |   +-- model/            <-- Data structures
|   |
|   +-- inventory-service/    <-- WAREHOUSE
|       +-- controller/
|       +-- service/
|       +-- repository/
|       +-- model/
|
+-- docker-compose.yml        <-- Starts databases & Kafka
```

---

## The Complete Flow

```
STEP 1: Add products (one-time setup)
        POST /api/v1/products to Inventory Service
        "Add Laptop, price $999, stock 10"
                    |
                    v
        Product saved to inventorydb

STEP 2: Place order
        POST /api/v1/orders to Order Service
        "Customer wants 2 Laptops"
                    |
                    v
        Order saved to orderdb
                    |
                    v
        Event sent to Kafka: "Order placed!"
                    |
                    v
        Inventory receives event
                    |
                    v
        Stock reduced: 10 -> 8
```

---

## Layer Architecture

```
        Request comes in
              |
              v
    +------------------+
    |   CONTROLLER     |  <-- Receives request
    +--------+---------+
             |
             v
    +------------------+
    |    SERVICE       |  <-- Business logic
    +--------+---------+
             |
             v
    +------------------+
    |   REPOSITORY     |  <-- Database access
    +--------+---------+
             |
             v
    +------------------+
    |    DATABASE      |  <-- Stores data
    +------------------+
```

---

## Why This Architecture?

| Benefit | Explanation |
|---------|-------------|
| Scalability | Add more instances if traffic increases |
| Independence | Update one service without touching others |
| Resilience | If one crashes, others keep working |
| Team Organization | Different teams can own different services |

---

## Summary

This project demonstrates:

1. **Microservices** - Split into small services
2. **Data isolation** - Each service has its own database
3. **Event-driven** - Kafka connects services
4. **REST APIs** - HTTP endpoints for access
5. **Layered code** - Controller -> Service -> Repository
6. **Containerized** - Docker runs infrastructure

These are the same patterns used by Netflix, Uber, Amazon!
