# High-Level Architecture Diagram

## E-Commerce Order Processing System

```
┌─────────────────────────────────────────────────────────────────────────────────┐
│                         E-COMMERCE ORDER PROCESSING SYSTEM                       │
└─────────────────────────────────────────────────────────────────────────────────┘

                                    ┌─────────┐
                                    │  USER   │
                                    └────┬────┘
                                         │ HTTP REST
                                         ▼
┌ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ┐
│                              MICROSERVICES LAYER                                 │
│                                                                                  │
│  ┌──────────────────────────────┐       ┌──────────────────────────────┐        │
│  │      ORDER SERVICE           │       │     INVENTORY SERVICE        │        │
│  │        (Port 8080)           │       │        (Port 8081)           │        │
│  │                              │       │                              │        │
│  │  ┌────────────────────────┐  │       │  ┌────────────────────────┐  │        │
│  │  │    OrderController     │  │       │  │  InventoryController   │  │        │
│  │  │    /api/v1/orders      │  │       │  │  /api/v1/products      │  │        │
│  │  └───────────┬────────────┘  │       │  │  /api/v1/inventory     │  │        │
│  │              │               │       │  └───────────┬────────────┘  │        │
│  │              ▼               │       │              │               │        │
│  │  ┌────────────────────────┐  │       │              ▼               │        │
│  │  │    OrderServiceImpl    │  │       │  ┌────────────────────────┐  │        │
│  │  └───────────┬────────────┘  │       │  │  InventoryServiceImpl  │  │        │
│  │              │               │       │  └───────────┬────────────┘  │        │
│  │              ▼               │       │              ▲               │        │
│  │  ┌────────────────────────┐  │       │  ┌──────────┴─────────────┐  │        │
│  │  │  KafkaProducerService  │──┼───┐   │  │  KafkaConsumerService  │  │        │
│  │  └────────────────────────┘  │   │   │  └────────────────────────┘  │        │
│  │              │               │   │   │              ▲               │        │
│  │              ▼               │   │   │              │               │        │
│  │  ┌────────────────────────┐  │   │   │  ┌────────────────────────┐  │        │
│  │  │    OrderRepository     │  │   │   │  │  ProductRepository     │  │        │
│  │  └───────────┬────────────┘  │   │   │  │  InventoryRepository   │  │        │
│  │              │               │   │   │  └───────────┬────────────┘  │        │
│  └──────────────┼───────────────┘   │   └──────────────┼───────────────┘        │
│                 │                   │                  │                         │
└ ─ ─ ─ ─ ─ ─ ─ ─│─ ─ ─ ─ ─ ─ ─ ─ ─ ─│─ ─ ─ ─ ─ ─ ─ ─ ─│─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ┘
                 │                   │                  │
                 │                   │                  │
┌ ─ ─ ─ ─ ─ ─ ─ ─│─ ─ ─ ─ ─ ─ ─ ─ ─ ─│─ ─ ─ ─ ─ ─ ─ ─ ─│─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ┐
│                │      MESSAGE BROKER LAYER           │                          │
│                │                   │                  │                          │
│                │    ┌──────────────▼──────────────┐   │                          │
│                │    │         APACHE KAFKA        │   │                          │
│                │    │                             │   │                          │
│                │    │  Topic: "order-created"     │───┘                          │
│                │    │                             │                              │
│                │    │  ┌───────────────────────┐  │                              │
│                │    │  │  OrderCreatedEvent    │  │                              │
│                │    │  │  - orderId            │  │                              │
│                │    │  │  - orderNumber        │  │                              │
│                │    │  │  - customerName       │  │                              │
│                │    │  │  - orderItems[]       │  │                              │
│                │    │  └───────────────────────┘  │                              │
│                │    └─────────────────────────────┘                              │
│                │                   ▲                                             │
│                │    ┌──────────────┴──────────────┐                              │
│                │    │         ZOOKEEPER           │                              │
│                │    │        (Port 2181)          │                              │
│                │    └─────────────────────────────┘                              │
│                │                                                                 │
└ ─ ─ ─ ─ ─ ─ ─ ─│─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ┘
                 │                                                   │
                 │                                                   │
┌ ─ ─ ─ ─ ─ ─ ─ ─│─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─│─ ─ ─ ─ ─ ─ ┐
│                │            DATA LAYER                             │            │
│                │                                                   │            │
│                ▼                                                   ▼            │
│  ┌─────────────────────────┐                     ┌─────────────────────────┐    │
│  │       PostgreSQL        │                     │       PostgreSQL        │    │
│  │        orderdb          │                     │      inventorydb        │    │
│  │                         │                     │                         │    │
│  │  ┌───────────────────┐  │                     │  ┌───────────────────┐  │    │
│  │  │      orders       │  │                     │  │     products      │  │    │
│  │  ├───────────────────┤  │                     │  ├───────────────────┤  │    │
│  │  │    order_items    │  │                     │  │     inventory     │  │    │
│  │  └───────────────────┘  │                     │  └───────────────────┘  │    │
│  └─────────────────────────┘                     └─────────────────────────┘    │
│                                                                                 │
└ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ┘
```

---

## Data Flow

```
═══════════════════════════════════════════════════════════════════════════════════
                                  DATA FLOW
═══════════════════════════════════════════════════════════════════════════════════

  1. User creates order ──► Order Service ──► Save to orderdb
                                    │
  2.                                └──► Publish OrderCreatedEvent to Kafka
                                                        │
  3.                                                    └──► Inventory Service consumes event
                                                                        │
  4.                                                                    └──► Update stock in inventorydb
```

---

## Layer Summary

| Layer | Components | Purpose |
|-------|------------|---------|
| **API** | REST Controllers | Handle HTTP requests |
| **Business** | Service classes | Business logic |
| **Messaging** | Kafka Producer/Consumer | Async event communication |
| **Data** | JPA Repositories | Database operations |
| **Infrastructure** | PostgreSQL, Kafka, Zookeeper | Containerized via Docker Compose |

---

## Technology Stack

| Component | Technology | Version |
|-----------|------------|---------|
| Language | Java | 21 |
| Framework | Spring Boot | 3.2.0 |
| Database | PostgreSQL | 15 |
| Message Broker | Apache Kafka | 7.5.0 |
| Build Tool | Maven | 3.9+ |
| Containers | Docker Compose | Latest |

---

## Services Overview

### Order Service (Port 8080)
- **Endpoints**: `/api/v1/orders`
- **Database**: `orderdb`
- **Tables**: `orders`, `order_items`
- **Role**: Kafka Producer

### Inventory Service (Port 8081)
- **Endpoints**: `/api/v1/products`, `/api/v1/inventory`
- **Database**: `inventorydb`
- **Tables**: `products`, `inventory`
- **Role**: Kafka Consumer

---

## Event Flow

1. **Order Created** - User submits order via REST API
2. **Order Persisted** - Order saved to PostgreSQL (orderdb)
3. **Event Published** - `OrderCreatedEvent` sent to Kafka topic `order-created`
4. **Event Consumed** - Inventory Service receives event
5. **Stock Updated** - Product quantities reduced in inventorydb
