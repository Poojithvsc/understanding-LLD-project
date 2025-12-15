# 🍃 Spring Boot Basics - Your Complete Reference

**What You'll Learn:** Spring Boot fundamentals, annotations, and how everything works together.

---

## 🤔 Part 1: What is Spring Boot?

### Simple Explanation
**Spring Boot** = A framework that makes building Java web applications EASY.

**Without Spring Boot:**
- You write 100+ lines of configuration
- Set up web server manually
- Configure database manually
- Write tons of boilerplate code

**With Spring Boot:**
- Write actual business logic
- Everything else is AUTO-CONFIGURED
- You focus on WHAT you're building, not HOW to set it up

### Real-World Analogy
**Building a house:**
- **Without Spring Boot**: You build everything from scratch (foundations, plumbing, electrical)
- **With Spring Boot**: You get a pre-built house structure, you just decorate and furnish it

---

## 🏗️ Part 2: Spring Boot Architecture (Layers)

Your application has 4 main layers:

```
┌─────────────────────────────────────┐
│   CLIENT (Browser/Postman/App)      │
└──────────────┬──────────────────────┘
               │ HTTP Request
               ↓
┌─────────────────────────────────────┐
│   CONTROLLER LAYER                   │  ← Handles HTTP requests
│   @RestController                    │     Returns HTTP responses
└──────────────┬──────────────────────┘
               │ Calls
               ↓
┌─────────────────────────────────────┐
│   SERVICE LAYER                      │  ← Business logic
│   @Service                           │     Calculations, validations
└──────────────┬──────────────────────┘
               │ Uses
               ↓
┌─────────────────────────────────────┐
│   REPOSITORY LAYER                   │  ← Database access
│   @Repository                        │     CRUD operations
└──────────────┬──────────────────────┘
               │ Reads/Writes
               ↓
┌─────────────────────────────────────┐
│   DATABASE (PostgreSQL)              │  ← Stores data
└─────────────────────────────────────┘
```

### Flow Example: Creating an Order
```
1. Client sends: POST /api/v1/orders
   ↓
2. OrderController receives request
   ↓
3. Controller calls OrderService.createOrder()
   ↓
4. Service validates data, calculates total
   ↓
5. Service calls OrderRepository.save()
   ↓
6. Repository saves to PostgreSQL
   ↓
7. Database returns saved order
   ↓
8. Service returns to Controller
   ↓
9. Controller sends HTTP response to Client
```

---

## 🎯 Part 3: Important Annotations

Annotations are like **labels** that tell Spring Boot what a class does.

### Class-Level Annotations

| Annotation | What It Does | Where to Use |
|------------|--------------|--------------|
| `@SpringBootApplication` | Marks main application class | Main class only |
| `@RestController` | Handles HTTP requests | Controller classes |
| `@Service` | Contains business logic | Service classes |
| `@Repository` | Accesses database | Repository interfaces |
| `@Entity` | Represents database table | Model/Entity classes |

### Method-Level Annotations (Mapping HTTP)

| Annotation | HTTP Method | Purpose |
|------------|-------------|---------|
| `@GetMapping("/orders")` | GET | Retrieve data |
| `@PostMapping("/orders")` | POST | Create new data |
| `@PutMapping("/orders/{id}")` | PUT | Update data |
| `@PatchMapping("/orders/{id}")` | PATCH | Partially update |
| `@DeleteMapping("/orders/{id}")` | DELETE | Delete data |

### Field-Level Annotations (Database)

| Annotation | Purpose | Example |
|------------|---------|---------|
| `@Id` | Primary key | `@Id private UUID orderId;` |
| `@GeneratedValue` | Auto-generate value | `@GeneratedValue(strategy = GenerationType.UUID)` |
| `@Column` | Configure column | `@Column(name = "customer_name", nullable = false)` |
| `@Table` | Specify table name | `@Table(name = "orders")` |
| `@OneToMany` | One-to-many relationship | One order → many items |
| `@ManyToOne` | Many-to-one relationship | Many items → one order |

### Validation Annotations

| Annotation | What It Checks |
|------------|----------------|
| `@NotNull` | Field cannot be null |
| `@NotBlank` | String cannot be empty |
| `@Email` | Must be valid email format |
| `@Min(1)` | Number must be at least 1 |
| `@Max(1000)` | Number cannot exceed 1000 |
| `@Size(min=2, max=100)` | String length between 2-100 |

---

## 📁 Part 4: Project Structure Explained

```
src/main/java/com/ecommerce/order/
│
├── OrderServiceApplication.java    ← Main entry point (@SpringBootApplication)
│
├── controller/
│   └── OrderController.java        ← Handles HTTP (@RestController)
│
├── service/
│   ├── OrderService.java           ← Interface (defines what methods exist)
│   └── OrderServiceImpl.java       ← Implementation (@Service)
│
├── repository/
│   └── OrderRepository.java        ← Database access (@Repository)
│
├── model/
│   ├── Order.java                  ← Entity (@Entity)
│   └── OrderItem.java              ← Entity (@Entity)
│
└── dto/
    ├── OrderRequest.java           ← Data from client
    ├── OrderResponse.java          ← Data to client
    └── OrderItemDto.java           ← Item data transfer
```

---

## 🔄 Part 5: How Spring Boot Auto-Configuration Works

### The Magic: Dependency Injection

**Problem Without Dependency Injection:**
```java
public class OrderController {
    // You have to create objects manually
    OrderService orderService = new OrderServiceImpl();
    OrderRepository orderRepository = new OrderRepositoryImpl();
    // This is tedious and error-prone!
}
```

**Solution With Dependency Injection:**
```java
@RestController
public class OrderController {
    private final OrderService orderService;

    // Spring automatically provides the object!
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }
}
```

**How it works:**
1. Spring Boot scans all classes with `@Service`, `@Repository`, `@Controller`
2. Creates objects (beans) automatically
3. Injects them where needed (constructor injection)

---

## 🗄️ Part 6: JPA/Hibernate (Database Magic)

### What is JPA?
**JPA (Java Persistence API)** = Standard way to interact with databases in Java.

### What is Hibernate?
**Hibernate** = The actual implementation of JPA (the engine that does the work).

### The Magic: No SQL Needed!

**Traditional Way (Writing SQL):**
```sql
INSERT INTO orders (order_id, customer_name, email, total_amount, status)
VALUES ('abc-123', 'John Doe', 'john@example.com', 2029.97, 'PENDING');
```

**JPA Way (Just Java):**
```java
Order order = new Order();
order.setCustomerName("John Doe");
order.setEmail("john@example.com");
order.setTotalAmount(new BigDecimal("2029.97"));
order.setStatus(OrderStatus.PENDING);

orderRepository.save(order);  // Hibernate generates SQL for you!
```

### How JPA Knows What to Do

**Entity Class:**
```java
@Entity                                    // This is a database table
@Table(name = "orders")                    // Table name is "orders"
public class Order {

    @Id                                    // Primary key
    @GeneratedValue(strategy = GenerationType.UUID)  // Auto-generate UUID
    private UUID orderId;                  // Column: order_id

    @Column(name = "customer_name", nullable = false)  // Column: customer_name (required)
    private String customerName;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "total_amount", precision = 10, scale = 2)
    private BigDecimal totalAmount;        // Column: total_amount (DECIMAL(10,2))

    @Enumerated(EnumType.STRING)           // Store enum as string
    private OrderStatus status;
}
```

**What Hibernate Generates:**
```sql
CREATE TABLE orders (
    order_id UUID PRIMARY KEY,
    customer_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    total_amount DECIMAL(10,2),
    status VARCHAR(20)
);
```

---

## 🔗 Part 7: Relationships (Connecting Tables)

### One-to-Many Example

**One Order → Many Items**

```java
@Entity
@Table(name = "orders")
public class Order {
    @Id
    private UUID orderId;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems;  // One order has many items
}

@Entity
@Table(name = "order_items")
public class OrderItem {
    @Id
    private UUID itemId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;  // Many items belong to one order
}
```

**What This Creates:**

**orders** table:
| order_id | customer_name | total_amount |
|----------|---------------|--------------|
| abc-123 | John Doe | 2029.97 |

**order_items** table:
| item_id | order_id | product_name | price |
|---------|----------|--------------|-------|
| item-1 | abc-123 | Laptop | 999.99 |
| item-2 | abc-123 | Mouse | 29.99 |

Notice how `order_id` in `order_items` links to `orders` table!

---

## 🛠️ Part 8: Application Startup Flow

**What happens when you run:** `mvn spring-boot:run`

```
1. JVM starts
   ↓
2. Reads OrderServiceApplication.java
   ↓
3. Sees @SpringBootApplication annotation
   ↓
4. Spring Boot starts:
   - Reads application.properties
   - Configures database connection
   - Starts embedded Tomcat server on port 8080
   ↓
5. Component Scanning:
   - Finds all @RestController classes
   - Finds all @Service classes
   - Finds all @Repository classes
   ↓
6. Dependency Injection:
   - Creates objects (beans)
   - Injects dependencies
   ↓
7. Database Initialization:
   - Connects to PostgreSQL
   - Reads @Entity classes
   - Creates/updates tables automatically
   ↓
8. Server Ready!
   - Listens on http://localhost:8080
   - Waits for HTTP requests
```

---

## 📝 Part 9: Configuration File (application.properties)

**Location:** `src/main/resources/application.properties`

```properties
# Application Settings
spring.application.name=order-service     # App name
server.port=8080                          # Port to run on

# Database Connection (PostgreSQL)
spring.datasource.url=jdbc:postgresql://localhost:5432/orderdb
spring.datasource.username=postgres
spring.datasource.password=yourpassword
spring.datasource.driver-class-name=org.postgresql.Driver

# JPA/Hibernate Settings
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.hibernate.ddl-auto=update      # Auto-create/update tables
spring.jpa.show-sql=true                  # Print SQL to console
spring.jpa.properties.hibernate.format_sql=true  # Pretty-print SQL

# Logging
logging.level.com.ecommerce.order=DEBUG   # Your code: detailed logs
logging.level.org.springframework=INFO     # Spring: general logs
```

### What Each Setting Does

| Setting | Purpose |
|---------|---------|
| `spring.datasource.url` | Where to find the database |
| `spring.jpa.hibernate.ddl-auto=update` | Auto-create tables from @Entity classes |
| `spring.jpa.show-sql=true` | See SQL queries in console (great for learning!) |

---

## 🎯 Part 10: Common Patterns You'll Use

### Pattern 1: Creating an Entity
```java
@Entity
@Table(name = "table_name")
public class EntityName {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String someField;

    // Constructors, getters, setters
}
```

### Pattern 2: Creating a Repository
```java
@Repository
public interface EntityRepository extends JpaRepository<EntityName, UUID> {
    // Spring provides: save(), findById(), findAll(), deleteById()

    // Custom query methods:
    Optional<EntityName> findBySomeField(String field);
}
```

### Pattern 3: Creating a Service
```java
@Service
public class EntityServiceImpl implements EntityService {
    private final EntityRepository repository;

    public EntityServiceImpl(EntityRepository repository) {
        this.repository = repository;
    }

    public EntityResponse create(EntityRequest request) {
        // Business logic here
        return repository.save(entity);
    }
}
```

### Pattern 4: Creating a Controller
```java
@RestController
@RequestMapping("/api/v1/entities")
public class EntityController {
    private final EntityService service;

    public EntityController(EntityService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<EntityResponse> create(@RequestBody EntityRequest request) {
        return ResponseEntity.status(201).body(service.create(request));
    }
}
```

---

## 🚀 Quick Reference

### Running the Application
```bash
cd services/order-service
mvn spring-boot:run
```

### Testing Endpoints
```bash
# Create
curl -X POST http://localhost:8080/api/v1/orders -H "Content-Type: application/json" -d '{"data":"here"}'

# Read
curl http://localhost:8080/api/v1/orders

# Update
curl -X PUT http://localhost:8080/api/v1/orders/123 -H "Content-Type: application/json" -d '{"data":"here"}'

# Delete
curl -X DELETE http://localhost:8080/api/v1/orders/123
```

---

**Next Steps:** Read "03_POSTGRESQL_DOCKER_GUIDE.md" to set up your database!
