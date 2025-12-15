# 🎓 Complete Learning Workflow
## E-Commerce Microservices Project - Enterprise Development Path

**Student:** Poojith
**Date Started:** December 15, 2025
**Duration:** 10 hours (2-day intensive)
**Status:** Ready to Execute

---

## 📌 Project Vision

Build a **production-ready e-commerce order processing system** that demonstrates:
- Cloud-native architecture (AWS RDS + S3)
- Microservices design patterns
- Event-driven communication (Apache Kafka)
- Professional testing practices
- Industry-standard development workflow

---

## 🎯 7 Core Learning Objectives

| # | Learning Goal | Technology/Skill | Status |
|---|---------------|------------------|--------|
| 1 | Build complete specification-based project | LLD → Implementation | ⏳ In Progress |
| 2 | Use cloud database (Whizlabs subscription) | AWS RDS PostgreSQL | 📋 Planned |
| 3 | Learn blob storage integration | AWS S3 | 📋 Planned |
| 4 | Create and use industry-standard LLD | Technical Documentation | ✅ Completed |
| 5 | Write and understand unit tests | JUnit 5 + Mockito | 📋 Planned |
| 6 | Professional Git workflow | GitHub PR (dev → main) | 📋 Planned |
| 7 | Microservices + event streaming | Spring Boot + Kafka | 📋 Planned |

---

## 📊 Progress Tracking Dashboard

**Last Updated:** December 15, 2025
**Overall Progress:** 40% Complete (Foundation Phase Done)

### Phase Completion Summary

| Phase | Goal | Status | Completion | Tasks Done |
|-------|------|--------|------------|------------|
| **Foundation** | Spring Boot + PostgreSQL setup | ✅ COMPLETE | 100% | All foundation tasks |
| **Phase 1** | AWS RDS PostgreSQL migration | 📋 PENDING | 0% | 0/10 tasks |
| **Phase 2** | AWS S3 blob storage | 📋 PENDING | 0% | 0/9 tasks |
| **Phase 3** | Unit testing with JUnit + Mockito | 📋 PENDING | 0% | 0/8 tasks |
| **Phase 4** | Git PR workflow | 📋 PENDING | 0% | 0/9 tasks |
| **Phase 5** | Inventory Service microservice | 📋 PENDING | 0% | 0/10 tasks |
| **Phase 6** | Apache Kafka integration | 📋 PENDING | 0% | 0/9 tasks |
| **Phase 7** | Documentation updates | 📋 PENDING | 0% | 0/7 tasks |

### How to Track Progress

After completing each task:
1. Mark the checkbox with `[x]` in the task list
2. Update the "Tasks Done" count in the table above
3. Recalculate the "Completion %" for that phase
4. Update "Overall Progress" percentage
5. Save and commit this file to Git

**Formula:** Phase Completion % = (Tasks Done / Total Tasks) × 100

---

## 🏗️ Final Architecture (What You'll Build)

```
┌─────────────────────────────────────────────────────────────────┐
│                         CLIENT LAYER                             │
│                    (Browser / Mobile App)                        │
└────────────────┬────────────────────────────────────────────────┘
                 │ HTTP/REST
                 ↓
┌─────────────────────────────────────────────────────────────────┐
│                     MICROSERVICES LAYER                          │
│                                                                  │
│  ┌─────────────────────┐         ┌─────────────────────┐       │
│  │  ORDER SERVICE      │         │ INVENTORY SERVICE   │       │
│  │  (Port: 8080)       │────────→│  (Port: 8081)       │       │
│  │                     │  Kafka  │                     │       │
│  │ - Create orders     │  Events │ - Track stock       │       │
│  │ - Upload receipts   │         │ - Update inventory  │       │
│  │ - Manage orders     │         │ - Check availability│       │
│  └──────┬──────────────┘         └──────┬──────────────┘       │
│         │                                │                      │
└─────────┼────────────────────────────────┼──────────────────────┘
          │                                │
          │                                │
┌─────────┼────────────────────────────────┼──────────────────────┐
│         │        EVENT STREAMING LAYER   │                      │
│         │                                │                      │
│         │      ┌─────────────────────┐   │                      │
│         └─────→│   APACHE KAFKA      │←──┘                      │
│                │   (Port: 9092)      │                          │
│                │                     │                          │
│                │ Topics:             │                          │
│                │ - order.created     │                          │
│                │ - inventory.updated │                          │
│                └─────────────────────┘                          │
└─────────────────────────────────────────────────────────────────┘
          │                                │
          ↓                                ↓
┌─────────────────────────────────────────────────────────────────┐
│                       DATA LAYER (AWS CLOUD)                     │
│                                                                  │
│  ┌─────────────────────┐         ┌─────────────────────┐       │
│  │   AWS RDS           │         │     AWS S3          │       │
│  │   PostgreSQL        │         │   Object Storage    │       │
│  │   (Cloud Database)  │         │                     │       │
│  │                     │         │ - Order receipts    │       │
│  │ Schemas:            │         │ - Product images    │       │
│  │ - orderdb           │         │ - Documents         │       │
│  │ - inventorydb       │         │                     │       │
│  └─────────────────────┘         └─────────────────────┘       │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

---

## 📋 Phase-by-Phase Workflow

### **PHASE 1: Cloud Database Setup (AWS RDS)**
**Learning Goal #2: Use Whizlabs to spin up cloud DB**

#### Tasks:
- [ ] Log into Whizlabs AWS account
- [ ] Navigate to RDS service
- [ ] Create PostgreSQL 15 database instance
- [ ] Configure security groups (allow your IP)
- [ ] Get RDS endpoint URL
- [ ] Update `application.properties` with RDS connection string
- [ ] Test connection from Order Service
- [ ] Verify tables are created in cloud database
- [ ] Stop local Docker PostgreSQL container

#### What You'll Learn:
- AWS RDS console navigation
- Database instance provisioning
- Security groups and network configuration
- Connection string management
- Cloud vs local database differences

#### Deliverables:
- ✅ Running RDS PostgreSQL instance
- ✅ Order Service connected to cloud DB
- ✅ Data persisting in cloud (not local)

#### Verification:
```bash
# Test cloud DB connection
curl -X POST http://localhost:8080/api/v1/orders -H "Content-Type: application/json" -d '{...}'

# Verify in AWS RDS Query Editor
SELECT * FROM orders;
```

---

### **PHASE 2: AWS S3 Blob Storage Integration**
**Learning Goal #3: Learn blob storage like S3**

#### Tasks:
- [ ] Create S3 bucket in AWS (e.g., `ecommerce-order-receipts`)
- [ ] Configure bucket permissions (private with signed URLs)
- [ ] Add AWS SDK dependencies to `pom.xml`
- [ ] Create `S3Service` class for file operations
- [ ] Add new column `receipt_url` to `orders` table
- [ ] Create endpoint: `POST /api/v1/orders/{orderId}/receipt`
- [ ] Implement file upload logic (PDF/image → S3)
- [ ] Generate presigned URL for file download
- [ ] Test upload and download flow

#### What You'll Learn:
- S3 bucket creation and configuration
- AWS SDK for Java usage
- Multipart file upload handling
- Presigned URL generation
- Object storage vs file system

#### Code Changes:
```java
// New class: S3Service.java
@Service
public class S3Service {
    private final S3Client s3Client;

    public String uploadFile(MultipartFile file, String orderId) {
        // Upload to S3 and return URL
    }

    public String generatePresignedUrl(String s3Key) {
        // Generate temporary download URL
    }
}

// Updated: OrderController.java
@PostMapping("/{orderId}/receipt")
public ResponseEntity<String> uploadReceipt(
    @PathVariable UUID orderId,
    @RequestParam("file") MultipartFile file
) {
    String s3Url = s3Service.uploadFile(file, orderId.toString());
    orderService.updateReceiptUrl(orderId, s3Url);
    return ResponseEntity.ok(s3Url);
}
```

#### Deliverables:
- ✅ S3 bucket created and configured
- ✅ File upload endpoint working
- ✅ Files stored in S3 (not local filesystem)
- ✅ Receipt URLs saved in database

#### Verification:
```bash
# Upload receipt
curl -X POST http://localhost:8080/api/v1/orders/{orderId}/receipt \
  -F "file=@receipt.pdf"

# Response: S3 URL
# https://ecommerce-order-receipts.s3.amazonaws.com/orders/{orderId}/receipt.pdf
```

---

### **PHASE 3: Unit Testing**
**Learning Goal #5: Write and understand unit tests**

#### Tasks:
- [ ] Create test class: `OrderServiceImplTest.java`
- [ ] Write test: `testCreateOrder_Success()`
- [ ] Write test: `testCreateOrder_InvalidData()`
- [ ] Write test: `testGetOrderById_Found()`
- [ ] Write test: `testGetOrderById_NotFound()`
- [ ] Use Mockito to mock `OrderRepository`
- [ ] Create test class: `OrderControllerTest.java`
- [ ] Write controller tests with MockMvc
- [ ] Run tests: `mvn test`
- [ ] Generate coverage report
- [ ] Aim for >70% code coverage

#### What You'll Learn:
- JUnit 5 annotations (@Test, @BeforeEach, etc.)
- Mockito mocking framework
- Test-driven development thinking
- Arrange-Act-Assert pattern
- MockMvc for controller testing
- Code coverage analysis

#### Example Test:
```java
@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderServiceImpl orderService;

    @Test
    void testCreateOrder_Success() {
        // Arrange
        OrderRequest request = new OrderRequest();
        request.setCustomerName("Poojith");
        request.setEmail("poojith@example.com");

        Order savedOrder = new Order();
        savedOrder.setOrderId(UUID.randomUUID());

        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);

        // Act
        OrderResponse response = orderService.createOrder(request);

        // Assert
        assertNotNull(response);
        assertEquals("Poojith", response.getCustomerName());
        verify(orderRepository, times(1)).save(any(Order.class));
    }
}
```

#### Deliverables:
- ✅ 10+ unit tests written
- ✅ All tests passing (`mvn test`)
- ✅ >70% code coverage
- ✅ Understanding of testing principles

#### Verification:
```bash
mvn test
# [INFO] Tests run: 12, Failures: 0, Errors: 0, Skipped: 0
# [INFO] BUILD SUCCESS
```

---

### **PHASE 4: Git Workflow & Pull Request**
**Learning Goal #6: Professional Git workflow (dev → main)**

#### Tasks:
- [ ] Ensure all changes committed to `dev` branch
- [ ] Write meaningful commit messages
- [ ] Push `dev` branch to GitHub
- [ ] Navigate to GitHub repository
- [ ] Create Pull Request: `dev` → `main`
- [ ] Write PR description (what changed, why)
- [ ] Add screenshots/testing evidence
- [ ] Review changes in GitHub UI
- [ ] Merge Pull Request
- [ ] Delete `dev` branch (or keep for next iteration)
- [ ] Pull latest `main` locally

#### What You'll Learn:
- Git branching strategies
- Commit message best practices
- Pull Request workflow
- Code review process
- Branch management
- Collaborative development practices

#### Git Commands:
```bash
# Ensure you're on dev branch
git checkout dev
git status

# Commit all changes
git add .
git commit -m "feat: Add AWS RDS, S3, unit tests, and Kafka integration

- Migrated from local PostgreSQL to AWS RDS
- Integrated S3 for order receipt storage
- Added comprehensive unit tests (>70% coverage)
- Built Inventory Service microservice
- Integrated Apache Kafka for event streaming
- Updated LLD with cloud architecture

🤖 Generated with Claude Code"

# Push to GitHub
git push origin dev

# Create PR on GitHub UI
# Navigate to: https://github.com/Poojithvsc/understanding-LLD-project
# Click: "Compare & pull request"
# Fill in PR template
# Merge!
```

#### PR Description Template:
```markdown
## Summary
Complete implementation of cloud-native e-commerce order processing system with microservices architecture.

## Changes Made
- ✅ AWS RDS PostgreSQL integration
- ✅ AWS S3 blob storage for receipts
- ✅ Unit tests with JUnit + Mockito
- ✅ Inventory Service (2nd microservice)
- ✅ Apache Kafka event streaming
- ✅ Updated LLD documentation

## Testing
- All unit tests passing (12/12)
- Manual API testing completed
- End-to-end Kafka event flow verified

## Screenshots
[Add screenshots of working system]

## Reviewer Notes
This project demonstrates enterprise-level microservices development with cloud integration. Ready for architectural review and feedback.
```

#### Deliverables:
- ✅ Clean commit history
- ✅ PR created and merged
- ✅ `main` branch updated with all features
- ✅ Professional documentation in PR

---

### **PHASE 5: Build Inventory Service (Microservice #2)**
**Learning Goal #7a: Learn microservices architecture**

#### Tasks:
- [ ] Create new Spring Boot project: `inventory-service`
- [ ] Same structure as Order Service (controller, service, repository)
- [ ] Create entities: `Product`, `Inventory`
- [ ] Create REST endpoints:
  - `GET /api/v1/inventory/{productId}` - Get stock level
  - `POST /api/v1/inventory` - Add product inventory
  - `PATCH /api/v1/inventory/{productId}/reduce` - Reduce stock
  - `PATCH /api/v1/inventory/{productId}/increase` - Increase stock
- [ ] Configure to run on port 8081
- [ ] Connect to RDS (different schema: `inventorydb`)
- [ ] Write unit tests for Inventory Service
- [ ] Test independently from Order Service

#### What You'll Learn:
- Microservices independence
- Service-specific databases (database per service pattern)
- Port configuration for multiple services
- API design for inventory management
- Running multiple Spring Boot apps simultaneously

#### Database Schema:
```sql
-- inventorydb schema

CREATE TABLE products (
    product_id UUID PRIMARY KEY,
    product_name VARCHAR(200) NOT NULL,
    description TEXT,
    price DECIMAL(10,2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE inventory (
    inventory_id UUID PRIMARY KEY,
    product_id UUID NOT NULL REFERENCES products(product_id),
    stock_quantity INTEGER NOT NULL DEFAULT 0,
    reserved_quantity INTEGER NOT NULL DEFAULT 0,
    available_quantity INTEGER GENERATED ALWAYS AS (stock_quantity - reserved_quantity) STORED,
    last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

#### Deliverables:
- ✅ Inventory Service running on port 8081
- ✅ Independent database schema
- ✅ REST API working
- ✅ Unit tests passing
- ✅ Can run both services simultaneously

#### Verification:
```bash
# Terminal 1: Run Order Service
cd services/order-service
mvn spring-boot:run
# Runs on port 8080

# Terminal 2: Run Inventory Service
cd services/inventory-service
mvn spring-boot:run
# Runs on port 8081

# Test Inventory Service
curl http://localhost:8081/api/v1/inventory/{productId}
```

---

### **PHASE 6: Apache Kafka Integration**
**Learning Goal #7b: Learn event-driven architecture**

#### Tasks:
- [ ] Run Kafka + Zookeeper in Docker
- [ ] Create Kafka topics: `order.created`, `inventory.updated`
- [ ] Add Kafka dependencies to both services
- [ ] Configure Kafka properties in both services
- [ ] **Order Service:** Publish `OrderCreatedEvent` when order created
- [ ] **Inventory Service:** Consume `OrderCreatedEvent` and reduce stock
- [ ] Test end-to-end flow
- [ ] Add logging to track event flow
- [ ] Handle event processing failures

#### What You'll Learn:
- Event-driven architecture concepts
- Kafka producers and consumers
- Async communication between microservices
- Event serialization (JSON)
- Topic management
- Consumer groups
- Event ordering and reliability

#### Docker Compose for Kafka:
```yaml
version: '3.8'
services:
  zookeeper:
    image: confluentinc/cp-zookeeper:7.5.0
    environment:
      ZOOKEEPER_CLIENT_PORT: 2181
    ports:
      - "2181:2181"

  kafka:
    image: confluentinc/cp-kafka:7.5.0
    depends_on:
      - zookeeper
    ports:
      - "9092:9092"
    environment:
      KAFKA_BROKER_ID: 1
      KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181
      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://localhost:9092
      KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 1
```

#### Event Flow:
```
1. User creates order via POST /api/v1/orders
   ↓
2. Order Service saves order to database
   ↓
3. Order Service publishes OrderCreatedEvent to Kafka topic "order.created"
   {
     "orderId": "uuid",
     "orderNumber": "ORD-...",
     "items": [
       {"productId": "uuid", "quantity": 2}
     ]
   }
   ↓
4. Kafka stores event
   ↓
5. Inventory Service consumes event from "order.created"
   ↓
6. Inventory Service reduces stock for each product
   ↓
7. Inventory Service publishes InventoryUpdatedEvent to "inventory.updated"
   ↓
8. (Optional) Order Service consumes confirmation
```

#### Code Example:
```java
// Order Service - Producer
@Service
public class OrderEventPublisher {
    private final KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate;

    public void publishOrderCreated(Order order) {
        OrderCreatedEvent event = new OrderCreatedEvent(
            order.getOrderId(),
            order.getOrderNumber(),
            order.getOrderItems()
        );
        kafkaTemplate.send("order.created", event);
        log.info("Published OrderCreatedEvent: {}", event.getOrderNumber());
    }
}

// Inventory Service - Consumer
@Service
public class OrderEventConsumer {

    @KafkaListener(topics = "order.created", groupId = "inventory-service")
    public void handleOrderCreated(OrderCreatedEvent event) {
        log.info("Received OrderCreatedEvent: {}", event.getOrderNumber());

        for (OrderItemDto item : event.getItems()) {
            inventoryService.reduceStock(item.getProductId(), item.getQuantity());
        }

        log.info("Inventory updated for order: {}", event.getOrderNumber());
    }
}
```

#### Deliverables:
- ✅ Kafka running in Docker
- ✅ Events published from Order Service
- ✅ Events consumed by Inventory Service
- ✅ End-to-end flow working
- ✅ Logging shows event processing

#### Verification:
```bash
# Create order
curl -X POST http://localhost:8080/api/v1/orders -H "Content-Type: application/json" -d '{...}'

# Check Order Service logs
# [INFO] Published OrderCreatedEvent: ORD-20251215-123456-ABCD

# Check Inventory Service logs
# [INFO] Received OrderCreatedEvent: ORD-20251215-123456-ABCD
# [INFO] Reducing stock for product: 550e8400-e29b-41d4-a716-446655440000
# [INFO] Inventory updated for order: ORD-20251215-123456-ABCD

# Verify stock reduced
curl http://localhost:8081/api/v1/inventory/550e8400-e29b-41d4-a716-446655440000
# {"stockQuantity": 98, "availableQuantity": 98}
```

---

### **PHASE 7: Update Documentation**
**Learning Goal #4: Maintain LLD and documentation**

#### Tasks:
- [ ] Update `LLD_UPDATED.md` with:
  - AWS RDS connection details
  - S3 integration architecture
  - Inventory Service design
  - Kafka event flow diagrams
  - Updated database schema (both services)
- [ ] Update `PROJECT_SUMMARY.md` with completion status
- [ ] Create `DEPLOYMENT_GUIDE.md` for running entire system
- [ ] Update `README.md` with new architecture
- [ ] Add Kafka event flow sequence diagram

#### Deliverables:
- ✅ Comprehensive LLD reflecting final architecture
- ✅ Updated project documentation
- ✅ Deployment guide for reviewer
- ✅ Professional README

---

## 🎯 Success Criteria

### Technical Achievements:
- ✅ 2 microservices running independently
- ✅ Cloud database (AWS RDS) in production use
- ✅ Blob storage (S3) integrated
- ✅ Event streaming (Kafka) working
- ✅ Unit tests >70% coverage
- ✅ Professional Git workflow executed

### Learning Achievements:
- ✅ Can read LLD and implement project
- ✅ Understand cloud database setup
- ✅ Know how to integrate S3
- ✅ Can write effective unit tests
- ✅ Comfortable with Git PR workflow
- ✅ Understand microservices architecture
- ✅ Know event-driven patterns with Kafka

### Documentation Achievements:
- ✅ Industry-standard LLD
- ✅ Complete project summary
- ✅ Deployment guide
- ✅ Professional Git history

---

## 📊 Progress Tracking

| Phase | Status | Completion % |
|-------|--------|-------------|
| Phase 1: AWS RDS Setup | 📋 Pending | 0% |
| Phase 2: S3 Integration | 📋 Pending | 0% |
| Phase 3: Unit Testing | 📋 Pending | 0% |
| Phase 4: Git PR Workflow | 📋 Pending | 0% |
| Phase 5: Inventory Service | 📋 Pending | 0% |
| Phase 6: Kafka Integration | 📋 Pending | 0% |
| Phase 7: Documentation | 📋 Pending | 0% |

**Overall Progress:** 0% → Target: 100%

---

## 🛠️ Technology Stack (Complete)

| Category | Technology | Version | Purpose |
|----------|-----------|---------|---------|
| **Language** | Java | 21 | Programming language |
| **Framework** | Spring Boot | 3.2.0 | Application framework |
| **Cloud DB** | AWS RDS PostgreSQL | 15 | Production database |
| **Blob Storage** | AWS S3 | Latest | File/document storage |
| **Messaging** | Apache Kafka | 7.5.0 | Event streaming |
| **Container** | Docker | Latest | Infrastructure containerization |
| **Testing** | JUnit 5 + Mockito | Latest | Unit testing |
| **Build Tool** | Maven | 3.9+ | Dependency management |
| **Version Control** | Git + GitHub | Latest | Source control |
| **Cloud Provider** | AWS (Whizlabs) | - | RDS + S3 services |

---

## 📁 Final Project Structure

```
project/
├── docs/
│   ├── architecture/
│   │   ├── HLD.md
│   │   └── LLD_UPDATED.md          # Updated with cloud + Kafka
│   ├── learning/
│   │   ├── 01_FUNDAMENTALS.md
│   │   ├── 02_SPRING_BOOT_BASICS.md
│   │   └── 03_HANDS_ON_TUTORIAL.md
│   └── guides/
│       └── DEPLOYMENT_GUIDE.md     # New: How to run everything
│
├── services/
│   ├── order-service/              # Microservice 1
│   │   ├── src/
│   │   │   ├── main/java/com/ecommerce/order/
│   │   │   │   ├── controller/
│   │   │   │   ├── service/
│   │   │   │   │   └── S3Service.java          # New: S3 integration
│   │   │   │   ├── repository/
│   │   │   │   ├── model/
│   │   │   │   ├── dto/
│   │   │   │   └── events/
│   │   │   │       ├── OrderCreatedEvent.java  # New: Kafka event
│   │   │   │       └── OrderEventPublisher.java
│   │   │   └── test/java/
│   │   │       ├── OrderServiceImplTest.java   # New: Unit tests
│   │   │       └── OrderControllerTest.java
│   │   └── pom.xml                 # Updated: AWS SDK, Kafka
│   │
│   └── inventory-service/          # Microservice 2 (NEW)
│       ├── src/
│       │   ├── main/java/com/ecommerce/inventory/
│       │   │   ├── controller/
│       │   │   ├── service/
│       │   │   ├── repository/
│       │   │   ├── model/
│       │   │   │   ├── Product.java
│       │   │   │   └── Inventory.java
│       │   │   └── events/
│       │   │       └── OrderEventConsumer.java  # Kafka consumer
│       │   └── test/java/
│       └── pom.xml
│
├── docker-compose.yml              # Kafka + Zookeeper
├── LEARNING_WORKFLOW.md            # This file
├── PROJECT_SUMMARY.md              # Updated
└── PROGRESS.md                     # Updated

```

---

## 🚀 Quick Start Commands (After Completion)

```bash
# 1. Start Kafka
docker-compose up -d

# 2. Start Order Service (Terminal 1)
cd services/order-service
mvn spring-boot:run

# 3. Start Inventory Service (Terminal 2)
cd services/inventory-service
mvn spring-boot:run

# 4. Create order (triggers Kafka event)
curl -X POST http://localhost:8080/api/v1/orders \
  -H "Content-Type: application/json" \
  -d '{...}'

# 5. Verify inventory updated
curl http://localhost:8081/api/v1/inventory/{productId}

# 6. Upload receipt to S3
curl -X POST http://localhost:8080/api/v1/orders/{orderId}/receipt \
  -F "file=@receipt.pdf"
```

---

## 📞 For Your Reviewer

**Dear Reviewer,**

This project demonstrates comprehensive understanding of:

1. **Cloud-Native Development**: AWS RDS and S3 integration
2. **Microservices Architecture**: Multiple independent services
3. **Event-Driven Design**: Kafka-based async communication
4. **Professional Testing**: Unit tests with high coverage
5. **Development Best Practices**: Git workflow, PR process, documentation
6. **LLD-Driven Development**: Implementation follows documented design

**Technologies Mastered:**
- Spring Boot 3.2.0
- AWS RDS PostgreSQL
- AWS S3
- Apache Kafka
- JUnit 5 + Mockito
- Docker
- Git/GitHub

**Ready for:** Code review, architectural feedback, and guidance on organizational alignment.

---

**Last Updated:** December 15, 2025
**Status:** Ready to Execute → Awaiting Phase 1 Start
**Student:** Poojith
