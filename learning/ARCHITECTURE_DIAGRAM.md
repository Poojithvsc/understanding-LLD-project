# High-Level Architecture Diagram

## E-Commerce Order Processing System

```
                                    +---------+
                                    |  USER   |
                                    +----+----+
                                         |
                    +--------------------+--------------------+
                    |                                         |
                    | Manage Products                         | Place Order
                    | (REST API)                              | (REST API)
                    v                                         v
+-------------------------------+       +-------------------------------+
|     INVENTORY SERVICE         |       |      ORDER SERVICE            |
|        (Port 8081)            |       |        (Port 8080)            |
|                               |       |                               |
|  +-------------------------+  |       |  +-------------------------+  |
|  |  InventoryController    |  |       |  |    OrderController      |  |
|  |  /api/v1/products       |  |       |  |    /api/v1/orders       |  |
|  |  /api/v1/inventory      |  |       |  +------------+------------+  |
|  +------------+------------+  |       |              |                |
|               |               |       |              v                |
|               v               |       |  +-------------------------+  |
|  +-------------------------+  |       |  |    OrderServiceImpl     |  |
|  |  InventoryServiceImpl   |<-+-------+--+-------------------------+  |
|  +-------------------------+  |       |              |                |
|               |               |  Kafka|              v                |
|               v               |       |  +-------------------------+  |
|  +-------------------------+  |       |  |  KafkaProducerService   |  |
|  |  ProductRepository      |  |       |  +------------+------------+  |
|  |  InventoryRepository    |  |       |              |                |
|  +------------+------------+  |       |              v                |
|               |               |       |  +-------------------------+  |
+---------------+---------------+       |  |    OrderRepository      |  |
                |                       |  +------------+------------+  |
                v                       +---------------+---------------+
+-------------------------------+                       |
|       PostgreSQL              |                       v
|      inventorydb              |       +-------------------------------+
|                               |       |       PostgreSQL              |
|  Tables:                      |       |        orderdb                |
|  - products                   |       |                               |
|  - inventory                  |       |  Tables:                      |
+-------------------------------+       |  - orders                     |
                                        |  - order_items                |
                                        +-------------------------------+

                         +-------------------------------+
                         |         APACHE KAFKA          |
                         |                               |
                         |    Topic: order-created       |
                         |                               |
                         |  Order Service PUBLISHES -->  |
                         |  --> Inventory CONSUMES       |
                         |  (Auto stock update)          |
                         +-------------------------------+
```

---

## Two Ways Users Interact

| Action | Service | What Happens |
|--------|---------|--------------|
| **Add/View Products** | Inventory Service | Direct REST API calls |
| **Place Order** | Order Service | Saves order + triggers Kafka event |

---

## The Kafka Connection

When an order is placed:
1. Order Service saves the order to `orderdb`
2. Order Service publishes `OrderCreatedEvent` to Kafka
3. Inventory Service (listening) receives the event
4. Inventory Service automatically reduces stock in `inventorydb`

---

## Technology Stack

| Component | Technology |
|-----------|------------|
| Language | Java 21 |
| Framework | Spring Boot 3.2.0 |
| Database | PostgreSQL 15 |
| Message Broker | Apache Kafka 7.5.0 |
| Build Tool | Maven |
| Containers | Docker Compose |

---

## Services Overview

### Order Service (Port 8080)
- **Purpose**: Handle customer orders
- **Endpoints**: `/api/v1/orders`
- **Database**: `orderdb`
- **Kafka Role**: Producer (publishes events)

### Inventory Service (Port 8081)
- **Purpose**: Manage products and stock
- **Endpoints**: `/api/v1/products`, `/api/v1/inventory`
- **Database**: `inventorydb`
- **Kafka Role**: Consumer (auto-updates stock)
