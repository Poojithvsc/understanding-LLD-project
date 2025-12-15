# 🎯 Hands-On Tutorial - Build Your First Order Service

**Follow these steps.** I'll guide you through each one!

---

## ✅ Prerequisites Check

Before starting, verify you have:
- [ ] Java 21+ installed (`java -version`)
- [ ] Maven installed (`mvn -version`)
- [ ] Docker Desktop installed and running (`docker --version`)
- [ ] VS Code or IntelliJ IDEA
- [ ] Git configured

---

## 📚 SESSION 1: Your First Entity (30 minutes)

### Step 1: Clean the Workspace
We'll keep the existing structure but understand what each file does.

**Files you have:**
```
services/order-service/
├── pom.xml                      ← Maven configuration (dependencies)
├── src/main/java/com/ecommerce/order/
│   ├── OrderServiceApplication.java    ← Main application
│   ├── model/
│   │   ├── Order.java          ← Order entity
│   │   └── OrderItem.java      ← OrderItem entity
│   ├── repository/
│   │   └── OrderRepository.java ← Database access
│   ├── service/
│   │   ├── OrderService.java    ← Interface
│   │   └── OrderServiceImpl.java ← Implementation
│   ├── controller/
│   │   └── OrderController.java ← REST endpoints
│   └── dto/
│       ├── OrderRequest.java    ← Input data
│       ├── OrderResponse.java   ← Output data
│       └── OrderItemDto.java    ← Item data
└── src/main/resources/
    └── application.properties   ← Configuration
```

### Step 2: Read and Understand Order Entity

**YOUR TASK:** Open `services/order-service/src/main/java/com/ecommerce/order/model/Order.java`

**Read through the file and answer these questions:**
1. What does `@Entity` annotation do?
2. What does `@Id` mean?
3. What does `@GeneratedValue(strategy = GenerationType.UUID)` do?
4. What is the relationship between Order and OrderItem?

**When done, tell me your answers!**

---

### Step 3: Understand the Repository

**YOUR TASK:** Open `OrderRepository.java`

**Questions:**
1. Why does it `extends JpaRepository`?
2. What methods do you get for free?
3. What is the difference between `findById()` and `findByOrderNumber()`?

---

### Step 4: Understand the Service Layer

**YOUR TASK:** Open `OrderServiceImpl.java`

**Questions:**
1. What does `@Service` annotation do?
2. Why do we have both `OrderService` (interface) and `OrderServiceImpl` (class)?
3. In `createOrder()` method, what business logic happens?

---

### Step 5: Understand the Controller

**YOUR TASK:** Open `OrderController.java`

**Questions:**
1. What does `@RestController` do?
2. What is `@PostMapping` vs `@GetMapping`?
3. What does `ResponseEntity` do?

---

## 🐳 SESSION 2: Set Up PostgreSQL with Docker (20 minutes)

### Step 1: Pull PostgreSQL Docker Image

**RUN THIS COMMAND:**
```bash
docker pull postgres:15
```

**What this does:** Downloads PostgreSQL version 15 from Docker Hub.

---

### Step 2: Create and Run PostgreSQL Container

**RUN THIS COMMAND:**
```bash
docker run --name postgres-orderdb -e POSTGRES_PASSWORD=postgres123 -e POSTGRES_DB=orderdb -p 5432:5432 -d postgres:15
```

**Let's break it down:**
- `--name postgres-orderdb` = Name your container
- `-e POSTGRES_PASSWORD=postgres123` = Set password
- `-e POSTGRES_DB=orderdb` = Create database named "orderdb"
- `-p 5432:5432` = Map port 5432 (PostgreSQL default)
- `-d postgres:15` = Run in background using PostgreSQL 15

**Verify it's running:**
```bash
docker ps
```

You should see your container running!

---

### Step 3: Test Database Connection

**RUN THIS COMMAND:**
```bash
docker exec -it postgres-orderdb psql -U postgres -d orderdb
```

**What this does:** Opens PostgreSQL command line.

**Try these SQL commands:**
```sql
\l              -- List all databases
\dt             -- List all tables (should be empty for now)
\q              -- Quit
```

---

### Step 4: Update application.properties

**YOUR TASK:** Open `src/main/resources/application.properties`

**CHANGE FROM H2 TO POSTGRESQL:**

**Find these H2 lines (DELETE THEM):**
```properties
# H2 Database
spring.datasource.url=jdbc:h2:mem:orderdb
spring.datasource.driverClassName=org.h2.Driver
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
```

**ADD THESE POSTGRESQL LINES:**
```properties
# PostgreSQL Database
spring.datasource.url=jdbc:postgresql://localhost:5432/orderdb
spring.datasource.username=postgres
spring.datasource.password=postgres123
spring.datasource.driver-class-name=org.postgresql.Driver
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
```

**KEEP THESE LINES (DON'T CHANGE):**
```properties
spring.application.name=order-service
server.port=8080
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
logging.level.com.ecommerce.order=DEBUG
```

---

### Step 5: Update pom.xml (Add PostgreSQL Driver)

**YOUR TASK:** Open `pom.xml`

**FIND THIS SECTION (around line 38-43):**
```xml
<!-- H2 Database -->
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <scope>runtime</scope>
</dependency>
```

**DELETE IT and ADD THIS:**
```xml
<!-- PostgreSQL Database -->
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <scope>runtime</scope>
</dependency>
```

**SAVE THE FILE!**

---

## 🚀 SESSION 3: Run Your Application with PostgreSQL (10 minutes)

### Step 1: Build the Application

**RUN THIS COMMAND:**
```bash
cd "services/order-service"
mvn clean package -DskipTests
```

**What this does:**
- `clean` = Delete old build files
- `package` = Compile and create JAR file
- `-DskipTests` = Skip tests for now

**You should see:** `BUILD SUCCESS`

---

### Step 2: Run the Application

**RUN THIS COMMAND:**
```bash
mvn spring-boot:run
```

**Watch the console output! You should see:**
```
Hibernate:
    create table order_items (...)

Hibernate:
    create table orders (...)

Started OrderServiceApplication in X.XXX seconds
```

**What just happened?**
- Spring Boot connected to PostgreSQL
- Hibernate read your `@Entity` classes
- Automatically created tables in PostgreSQL!

---

### Step 3: Verify Tables Were Created

**OPEN A NEW TERMINAL** (keep Spring Boot running in first terminal)

**RUN THIS:**
```bash
docker exec -it postgres-orderdb psql -U postgres -d orderdb
```

**THEN RUN:**
```sql
\dt
```

**You should see:**
```
         List of relations
 Schema |    Name     | Type  |  Owner
--------+-------------+-------+----------
 public | order_items | table | postgres
 public | orders      | table | postgres
```

**WOW! The tables were created automatically!**

**See the structure:**
```sql
\d orders
\d order_items
```

**Exit PostgreSQL:**
```sql
\q
```

---

## 🧪 SESSION 4: Test Your Application (15 minutes)

### Step 1: Create Your First Order

**OPEN A NEW TERMINAL** (keep Spring Boot running)

**RUN THIS:**
```bash
curl -X POST http://localhost:8080/api/v1/orders \
  -H "Content-Type: application/json" \
  -d '{
    "customerName": "Poojith",
    "email": "poojith@example.com",
    "orderItems": [
      {
        "productId": "550e8400-e29b-41d4-a716-446655440000",
        "productName": "MacBook Pro",
        "quantity": 1,
        "price": 1999.99
      }
    ]
  }'
```

**You should get a response with:**
- Order ID (UUID)
- Order Number (e.g., ORD-20241214-...)
- Total Amount (calculated automatically!)
- Status: PENDING

---

### Step 2: Verify Data in PostgreSQL

**CHECK THE DATABASE:**
```bash
docker exec -it postgres-orderdb psql -U postgres -d orderdb
```

**SEE YOUR DATA:**
```sql
SELECT * FROM orders;
SELECT * FROM order_items;
```

**YOUR DATA IS PERMANENTLY STORED!** Even if you stop Spring Boot, the data is still there.

**Exit:**
```sql
\q
```

---

### Step 3: Test Other Endpoints

**GET ALL ORDERS:**
```bash
curl http://localhost:8080/api/v1/orders
```

**GET BY EMAIL:**
```bash
curl "http://localhost:8080/api/v1/orders/customer?email=poojith@example.com"
```

**UPDATE STATUS:**
```bash
curl -X PATCH "http://localhost:8080/api/v1/orders/{YOUR_ORDER_ID}/status?status=CONFIRMED"
```

(Replace `{YOUR_ORDER_ID}` with actual ID from step 1)

---

## 🎓 QUIZ TIME!

**Answer these to test your understanding:**

1. **What is the difference between H2 and PostgreSQL?**

2. **What happens when Spring Boot starts with `spring.jpa.hibernate.ddl-auto=update`?**

3. **Where is the business logic (calculating total amount)?**
   - A) Controller
   - B) Service
   - C) Repository
   - D) Entity

4. **What does `@OneToMany` mean in Order entity?**

5. **How does Spring Boot know to create a "orders" table?**

---

## 🎯 Next Steps

After completing this tutorial:
1. Read the reference documents (`01_FUNDAMENTALS.md`, `02_SPRING_BOOT_BASICS.md`)
2. Experiment: Try creating more orders
3. Explore: Look at the data in PostgreSQL
4. Learn: Read through each Java file and understand annotations

**Then tell me:** "Tutorial complete!" and we'll move to the next session!

---

## 🆘 Troubleshooting

**Problem: Port 8080 already in use**
```bash
# Windows
netstat -ano | findstr :8080
taskkill //F //PID {PID_NUMBER}
```

**Problem: Docker container won't start**
```bash
docker ps -a
docker rm postgres-orderdb
# Then run the docker run command again
```

**Problem: Cannot connect to database**
- Check Docker is running: `docker ps`
- Check password matches in application.properties
- Check database name is "orderdb"
