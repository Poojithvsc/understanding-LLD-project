# Project Overview - Simplified Guide

## What Is This Project?

This is an **online shopping backend system** - the behind-the-scenes code that powers an e-commerce website.

Think of it like this: When you buy something on Amazon, there's code running that:
1. Takes your order
2. Checks if items are in stock
3. Updates the inventory

This project does exactly that, but split into **two separate mini-applications** that talk to each other.

---

## The Two Services (Think of them as Two Employees)

```
┌─────────────────────┐          ┌─────────────────────┐
│                     │          │                     │
│   ORDER SERVICE     │  ─────►  │  INVENTORY SERVICE  │
│   (The Cashier)     │  message │  (The Warehouse)    │
│                     │          │                     │
└─────────────────────┘          └─────────────────────┘
```

### Order Service (The Cashier)
- Takes customer orders
- Saves order details
- Tells the warehouse "Hey, someone bought these items!"

### Inventory Service (The Warehouse)
- Keeps track of all products
- Knows how many items are in stock
- When notified of an order, reduces the stock count

---

## How They Talk to Each Other

Instead of calling each other directly (like a phone call), they use a **message system** called **Kafka**.

```
ORDER SERVICE                    KAFKA                     INVENTORY SERVICE
     │                             │                              │
     │  "New order for 2 laptops"  │                              │
     │ ──────────────────────────► │                              │
     │                             │  "New order for 2 laptops"   │
     │                             │ ────────────────────────────►│
     │                             │                              │
     │                             │                    (reduces stock by 2)
```

**Why use Kafka instead of direct calls?**
- Order Service doesn't have to wait for Inventory Service
- If Inventory Service is busy/down, the message waits in Kafka
- Like leaving a voicemail instead of waiting on hold

---

## The Database (Where Data Lives)

Each service has its **own database** (like each employee has their own filing cabinet):

```
ORDER SERVICE                          INVENTORY SERVICE
      │                                       │
      ▼                                       ▼
┌─────────────┐                        ┌─────────────┐
│  orderdb    │                        │ inventorydb │
│             │                        │             │
│ - orders    │                        │ - products  │
│ - items     │                        │ - inventory │
└─────────────┘                        └─────────────┘
```

---

## Key Concepts Explained Simply

### 1. Microservices
**What:** Breaking one big application into smaller, independent applications.

**Real-world analogy:** Instead of one person doing everything in a restaurant (cooking, serving, billing), you have a chef, waiter, and cashier - each doing their own job.

**In this project:** Order Service and Inventory Service are two microservices.

---

### 2. REST API
**What:** A way for applications to talk over the internet using simple commands.

**Real-world analogy:** Like a restaurant menu - you ask for specific items using standard requests.

**The commands:**
| Command | What it does | Example |
|---------|--------------|---------|
| GET | Read/fetch data | "Show me all orders" |
| POST | Create new data | "Create a new order" |
| PUT | Update existing data | "Change this order" |
| DELETE | Remove data | "Cancel this order" |

**In this project:**
- `GET /api/v1/orders` → Get all orders
- `POST /api/v1/orders` → Create new order

---

### 3. Kafka (Message Queue)
**What:** A system that holds messages between services.

**Real-world analogy:** A notice board where one person posts a note and another person reads it later.

**In this project:**
- Order Service posts "OrderCreatedEvent" to Kafka
- Inventory Service reads it and updates stock

---

### 4. Spring Boot
**What:** A framework that makes building Java applications easier.

**Real-world analogy:** Like a pre-built house frame - you don't build from scratch, you add rooms to an existing structure.

**What it gives you:**
- Web server (built-in)
- Database connections (easy setup)
- Dependency injection (automatic wiring)

---

### 5. JPA / Hibernate
**What:** A way to save Java objects to a database without writing complex SQL.

**Real-world analogy:** Instead of manually filing papers in specific folders, you hand them to a secretary who knows where everything goes.

**Example:**
```java
// Instead of writing SQL like:
// INSERT INTO orders (customer_name, email) VALUES ('John', 'john@email.com')

// You just do:
Order order = new Order("John", "john@email.com");
repository.save(order);  // JPA handles the SQL
```

---

### 6. Repository Pattern
**What:** A class that handles all database operations for one type of data.

**Real-world analogy:** A librarian who only handles books - you ask them to find, add, or remove books.

**In this project:**
- `OrderRepository` - handles all order database operations
- `ProductRepository` - handles all product database operations

---

### 7. DTO (Data Transfer Object)
**What:** A simple object used to send/receive data through the API.

**Real-world analogy:** A shipping box - you don't ship your entire house, you pack specific items in a box.

**In this project:**
- `OrderRequest` - What user sends when creating order
- `OrderResponse` - What user receives back

---

### 8. Docker
**What:** A tool that packages applications so they run the same everywhere.

**Real-world analogy:** A shipping container - put your stuff in it, and it works the same whether shipped by truck, train, or boat.

**In this project:** Docker runs:
- PostgreSQL database
- Kafka message broker
- Zookeeper (Kafka's helper)

---

## The Complete Flow (Step by Step)

```
CUSTOMER PLACES ORDER
        │
        ▼
┌───────────────────────────────────────────────────────────────┐
│ STEP 1: User sends order data                                 │
│         POST /api/v1/orders                                   │
│         { "customerName": "John", "items": [...] }            │
└───────────────────────────────────────────────────────────────┘
        │
        ▼
┌───────────────────────────────────────────────────────────────┐
│ STEP 2: Order Service receives request                        │
│         - Validates the data                                  │
│         - Calculates total price                              │
│         - Generates order number (ORD-20251217-123456-AB12)   │
└───────────────────────────────────────────────────────────────┘
        │
        ▼
┌───────────────────────────────────────────────────────────────┐
│ STEP 3: Order saved to database                               │
│         - Order details stored in 'orders' table              │
│         - Order items stored in 'order_items' table           │
└───────────────────────────────────────────────────────────────┘
        │
        ▼
┌───────────────────────────────────────────────────────────────┐
│ STEP 4: Event sent to Kafka                                   │
│         "Hey! New order created with these items!"            │
│         Topic: "order-created"                                │
└───────────────────────────────────────────────────────────────┘
        │
        ▼
┌───────────────────────────────────────────────────────────────┐
│ STEP 5: Inventory Service receives event                      │
│         - Reads the order details                             │
│         - Finds each product in database                      │
│         - Reduces stock quantity                              │
└───────────────────────────────────────────────────────────────┘
        │
        ▼
┌───────────────────────────────────────────────────────────────┐
│ STEP 6: Stock updated in inventory database                   │
│         Product "Laptop" stock: 10 → 8 (customer bought 2)    │
└───────────────────────────────────────────────────────────────┘
```

---

## Project Structure (Simplified)

```
project/
│
├── services/
│   │
│   ├── order-service/           ← CASHIER APP
│   │   ├── controller/          ← Receives web requests
│   │   ├── service/             ← Business logic (calculations, rules)
│   │   ├── repository/          ← Talks to database
│   │   ├── model/               ← Data structures (Order, OrderItem)
│   │   └── dto/                 ← Input/Output formats
│   │
│   └── inventory-service/       ← WAREHOUSE APP
│       ├── controller/          ← Receives web requests
│       ├── service/             ← Business logic
│       ├── repository/          ← Talks to database
│       ├── model/               ← Data structures (Product, Inventory)
│       └── dto/                 ← Input/Output formats
│
└── docker-compose.yml           ← Starts databases and Kafka
```

---

## Layer Architecture (Each Service)

```
        HTTP Request comes in
              │
              ▼
    ┌─────────────────┐
    │   CONTROLLER    │  ← Receives request, returns response
    │   (Receptionist)│
    └────────┬────────┘
             │
             ▼
    ┌─────────────────┐
    │    SERVICE      │  ← Does the actual work (business logic)
    │    (Manager)    │
    └────────┬────────┘
             │
             ▼
    ┌─────────────────┐
    │   REPOSITORY    │  ← Saves/retrieves from database
    │   (File Clerk)  │
    └────────┬────────┘
             │
             ▼
    ┌─────────────────┐
    │    DATABASE     │  ← Stores all the data
    │  (Filing Cabinet)│
    └─────────────────┘
```

---

## Why This Architecture?

| Benefit | Explanation |
|---------|-------------|
| **Scalability** | If orders increase, add more Order Service instances |
| **Independence** | Can update Inventory Service without touching Order Service |
| **Resilience** | If one service crashes, others keep working |
| **Technology Freedom** | Could rewrite Inventory Service in Python if needed |
| **Team Organization** | Different teams can own different services |

---

## Summary

This project demonstrates how modern e-commerce backends work:

1. **Split into small services** (Microservices)
2. **Each service has its own database** (Data isolation)
3. **Services communicate via messages** (Event-driven with Kafka)
4. **REST APIs for external access** (HTTP endpoints)
5. **Layered code organization** (Controller → Service → Repository)
6. **Containerized infrastructure** (Docker)

These are the same patterns used by Netflix, Uber, Amazon, and other large-scale applications!
