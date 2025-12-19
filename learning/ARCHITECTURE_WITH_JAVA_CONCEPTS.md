# Architecture with Java Concepts

This shows the same architecture with Java/Spring annotations labeled.

```
                                    +---------+
                                    |  USER   |
                                    +----+----+
                                         |
                    +--------------------+--------------------+
                    |                                         |
                    | HTTP REST (JSON)                        | HTTP REST (JSON)
                    v                                         v
+---------------------------------------+   +---------------------------------------+
|        INVENTORY SERVICE              |   |         ORDER SERVICE                 |
|           (Port 8081)                 |   |           (Port 8080)                 |
|                                       |   |                                       |
|  +--------------------------------+   |   |  +--------------------------------+   |
|  |     InventoryController        |   |   |  |      OrderController           |   |
|  | ----------------------------- |   |   |  | ----------------------------- |   |
|  | @RestController               |   |   |  | @RestController               |   |
|  | @RequestMapping("/api/v1")    |   |   |  | @RequestMapping("/api/v1")    |   |
|  | @GetMapping, @PostMapping     |   |   |  | @PostMapping, @GetMapping     |   |
|  +---------------+----------------+   |   |  +---------------+----------------+   |
|                  |                    |   |                  |                    |
|                  v                    |   |                  v                    |
|  +--------------------------------+   |   |  +--------------------------------+   |
|  |    InventoryServiceImpl        |   |   |  |     OrderServiceImpl           |   |
|  | ----------------------------- |   |   |  | ----------------------------- |   |
|  | @Service                      |   |   |  | @Service                      |   |
|  | @Transactional                |   |   |  | @Transactional                |   |
|  +---------------+----------------+   |   |  +---------------+----------------+   |
|                  |                    |   |                  |                    |
|                  |                    |   |                  v                    |
|  +--------------------------------+   |   |  +--------------------------------+   |
|  |    KafkaConsumerService        |<--+---+--|     KafkaProducerService       |   |
|  | ----------------------------- |   |   |  | ----------------------------- |   |
|  | @KafkaListener                |   |   |  | KafkaTemplate.send()           |   |
|  | (auto receives messages)      | KAFKA |  | CompletableFuture (async)      |   |
|  +---------------+----------------+   |   |  +--------------------------------+   |
|                  |                    |   |                  |                    |
|                  v                    |   |                  v                    |
|  +--------------------------------+   |   |  +--------------------------------+   |
|  |  ProductRepository             |   |   |  |      OrderRepository           |   |
|  |  InventoryRepository           |   |   |  | ----------------------------- |   |
|  | ----------------------------- |   |   |  | extends JpaRepository          |   |
|  | extends JpaRepository         |   |   |  | findByOrderNumber()            |   |
|  +---------------+----------------+   |   |  +---------------+----------------+   |
+------------------+--------------------+   +------------------+--------------------+
                   |                                           |
                   v                                           v
        +-------------------+                       +-------------------+
        |   PostgreSQL      |                       |   PostgreSQL      |
        |   inventorydb     |                       |     orderdb       |
        | ----------------- |                       | ----------------- |
        | @Entity Product   |                       | @Entity Order     |
        | @Entity Inventory |                       | @Entity OrderItem |
        +-------------------+                       +-------------------+
```

---

## Key Java Concepts by Layer

### 1. Controller Layer
```
@RestController        - "I handle web requests"
@RequestMapping        - "My base URL path"
@PostMapping           - "POST = create"
@GetMapping            - "GET = read"
@RequestBody           - "Convert JSON to Java object"
@Valid                 - "Validate the input"
```

### 2. Service Layer
```
@Service               - "I contain business logic"
@Transactional         - "If something fails, undo everything"
@Autowired             - "Spring, give me this dependency"
```

### 3. Kafka Layer
```
KafkaTemplate.send()   - "Send message to Kafka"
@KafkaListener         - "Auto-receive messages from topic"
CompletableFuture      - "Handle async response"
```

### 4. Repository Layer
```
extends JpaRepository  - "Give me free CRUD methods"
findByXxx()            - "Spring writes the SQL for me"
@Query                 - "Custom SQL when needed"
```

### 5. Entity/Database Layer
```
@Entity                - "This class = database table"
@Table                 - "Table name"
@Id                    - "Primary key"
@Column                - "Column mapping"
@ManyToOne             - "Relationship: many items to one order"
```

---

## The Flow with Annotations

```
USER sends POST /api/v1/orders with JSON body
                    |
                    v
    @RestController + @PostMapping + @RequestBody
    "Receive request, convert JSON to OrderRequest"
                    |
                    v
    @Service + @Transactional
    "Create order, calculate total, generate order number"
                    |
        +-----------+-----------+
        |                       |
        v                       v
    JpaRepository           KafkaTemplate
    .save(order)            .send(event)
        |                       |
        v                       v
    PostgreSQL              Kafka Topic
    (orderdb)               (order-created)
                                |
                                v
                        @KafkaListener
                        "Receives message automatically"
                                |
                                v
                        @Service
                        "Reduce stock quantity"
                                |
                                v
                        JpaRepository.save()
                                |
                                v
                        PostgreSQL (inventorydb)
```

---

## Summary Table

| Layer | Annotation | Purpose |
|-------|------------|---------|
| Controller | `@RestController` | Handle HTTP requests |
| Service | `@Service` | Business logic |
| Kafka Producer | `KafkaTemplate` | Send messages |
| Kafka Consumer | `@KafkaListener` | Receive messages |
| Repository | `JpaRepository` | Database operations |
| Entity | `@Entity` | Map class to table |
