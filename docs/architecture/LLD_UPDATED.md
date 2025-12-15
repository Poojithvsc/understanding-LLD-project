# Low-Level Design Document (LLD)
## E-Commerce Order Processing System - Order Service

---

### Document Information

| Attribute | Details |
|-----------|---------|
| **Project Name** | E-Commerce Order Processing System |
| **Module** | Order Service (Microservice) |
| **Version** | 1.0.0 |
| **Author** | Poojith |
| **Last Updated** | December 15, 2025 |
| **Status** | Active Development - Foundation Complete (40%) |
| **Reviewer** | [Pending] |

---

### Implementation Status Tracker

**Overall Implementation Progress:** 40% Complete

| Component | Specification Status | Implementation Status | Completion % |
|-----------|---------------------|----------------------|--------------|
| **Order Service Foundation** | ✅ Documented | ✅ Implemented | 100% |
| - Entity Layer | ✅ Documented | ✅ Implemented | 100% |
| - Repository Layer | ✅ Documented | ✅ Implemented | 100% |
| - Service Layer | ✅ Documented | ✅ Implemented | 100% |
| - Controller Layer | ✅ Documented | ✅ Implemented | 100% |
| - DTOs | ✅ Documented | ✅ Implemented | 100% |
| **Database** | ✅ Documented | 🔄 Partially Implemented | 50% |
| - Local PostgreSQL | ✅ Documented | ✅ Implemented | 100% |
| - AWS RDS PostgreSQL | ✅ Documented | 📋 Pending | 0% |
| **AWS S3 Integration** | ✅ Documented | 📋 Pending | 0% |
| **Unit Tests** | ✅ Documented | 📋 Pending | 0% |
| **Inventory Service** | ✅ Documented | 📋 Pending | 0% |
| **Kafka Integration** | ✅ Documented | 📋 Pending | 0% |
| **Exception Handling** | ✅ Documented | 📋 Pending | 0% |
| **Security (Future)** | ✅ Documented | 📋 Not Started | 0% |
| **Monitoring (Future)** | ✅ Documented | 📋 Not Started | 0% |

**How to Read This Tracker:**
- ✅ **Implemented**: Code written, tested, and working
- 🔄 **Partially Implemented**: Work in progress
- 📋 **Pending**: Specified but not yet implemented
- ❌ **Blocked**: Implementation blocked by dependencies

**Update this table after each phase completion!**

---

### Document History

| Version | Date | Author | Changes |
|---------|------|--------|---------|
| 0.1 | Dec 9, 2024 | Poojith | Initial draft with H2 database |
| 1.0 | Dec 15, 2025 | Poojith | Updated with PostgreSQL, actual implementation |
| 1.1 | Dec 15, 2025 | Poojith | Added implementation status tracker |

---

## Table of Contents

1. [System Overview](#1-system-overview)
2. [Technology Stack](#2-technology-stack)
3. [Architecture & Design Patterns](#3-architecture--design-patterns)
4. [Database Design](#4-database-design)
5. [API Specifications](#5-api-specifications)
6. [Class Design](#6-class-design)
7. [Sequence Diagrams](#7-sequence-diagrams)
8. [Component Interactions](#8-component-interactions)
9. [Error Handling](#9-error-handling)
10. [Security Considerations](#10-security-considerations)
11. [Performance Considerations](#11-performance-considerations)
12. [Testing Strategy](#12-testing-strategy)
13. [Deployment Architecture](#13-deployment-architecture)
14. [Future Enhancements](#14-future-enhancements)

---

## 1. System Overview

### 1.1 Purpose
The Order Service is a microservice responsible for managing customer orders in an e-commerce system. It provides RESTful APIs for creating, reading, updating, and deleting orders with persistent storage in PostgreSQL.

### 1.2 Scope
- **In Scope:**
  - Order creation, retrieval, update, and deletion (CRUD operations)
  - Order item management
  - Order status tracking
  - Data persistence in PostgreSQL
  - RESTful API endpoints
  - Data validation

- **Out of Scope (Future Phases):**
  - Payment processing
  - Inventory management
  - User authentication/authorization
  - Event-driven communication (Kafka)
  - Caching layer (Redis)

### 1.3 Assumptions
- Client applications will send valid JSON payloads
- Database is always available
- Single deployment instance (no load balancing yet)
- No concurrent order modifications (optimistic locking present but not enforced)

---

## 2. Technology Stack

### 2.1 Backend Framework
| Component | Technology | Version | Purpose |
|-----------|-----------|---------|---------|
| **Language** | Java | 24 | Primary programming language |
| **Framework** | Spring Boot | 3.2.0 | Application framework |
| **Web** | Spring Web MVC | 6.1.1 | REST API framework |
| **ORM** | Hibernate (JPA) | 6.3.1 | Object-Relational Mapping |
| **Validation** | Jakarta Validation | 3.0.2 | Input validation |
| **Build Tool** | Maven | 3.9.11 | Dependency & build management |

### 2.2 Database
| Component | Technology | Version | Purpose |
|-----------|-----------|---------|---------|
| **RDBMS** | PostgreSQL | 15 | Primary data store |
| **Driver** | PostgreSQL JDBC | 42.7.1 | Database connectivity |
| **Connection Pool** | HikariCP | 5.1.0 | Connection pooling |

### 2.3 Infrastructure
| Component | Technology | Version | Purpose |
|-----------|-----------|---------|---------|
| **Containerization** | Docker | 24+ | Database containerization |
| **Server** | Apache Tomcat (Embedded) | 10.1.16 | Application server |

---

## 3. Architecture & Design Patterns

### 3.1 Architectural Style
**Layered Architecture** (MVC Pattern)

```
┌──────────────────────────────────────────────────┐
│                 CLIENT LAYER                      │
│         (Browser, Postman, Mobile App)            │
└────────────────────┬─────────────────────────────┘
                     │ HTTP/JSON
                     ↓
┌──────────────────────────────────────────────────┐
│              CONTROLLER LAYER                     │
│   - OrderController                               │
│   - Handles HTTP requests/responses               │
│   - Request validation                            │
│   - Response formatting                           │
└────────────────────┬─────────────────────────────┘
                     │ DTOs
                     ↓
┌──────────────────────────────────────────────────┐
│               SERVICE LAYER                       │
│   - OrderService (Interface)                      │
│   - OrderServiceImpl (Implementation)             │
│   - Business logic                                │
│   - Data transformation                           │
│   - Transaction management                        │
└────────────────────┬─────────────────────────────┘
                     │ Entities
                     ↓
┌──────────────────────────────────────────────────┐
│             REPOSITORY LAYER                      │
│   - OrderRepository (Spring Data JPA)             │
│   - Database CRUD operations                      │
│   - Custom query methods                          │
└────────────────────┬─────────────────────────────┘
                     │ SQL/JDBC
                     ↓
┌──────────────────────────────────────────────────┐
│              DATABASE LAYER                       │
│        PostgreSQL (Docker Container)              │
│   - orders table                                  │
│   - order_items table                             │
└──────────────────────────────────────────────────┘
```

### 3.2 Design Patterns Implemented

#### 3.2.1 Repository Pattern
**Purpose:** Abstract database access logic
```java
public interface OrderRepository extends JpaRepository<Order, UUID> {
    Optional<Order> findByOrderNumber(String orderNumber);
    List<Order> findByEmail(String email);
}
```

#### 3.2.2 Service Layer Pattern
**Purpose:** Encapsulate business logic
```java
public interface OrderService {
    OrderResponse createOrder(OrderRequest request);
    OrderResponse getOrderById(UUID orderId);
    // ... other methods
}
```

#### 3.2.3 DTO Pattern
**Purpose:** Decouple API contracts from domain entities
- `OrderRequest` - Client input
- `OrderResponse` - Server output
- `OrderItemDto` - Item data transfer

#### 3.2.4 Dependency Injection
**Purpose:** Loose coupling, testability
```java
@RestController
public class OrderController {
    private final OrderService orderService;

    // Constructor injection
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }
}
```

---

## 4. Database Design

### 4.1 Entity Relationship Diagram (ERD)

```
┌─────────────────────────────────┐
│          ORDERS                  │
├─────────────────────────────────┤
│ PK  order_id (UUID)              │
│ UK  order_number (VARCHAR)       │
│     customer_name (VARCHAR)      │
│     email (VARCHAR)              │
│     total_amount (DECIMAL)       │
│     status (VARCHAR)             │
│     created_at (TIMESTAMP)       │
│     updated_at (TIMESTAMP)       │
│     version (BIGINT)             │
└──────────────┬──────────────────┘
               │ 1
               │
               │ has many
               │
               │ N
┌──────────────┴──────────────────┐
│       ORDER_ITEMS                │
├─────────────────────────────────┤
│ PK  item_id (UUID)               │
│ FK  order_id (UUID)              │
│     product_id (UUID)            │
│     product_name (VARCHAR)       │
│     quantity (INTEGER)           │
│     price (DECIMAL)              │
└─────────────────────────────────┘
```

### 4.2 Table Specifications

#### 4.2.1 orders Table

```sql
CREATE TABLE orders (
    order_id UUID PRIMARY KEY,
    order_number VARCHAR(50) UNIQUE NOT NULL,
    customer_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    total_amount DECIMAL(10, 2) NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP(6),
    updated_at TIMESTAMP(6),
    version BIGINT,

    CONSTRAINT chk_status CHECK (
        status IN ('PENDING', 'CONFIRMED', 'PROCESSING', 'SHIPPED', 'DELIVERED', 'CANCELLED')
    )
);

-- Index for performance
CREATE INDEX idx_orders_email ON orders(email);
CREATE INDEX idx_orders_status ON orders(status);
CREATE INDEX idx_orders_created_at ON orders(created_at);
```

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| order_id | UUID | PK, NOT NULL | Unique order identifier |
| order_number | VARCHAR(50) | UNIQUE, NOT NULL | Human-readable order reference (e.g., ORD-20241215-143052-A4B9) |
| customer_name | VARCHAR(100) | NOT NULL | Customer's full name |
| email | VARCHAR(100) | NOT NULL | Customer's email address |
| total_amount | DECIMAL(10,2) | NOT NULL | Total order amount (calculated from items) |
| status | VARCHAR(20) | NOT NULL, CHECK | Current order status (enum) |
| created_at | TIMESTAMP(6) | AUTO | Order creation timestamp |
| updated_at | TIMESTAMP(6) | AUTO | Last modification timestamp |
| version | BIGINT | NOT NULL | Optimistic locking version |

#### 4.2.2 order_items Table

```sql
CREATE TABLE order_items (
    item_id UUID PRIMARY KEY,
    order_id UUID NOT NULL,
    product_id UUID NOT NULL,
    product_name VARCHAR(200) NOT NULL,
    quantity INTEGER NOT NULL CHECK (quantity > 0),
    price DECIMAL(10, 2) NOT NULL CHECK (price > 0),

    CONSTRAINT fk_order_items_order
        FOREIGN KEY (order_id)
        REFERENCES orders(order_id)
        ON DELETE CASCADE
);

-- Index for foreign key lookups
CREATE INDEX idx_order_items_order_id ON order_items(order_id);
```

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| item_id | UUID | PK, NOT NULL | Unique item identifier |
| order_id | UUID | FK, NOT NULL | Reference to parent order |
| product_id | UUID | NOT NULL | Product identifier (external reference) |
| product_name | VARCHAR(200) | NOT NULL | Product name (snapshot at order time) |
| quantity | INTEGER | NOT NULL, CHECK > 0 | Number of units |
| price | DECIMAL(10,2) | NOT NULL, CHECK > 0 | Price per unit at order time |

### 4.3 Relationships

| Relationship | Type | Cascade | Description |
|--------------|------|---------|-------------|
| orders ↔ order_items | One-to-Many | CASCADE | Deleting order deletes all items |

### 4.4 Indexes

| Table | Index Name | Columns | Purpose |
|-------|-----------|---------|---------|
| orders | idx_orders_email | email | Fast customer lookup |
| orders | idx_orders_status | status | Status-based queries |
| orders | idx_orders_created_at | created_at | Date range queries |
| order_items | idx_order_items_order_id | order_id | FK join optimization |

---

## 5. API Specifications

### 5.1 Base URL
```
http://localhost:8080/api/v1
```

### 5.2 Endpoints

#### 5.2.1 Create Order

**Endpoint:** `POST /orders`

**Description:** Create a new order with items

**Request Headers:**
```
Content-Type: application/json
```

**Request Body:**
```json
{
  "customerName": "John Doe",
  "email": "john.doe@example.com",
  "orderItems": [
    {
      "productId": "550e8400-e29b-41d4-a716-446655440000",
      "productName": "MacBook Pro 16\"",
      "quantity": 1,
      "price": 2499.99
    },
    {
      "productId": "550e8400-e29b-41d4-a716-446655440001",
      "productName": "Magic Mouse",
      "quantity": 2,
      "price": 79.99
    }
  ]
}
```

**Validation Rules:**
- `customerName`: Required, 2-100 characters
- `email`: Required, valid email format, max 100 characters
- `orderItems`: Required, at least 1 item, max 50 items
- `orderItems[].productId`: Required, valid UUID
- `orderItems[].productName`: Required, max 200 characters
- `orderItems[].quantity`: Required, min 1, max 1000
- `orderItems[].price`: Required, min 0.01

**Success Response:** `201 CREATED`
```json
{
  "orderId": "7f3e4d1c-8a2b-4c9d-ae5f-1234567890ab",
  "orderNumber": "ORD-20241215-143052-A4B9",
  "customerName": "John Doe",
  "email": "john.doe@example.com",
  "totalAmount": 2659.97,
  "status": "PENDING",
  "orderItems": [
    {
      "itemId": "9a1b2c3d-4e5f-6789-0abc-def123456789",
      "productId": "550e8400-e29b-41d4-a716-446655440000",
      "productName": "MacBook Pro 16\"",
      "quantity": 1,
      "price": 2499.99
    },
    {
      "itemId": "8b2c3d4e-5f67-8901-bcde-f12345678901",
      "productId": "550e8400-e29b-41d4-a716-446655440001",
      "productName": "Magic Mouse",
      "quantity": 2,
      "price": 79.99
    }
  ],
  "createdAt": "2024-12-15T14:30:52.123456",
  "updatedAt": "2024-12-15T14:30:52.123456",
  "version": 0
}
```

**Error Response:** `400 BAD REQUEST`
```json
{
  "timestamp": "2024-12-15T14:30:52.123456",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "errors": [
    {
      "field": "email",
      "message": "Email must be valid"
    },
    {
      "field": "orderItems",
      "message": "Order must have at least one item"
    }
  ]
}
```

---

#### 5.2.2 Get All Orders

**Endpoint:** `GET /orders`

**Description:** Retrieve all orders

**Success Response:** `200 OK`
```json
[
  {
    "orderId": "7f3e4d1c-8a2b-4c9d-ae5f-1234567890ab",
    "orderNumber": "ORD-20241215-143052-A4B9",
    "customerName": "John Doe",
    "email": "john.doe@example.com",
    "totalAmount": 2659.97,
    "status": "PENDING",
    "orderItems": [...],
    "createdAt": "2024-12-15T14:30:52.123456",
    "updatedAt": "2024-12-15T14:30:52.123456",
    "version": 0
  }
]
```

---

#### 5.2.3 Get Order by ID

**Endpoint:** `GET /orders/{id}`

**Path Parameters:**
- `id` (UUID, required): Order identifier

**Success Response:** `200 OK`
```json
{
  "orderId": "7f3e4d1c-8a2b-4c9d-ae5f-1234567890ab",
  "orderNumber": "ORD-20241215-143052-A4B9",
  ...
}
```

**Error Response:** `404 NOT FOUND`
```json
{
  "timestamp": "2024-12-15T14:30:52.123456",
  "status": 404,
  "error": "Not Found",
  "message": "Order not found with ID: 7f3e4d1c-8a2b-4c9d-ae5f-1234567890ab"
}
```

---

#### 5.2.4 Get Order by Order Number

**Endpoint:** `GET /orders/number/{orderNumber}`

**Path Parameters:**
- `orderNumber` (String, required): Order number

**Example:** `GET /orders/number/ORD-20241215-143052-A4B9`

**Success Response:** `200 OK` (same as Get by ID)

---

#### 5.2.5 Get Orders by Customer Email

**Endpoint:** `GET /orders/customer?email={email}`

**Query Parameters:**
- `email` (String, required): Customer email

**Example:** `GET /orders/customer?email=john.doe@example.com`

**Success Response:** `200 OK` (array of orders)

---

#### 5.2.6 Update Order

**Endpoint:** `PUT /orders/{id}`

**Description:** Update an existing order

**Path Parameters:**
- `id` (UUID, required): Order identifier

**Request Body:** Same as Create Order

**Success Response:** `200 OK` (updated order)

**Error Response:** `404 NOT FOUND` if order doesn't exist

---

#### 5.2.7 Update Order Status

**Endpoint:** `PATCH /orders/{id}/status?status={status}`

**Description:** Update order status only

**Path Parameters:**
- `id` (UUID, required): Order identifier

**Query Parameters:**
- `status` (OrderStatus, required): New status

**Valid Status Values:**
- `PENDING`
- `CONFIRMED`
- `PROCESSING`
- `SHIPPED`
- `DELIVERED`
- `CANCELLED`

**Example:** `PATCH /orders/7f3e4d1c-8a2b-4c9d-ae5f-1234567890ab/status?status=CONFIRMED`

**Success Response:** `200 OK` (updated order)

---

#### 5.2.8 Delete Order

**Endpoint:** `DELETE /orders/{id}`

**Description:** Delete an order and all its items

**Path Parameters:**
- `id` (UUID, required): Order identifier

**Success Response:** `204 NO CONTENT`

**Error Response:** `404 NOT FOUND` if order doesn't exist

---

### 5.3 API Response Codes

| Code | Meaning | Usage |
|------|---------|-------|
| 200 | OK | Successful GET, PUT, PATCH |
| 201 | Created | Successful POST |
| 204 | No Content | Successful DELETE |
| 400 | Bad Request | Validation failure |
| 404 | Not Found | Resource doesn't exist |
| 500 | Internal Server Error | Server-side error |

---

## 6. Class Design

### 6.1 Package Structure (Actual Implementation)

```
com.ecommerce.order
├── OrderServiceApplication.java         # Main application entry point
│
├── controller/
│   └── OrderController.java              # REST endpoints
│
├── service/
│   ├── OrderService.java                 # Business logic interface
│   └── OrderServiceImpl.java             # Business logic implementation
│
├── repository/
│   └── OrderRepository.java              # Data access layer
│
├── model/
│   ├── Order.java                        # Order entity
│   └── OrderItem.java                    # OrderItem entity
│
└── dto/
    ├── OrderRequest.java                 # API request DTO
    ├── OrderResponse.java                # API response DTO
    └── OrderItemDto.java                 # Item DTO
```

### 6.2 Class Diagrams

#### 6.2.1 Entity Classes

```
┌─────────────────────────────────┐
│         <<Entity>>               │
│           Order                  │
├─────────────────────────────────┤
│ - orderId: UUID                  │
│ - orderNumber: String            │
│ - customerName: String           │
│ - email: String                  │
│ - totalAmount: BigDecimal        │
│ - status: OrderStatus            │
│ - orderItems: List<OrderItem>    │
│ - createdAt: LocalDateTime       │
│ - updatedAt: LocalDateTime       │
│ - version: Long                  │
├─────────────────────────────────┤
│ + getters/setters()              │
└────────────┬────────────────────┘
             │ 1
             │ contains
             │ *
┌────────────┴────────────────────┐
│         <<Entity>>               │
│         OrderItem                │
├─────────────────────────────────┤
│ - itemId: UUID                   │
│ - productId: UUID                │
│ - productName: String            │
│ - quantity: Integer              │
│ - price: BigDecimal              │
│ - order: Order                   │
├─────────────────────────────────┤
│ + getLineTotal(): BigDecimal     │
│ + getters/setters()              │
└─────────────────────────────────┘
```

#### 6.2.2 Service Layer

```
┌─────────────────────────────────────────┐
│        <<Interface>>                     │
│         OrderService                     │
├─────────────────────────────────────────┤
│ + createOrder(OrderRequest): OrderResponse               │
│ + getOrderById(UUID): OrderResponse                      │
│ + getAllOrders(): List<OrderResponse>                    │
│ + updateOrder(UUID, OrderRequest): OrderResponse         │
│ + deleteOrder(UUID): void                                │
│ + getOrderByOrderNumber(String): OrderResponse           │
│ + getOrdersByEmail(String): List<OrderResponse>          │
│ + updateOrderStatus(UUID, OrderStatus): OrderResponse    │
└──────────────────────┬──────────────────┘
                       │ implements
                       ↓
┌────────────────────────────────────────────────┐
│              OrderServiceImpl                   │
├────────────────────────────────────────────────┤
│ - orderRepository: OrderRepository              │
├────────────────────────────────────────────────┤
│ + createOrder(OrderRequest): OrderResponse                │
│ + getOrderById(UUID): OrderResponse                       │
│ + getAllOrders(): List<OrderResponse>                     │
│ + updateOrder(UUID, OrderRequest): OrderResponse          │
│ + deleteOrder(UUID): void                                 │
│ + getOrderByOrderNumber(String): OrderResponse            │
│ + getOrdersByEmail(String): List<OrderResponse>           │
│ + updateOrderStatus(UUID, OrderStatus): OrderResponse     │
│ - generateOrderNumber(): String                           │
│ - mapToResponse(Order): OrderResponse                     │
└────────────────────────────────────────────────┘
```

---

## 7. Sequence Diagrams

### 7.1 Create Order Flow

```
Client          Controller       Service          Repository      Database
  │                 │               │                 │               │
  │ POST /orders    │               │                 │               │
  ├────────────────>│               │                 │               │
  │                 │ createOrder() │                 │               │
  │                 ├──────────────>│                 │               │
  │                 │               │ Validate        │               │
  │                 │               │ Generate UUID   │               │
  │                 │               │ Generate Order# │               │
  │                 │               │ Calculate Total │               │
  │                 │               │                 │               │
  │                 │               │ save(order)     │               │
  │                 │               ├────────────────>│               │
  │                 │               │                 │ INSERT orders │
  │                 │               │                 ├──────────────>│
  │                 │               │                 │ INSERT items  │
  │                 │               │                 ├──────────────>│
  │                 │               │                 │<──────────────┤
  │                 │               │<────────────────┤   Return      │
  │                 │               │                 │               │
  │                 │               │ mapToResponse() │               │
  │                 │<──────────────┤                 │               │
  │<────────────────┤ 201 Created   │                 │               │
  │  OrderResponse  │               │                 │               │
```

### 7.2 Get Order by ID Flow

```
Client          Controller       Service          Repository      Database
  │                 │               │                 │               │
  │ GET /orders/id  │               │                 │               │
  ├────────────────>│               │                 │               │
  │                 │ getOrderById()│                 │               │
  │                 ├──────────────>│                 │               │
  │                 │               │ findByIdWithItems()              │
  │                 │               ├────────────────>│               │
  │                 │               │                 │ SELECT + JOIN │
  │                 │               │                 ├──────────────>│
  │                 │               │                 │<──────────────┤
  │                 │               │<────────────────┤   Return      │
  │                 │               │ mapToResponse() │               │
  │                 │<──────────────┤                 │               │
  │<────────────────┤ 200 OK        │                 │               │
  │  OrderResponse  │               │                 │               │
```

---

## 8. Component Interactions

### 8.1 Technology Integration

```
┌─────────────────────────────────────────────────────┐
│              Spring Boot Application                 │
│                                                       │
│  ┌──────────────────┐      ┌──────────────────┐    │
│  │   Tomcat Server   │      │  Spring Context  │    │
│  │   (Port 8080)     │      │  (IoC Container) │    │
│  └──────────────────┘      └──────────────────┘    │
│                                                       │
│  ┌──────────────────────────────────────────────┐  │
│  │          Spring MVC (DispatcherServlet)       │  │
│  └──────────────────────────────────────────────┘  │
│                                                       │
│  ┌──────────────────┐      ┌──────────────────┐    │
│  │  Controllers      │      │   Services       │    │
│  │  (REST Layer)     │<────>│ (Business Logic) │    │
│  └──────────────────┘      └──────────────────┘    │
│                                                       │
│  ┌──────────────────┐      ┌──────────────────┐    │
│  │  Repositories     │      │   Hibernate      │    │
│  │  (Data Access)    │<────>│   (JPA Impl)     │    │
│  └──────────────────┘      └──────────────────┘    │
│                                                       │
│  ┌──────────────────────────────────────────────┐  │
│  │          HikariCP (Connection Pool)           │  │
│  └──────────────────────────────────────────────┘  │
│                                                       │
└───────────────────────┬─────────────────────────────┘
                        │ JDBC
                        ↓
              ┌─────────────────────┐
              │    PostgreSQL        │
              │  (Docker Container)  │
              │    Port: 5432        │
              └─────────────────────┘
```

### 8.2 Dependency Flow

```
Application Startup
       │
       ↓
OrderServiceApplication.main()
       │
       ↓
Spring Boot Auto-Configuration
       │
       ├─→ Scan packages (@ComponentScan)
       ├─→ Create beans (@Component, @Service, @Repository)
       ├─→ Configure DataSource (application.properties)
       ├─→ Initialize JPA/Hibernate
       ├─→ Create database tables (DDL auto)
       ├─→ Start Tomcat server (port 8080)
       │
       ↓
Application Ready
```

---

## 9. Error Handling

### 9.1 Exception Hierarchy (Planned)

```
Exception
    │
    └─→ RuntimeException
            │
            ├─→ OrderNotFoundException (404)
            ├─→ InvalidOrderException (400)
            ├─→ OrderValidationException (400)
            └─→ DatabaseException (500)
```

### 9.2 Error Response Format (Planned)

```json
{
  "timestamp": "2024-12-15T14:30:52.123456",
  "status": 404,
  "error": "Not Found",
  "message": "Order not found with ID: 7f3e4d1c-8a2b-4c9d-ae5f-1234567890ab",
  "path": "/api/v1/orders/7f3e4d1c-8a2b-4c9d-ae5f-1234567890ab"
}
```

### 9.3 Global Exception Handler (To Be Implemented)

```java
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleOrderNotFound(OrderNotFoundException ex) {
        // Return 404 with error details
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        // Return 400 with validation errors
    }
}
```

---

## 10. Security Considerations

### 10.1 Current State
- ⚠️ **No authentication/authorization** (future enhancement)
- ⚠️ **No API rate limiting** (future enhancement)
- ⚠️ **No input sanitization** (relies on validation only)

### 10.2 Planned Security Features
- JWT-based authentication
- Role-based access control (RBAC)
- API key validation
- Rate limiting
- Input sanitization
- SQL injection prevention (handled by JPA)
- HTTPS enforcement

---

## 11. Performance Considerations

### 11.1 Database Optimizations

**Implemented:**
- ✅ Connection pooling (HikariCP)
- ✅ Indexes on frequently queried columns
- ✅ Lazy loading for relationships (`@OneToMany(fetch = FetchType.LAZY)`)
- ✅ JOIN FETCH for N+1 query prevention

**Planned:**
- [ ] Query result caching (Redis)
- [ ] Database query optimization
- [ ] Pagination for list endpoints

### 11.2 Application Optimizations

**Implemented:**
- ✅ Transactional boundaries (`@Transactional`)
- ✅ Read-only transactions for GET operations

**Planned:**
- [ ] Response compression
- [ ] Async processing for non-critical operations
- [ ] Caching layer (Redis)

---

## 12. Testing Strategy

### 12.1 Test Pyramid

```
                 ▲
                /E\         E2E Tests (10%)
               /───\        - Full system tests
              /Unit \       - API contract tests
             /───────\
            /Integration\   Integration Tests (20%)
           /─────────────\  - Controller + Service + DB
          /   Unit Tests  \ Unit Tests (70%)
         /─────────────────\- Service logic
        /___________________\- Repository queries
                             - Utility methods
```

### 12.2 Test Coverage Goals

| Layer | Coverage Goal | Status |
|-------|---------------|--------|
| Controller | 75% | Pending |
| Service | 85% | Pending |
| Repository | 80% | Pending |
| **Overall** | **80%** | Pending |

### 12.3 Testing Tools

| Type | Framework | Purpose |
|------|-----------|---------|
| Unit Testing | JUnit 5 | Test framework |
| Mocking | Mockito | Mock dependencies |
| Assertions | AssertJ | Fluent assertions |
| Integration | Spring Test | Test with context |
| Coverage | JaCoCo | Coverage reporting |

---

## 13. Deployment Architecture

### 13.1 Current Deployment (Local Development)

```
┌─────────────────────────────────────┐
│       Developer Machine              │
│                                       │
│  ┌──────────────────────────────┐  │
│  │   Spring Boot Application     │  │
│  │   (mvn spring-boot:run)       │  │
│  │   Port: 8080                  │  │
│  └──────────────────────────────┘  │
│                                       │
│  ┌──────────────────────────────┐  │
│  │   Docker Desktop              │  │
│  │   ┌────────────────────────┐ │  │
│  │   │  PostgreSQL Container   │ │  │
│  │   │  Port: 5432             │ │  │
│  │   └────────────────────────┘ │  │
│  └──────────────────────────────┘  │
└─────────────────────────────────────┘
```

### 13.2 Planned Production Deployment

```
                    Internet
                       │
                       ↓
              ┌────────────────┐
              │  Load Balancer  │
              └────────┬───────┘
                       │
       ┌───────────────┼───────────────┐
       ↓               ↓               ↓
┌─────────────┐ ┌─────────────┐ ┌─────────────┐
│ Order Svc 1 │ │ Order Svc 2 │ │ Order Svc 3 │
│  (Docker)   │ │  (Docker)   │ │  (Docker)   │
└──────┬──────┘ └──────┬──────┘ └──────┬──────┘
       │               │               │
       └───────────────┼───────────────┘
                       │
                       ↓
              ┌────────────────┐
              │   PostgreSQL    │
              │    (RDS/Cloud)  │
              └────────────────┘
```

---

## 14. Future Enhancements

### 14.1 Phase 2 - Event-Driven Architecture
- [ ] Apache Kafka integration
- [ ] Event publishing (OrderCreated, OrderUpdated)
- [ ] Event consumption (PaymentCompleted, InventoryReserved)

### 14.2 Phase 3 - Additional Microservices
- [ ] Inventory Service
- [ ] Payment Service
- [ ] Notification Service
- [ ] File Storage Service (S3)

### 14.3 Phase 4 - Production Readiness
- [ ] Exception handling (`@ControllerAdvice`)
- [ ] Unit tests (JUnit + Mockito)
- [ ] Integration tests
- [ ] Redis caching
- [ ] API documentation (Swagger/OpenAPI)
- [ ] Monitoring (Prometheus + Grafana)
- [ ] Distributed tracing
- [ ] CI/CD pipeline
- [ ] Docker Compose for all services

---

## 15. Appendix

### 15.1 Configuration Files

#### application.properties
```properties
# Application
spring.application.name=order-service
server.port=8080

# PostgreSQL Database
spring.datasource.url=jdbc:postgresql://localhost:5432/orderdb
spring.datasource.username=postgres
spring.datasource.password=postgres123
spring.datasource.driver-class-name=org.postgresql.Driver

# JPA/Hibernate
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

# Logging
logging.level.com.ecommerce.order=DEBUG
logging.level.org.springframework.web=INFO
logging.pattern.console=%d{yyyy-MM-dd HH:mm:ss} - %msg%n
```

### 15.2 Docker Commands

```bash
# Start PostgreSQL
docker run --name postgres-orderdb \
  -e POSTGRES_PASSWORD=postgres123 \
  -e POSTGRES_DB=orderdb \
  -p 5432:5432 -d postgres:15

# View logs
docker logs postgres-orderdb

# Connect to PostgreSQL
docker exec -it postgres-orderdb psql -U postgres -d orderdb

# Stop container
docker stop postgres-orderdb

# Remove container
docker rm postgres-orderdb
```

### 15.3 Build & Run Commands

```bash
# Build
mvn clean package -DskipTests

# Run
mvn spring-boot:run

# Run tests
mvn test

# Check coverage
mvn verify
```

---

## 16. Glossary

| Term | Definition |
|------|------------|
| **DTO** | Data Transfer Object - Object for transferring data between layers |
| **Entity** | JPA entity class mapped to database table |
| **ORM** | Object-Relational Mapping - Mapping objects to database tables |
| **CRUD** | Create, Read, Update, Delete operations |
| **REST** | Representational State Transfer - API architectural style |
| **JPA** | Java Persistence API - Java specification for ORM |
| **IoC** | Inversion of Control - Design pattern for dependency injection |
| **UUID** | Universally Unique Identifier - 128-bit identifier |

---

**Document Status:** ✅ Active
**Last Review:** December 15, 2025
**Next Review:** After Phase 2 completion (Kafka integration)
