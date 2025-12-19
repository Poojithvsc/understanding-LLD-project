# Architecture Diagram with Java Concepts

The same diagram - but with Java concepts labeled at each box.

```
                                    ┌─────────┐
                                    │  USER   │
                                    └────┬────┘
                                         │ HTTP REST (JSON)
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
│  │  │ ┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄ │  │       │  │ ┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄ │  │        │
│  │  │ @RestController        │  │       │  │ @RestController        │  │        │
│  │  │ @RequestMapping        │  │       │  │ @GetMapping            │  │        │
│  │  │ @PostMapping           │  │       │  │ @PostMapping           │  │        │
│  │  │ @RequestBody           │  │       │  │ @PathVariable          │  │        │
│  │  └───────────┬────────────┘  │       │  └───────────┬────────────┘  │        │
│  │              │               │       │              │               │        │
│  │              ▼               │       │              ▼               │        │
│  │  ┌────────────────────────┐  │       │  ┌────────────────────────┐  │        │
│  │  │    OrderServiceImpl    │  │       │  │  InventoryServiceImpl  │  │        │
│  │  │ ┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄ │  │       │  │ ┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄ │  │        │
│  │  │ @Service               │  │       │  │ @Service               │  │        │
│  │  │ @Transactional         │  │       │  │ @Transactional         │  │        │
│  │  │ Dependency Injection   │  │       │  │ Dependency Injection   │  │        │
│  │  └───────────┬────────────┘  │       │  └───────────┬────────────┘  │        │
│  │              │               │       │              ▲               │        │
│  │              ▼               │       │              │               │        │
│  │  ┌────────────────────────┐  │       │  ┌────────────────────────┐  │        │
│  │  │  KafkaProducerService  │──┼───┐   │  │  KafkaConsumerService  │  │        │
│  │  │ ┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄ │  │   │   │  │ ┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄ │  │        │
│  │  │ KafkaTemplate.send()   │  │   │   │  │ @KafkaListener         │  │        │
│  │  │ CompletableFuture      │  │   │   │  │ (auto-receives msgs)   │  │        │
│  │  └────────────────────────┘  │   │   │  └────────────────────────┘  │        │
│  │              │               │   │   │              ▲               │        │
│  │              ▼               │   │   │              │               │        │
│  │  ┌────────────────────────┐  │   │   │  ┌────────────────────────┐  │        │
│  │  │    OrderRepository     │  │   │   │  │  ProductRepository     │  │        │
│  │  │ ┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄ │  │   │   │  │  InventoryRepository   │  │        │
│  │  │ extends JpaRepository  │  │   │   │  │ ┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄ │  │        │
│  │  │ findByOrderNumber()    │  │   │   │  │ extends JpaRepository  │  │        │
│  │  │ (Spring writes SQL)    │  │   │   │  │ findByLocation()       │  │        │
│  │  └───────────┬────────────┘  │   │   │  └───────────┬────────────┘  │        │
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
│                │    │ ┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄ │   │                          │
│                │    │ Topic = mailbox name        │───┘                          │
│                │    │ Producer sends here         │                              │
│                │    │ Consumer listens here       │                              │
│                │    │                             │                              │
│                │    │  ┌───────────────────────┐  │                              │
│                │    │  │  OrderCreatedEvent    │  │                              │
│                │    │  │ ┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄ │  │                              │
│                │    │  │ Plain Java class      │  │                              │
│                │    │  │ (POJO) with fields:   │  │                              │
│                │    │  │  - orderId            │  │                              │
│                │    │  │  - orderNumber        │  │                              │
│                │    │  │  - orderItems[]       │  │                              │
│                │    │  └───────────────────────┘  │                              │
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
│  │  │ ┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄ │  │                     │  │ ┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄ │  │    │
│  │  │ @Entity Order     │  │                     │  │ @Entity Product   │  │    │
│  │  │ @Table("orders")  │  │                     │  │ @Id, @Column      │  │    │
│  │  ├───────────────────┤  │                     │  ├───────────────────┤  │    │
│  │  │    order_items    │  │                     │  │     inventory     │  │    │
│  │  │ ┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄ │  │                     │  │ ┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄ │  │    │
│  │  │ @Entity OrderItem │  │                     │  │ @Entity Inventory │  │    │
│  │  │ @ManyToOne Order  │  │                     │  │ @ManyToOne Product│  │    │
│  │  └───────────────────┘  │                     │  └───────────────────┘  │    │
│  └─────────────────────────┘                     └─────────────────────────┘    │
│                                                                                 │
└ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ┘
```

---

## Box-by-Box Explanation

### 1. Controllers (Top of each service)
```
┌────────────────────────┐
│    OrderController     │
│ ┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄ │
│ @RestController        │  ← "I handle web requests"
│ @PostMapping           │  ← "POST = create"
│ @GetMapping            │  ← "GET = read"
│ @RequestBody           │  ← "Convert JSON to Java object"
└────────────────────────┘
```

---

### 2. Services (Middle of each service)
```
┌────────────────────────┐
│   OrderServiceImpl     │
│ ┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄ │
│ @Service               │  ← "I have business logic"
│ @Transactional         │  ← "If something fails, undo everything"
│ Dependency Injection   │  ← "Spring gives me what I need"
└────────────────────────┘
```

---

### 3. Kafka Services (Connect the two services)
```
ORDER SERVICE                              INVENTORY SERVICE
┌────────────────────────┐                ┌────────────────────────┐
│  KafkaProducerService  │───── KAFKA ───▶│  KafkaConsumerService  │
│ ┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄ │                │ ┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄ │
│ KafkaTemplate.send()   │                │ @KafkaListener         │
│ "Send message"         │                │ "Receive message"      │
└────────────────────────┘                └────────────────────────┘
```

---

### 4. Repositories (Talk to database)
```
┌────────────────────────┐
│    OrderRepository     │
│ ┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄ │
│ extends JpaRepository  │  ← "Give me free CRUD methods"
│ findByOrderNumber()    │  ← "Just name it, Spring writes SQL"
└────────────────────────┘
```

---

### 5. Kafka Event (The message being sent)
```
┌───────────────────────┐
│  OrderCreatedEvent    │
│ ┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄ │
│ Plain Java class      │  ← Just a class with fields
│ (called POJO)         │  ← Gets converted to JSON automatically
│  - orderId            │
│  - orderNumber        │
│  - orderItems[]       │
└───────────────────────┘
```

---

### 6. Database Tables (Bottom layer)
```
┌───────────────────┐
│      orders       │
│ ┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄ │
│ @Entity Order     │  ← "This class = this table"
│ @Table("orders")  │  ← "Table name is 'orders'"
│ @Id orderId       │  ← "Primary key"
│ @Column           │  ← "This field = this column"
├───────────────────┤
│    order_items    │
│ ┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄ │
│ @ManyToOne Order  │  ← "Many items belong to one order"
└───────────────────┘
```

---

## The Flow with Java Concepts

```
USER clicks "Place Order"
         │
         │  JSON: {"customerName": "John", "items": [...]}
         ▼
┌─────────────────────────────────────────────────────────┐
│  OrderController                                         │
│  @PostMapping + @RequestBody                            │
│  "Convert JSON to OrderRequest object"                  │
└────────────────────────┬────────────────────────────────┘
                         │
                         ▼
┌─────────────────────────────────────────────────────────┐
│  OrderServiceImpl                                        │
│  @Service + @Transactional                              │
│  "Create order, calculate total, save to DB"            │
└────────────────────────┬────────────────────────────────┘
                         │
         ┌───────────────┴───────────────┐
         ▼                               ▼
┌─────────────────────┐       ┌─────────────────────┐
│  OrderRepository    │       │ KafkaProducerService│
│  JpaRepository      │       │ KafkaTemplate.send()│
│  .save(order)       │       │ "Send event"        │
└─────────┬───────────┘       └──────────┬──────────┘
          │                              │
          ▼                              ▼
┌─────────────────────┐       ┌─────────────────────┐
│  PostgreSQL         │       │  Apache Kafka       │
│  @Entity → Table    │       │  Topic: order-created│
│  INSERT INTO orders │       └──────────┬──────────┘
└─────────────────────┘                  │
                                         ▼
                              ┌─────────────────────┐
                              │KafkaConsumerService │
                              │ @KafkaListener      │
                              │ "Receives message"  │
                              └──────────┬──────────┘
                                         │
                                         ▼
                              ┌─────────────────────┐
                              │InventoryServiceImpl │
                              │ "Reduce stock"      │
                              └──────────┬──────────┘
                                         │
                                         ▼
                              ┌─────────────────────┐
                              │  PostgreSQL         │
                              │  UPDATE products    │
                              │  SET stock = stock-1│
                              └─────────────────────┘
```

---

## Summary Table

| Box in Diagram | Java Concept | What It Does |
|----------------|--------------|--------------|
| **OrderController** | `@RestController` | Receives HTTP requests |
| **OrderServiceImpl** | `@Service` | Business logic |
| **KafkaProducerService** | `KafkaTemplate` | Sends messages |
| **KafkaConsumerService** | `@KafkaListener` | Receives messages |
| **OrderRepository** | `JpaRepository` | Database operations |
| **OrderCreatedEvent** | POJO (plain class) | Message format |
| **orders table** | `@Entity` | Maps class to table |
| **order_items table** | `@ManyToOne` | Relationship |
