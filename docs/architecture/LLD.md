# Low-Level Design (LLD)

## Order Service - Detailed Design

### Package Structure

```
com.ecommerce.order
├── controller/
│   └── OrderController.java
├── service/
│   ├── OrderService.java
│   └── impl/
│       └── OrderServiceImpl.java
├── repository/
│   └── OrderRepository.java
├── model/
│   ├── entity/
│   │   ├── Order.java
│   │   └── OrderItem.java
│   ├── dto/
│   │   ├── OrderRequest.java
│   │   ├── OrderResponse.java
│   │   └── OrderItemDto.java
│   └── enums/
│       └── OrderStatus.java
├── kafka/
│   ├── producer/
│   │   └── OrderEventProducer.java
│   ├── consumer/
│   │   └── PaymentEventConsumer.java
│   └── event/
│       ├── OrderCreatedEvent.java
│       └── PaymentCompletedEvent.java
├── exception/
│   ├── OrderNotFoundException.java
│   └── GlobalExceptionHandler.java
├── config/
│   ├── KafkaConfig.java
│   └── CacheConfig.java
└── mapper/
    └── OrderMapper.java
```

### Database Schema

#### orders table
```sql
CREATE TABLE orders (
    id BIGSERIAL PRIMARY KEY,
    order_number VARCHAR(50) UNIQUE NOT NULL,
    user_id BIGINT NOT NULL,
    total_amount DECIMAL(10,2) NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    version INT DEFAULT 0  -- For optimistic locking
);

CREATE INDEX idx_orders_user_id ON orders(user_id);
CREATE INDEX idx_orders_status ON orders(status);
CREATE INDEX idx_orders_created_at ON orders(created_at);
```

#### order_items table
```sql
CREATE TABLE order_items (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity INT NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE
);

CREATE INDEX idx_order_items_order_id ON order_items(order_id);
CREATE INDEX idx_order_items_product_id ON order_items(product_id);
```

### Class Diagrams

#### Order Entity

```java
@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String orderNumber;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();

    @Version
    private Integer version;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    // Constructors, getters, setters
}
```

#### OrderStatus Enum

```java
public enum OrderStatus {
    PENDING,
    INVENTORY_RESERVED,
    PAYMENT_PROCESSING,
    COMPLETED,
    CANCELLED,
    FAILED
}
```

### API Specifications

#### Create Order

**Endpoint**: `POST /api/v1/orders`

**Request**:
```json
{
  "userId": 12345,
  "items": [
    {
      "productId": 1,
      "quantity": 2,
      "price": 29.99
    },
    {
      "productId": 2,
      "quantity": 1,
      "price": 49.99
    }
  ]
}
```

**Response**: 201 Created
```json
{
  "orderId": 1001,
  "orderNumber": "ORD-2024-001",
  "userId": 12345,
  "totalAmount": 109.97,
  "status": "PENDING",
  "items": [...],
  "createdAt": "2024-01-15T10:30:00Z"
}
```

**Error Responses**:
- 400 Bad Request: Invalid input
- 404 Not Found: Product not found
- 500 Internal Server Error

#### Get Order

**Endpoint**: `GET /api/v1/orders/{id}`

**Response**: 200 OK
```json
{
  "orderId": 1001,
  "orderNumber": "ORD-2024-001",
  "userId": 12345,
  "totalAmount": 109.97,
  "status": "COMPLETED",
  "items": [...],
  "createdAt": "2024-01-15T10:30:00Z",
  "updatedAt": "2024-01-15T10:35:00Z"
}
```

### Service Layer Logic

#### OrderService Interface

```java
public interface OrderService {
    OrderResponse createOrder(OrderRequest request);
    OrderResponse getOrder(Long orderId);
    Page<OrderResponse> getOrdersByUserId(Long userId, Pageable pageable);
    OrderResponse cancelOrder(Long orderId);
    void updateOrderStatus(Long orderId, OrderStatus status);
}
```

#### Business Logic: Create Order

```
1. Validate input (userId, items not empty, quantities > 0)
2. Calculate total amount
3. Generate unique order number (ORD-{YEAR}-{SEQUENCE})
4. Begin database transaction
   a. Save Order entity with status = PENDING
   b. Save OrderItem entities
   c. Commit transaction
5. Publish OrderCreatedEvent to Kafka
6. Return OrderResponse
7. If any step fails, rollback and throw appropriate exception
```

### Kafka Events

#### OrderCreatedEvent

```json
{
  "eventId": "evt-123456",
  "eventType": "ORDER_CREATED",
  "timestamp": "2024-01-15T10:30:00Z",
  "payload": {
    "orderId": 1001,
    "orderNumber": "ORD-2024-001",
    "userId": 12345,
    "items": [
      {
        "productId": 1,
        "quantity": 2
      }
    ],
    "totalAmount": 109.97
  }
}
```

**Topic**: `order-events`
**Partition Key**: `userId` (for ordering guarantees per user)

### Error Handling

#### Exception Hierarchy

```
RuntimeException
├── OrderException (base)
│   ├── OrderNotFoundException
│   ├── InvalidOrderStatusException
│   └── OrderCreationException
└── BusinessException
    ├── InsufficientInventoryException
    └── PaymentFailedException
```

#### Global Exception Handler

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleOrderNotFound(OrderNotFoundException ex) {
        // Return 404 with error details
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidation(ValidationException ex) {
        // Return 400 with validation errors
    }

    // ... other handlers
}
```

### Caching Strategy

```java
@Cacheable(value = "orders", key = "#orderId")
public OrderResponse getOrder(Long orderId) {
    // Cache hit: return from Redis
    // Cache miss: fetch from DB, store in Redis
}

@CacheEvict(value = "orders", key = "#orderId")
public void updateOrderStatus(Long orderId, OrderStatus status) {
    // Invalidate cache on update
}
```

**Cache Configuration**:
- TTL: 5 minutes
- Max entries: 10,000
- Eviction policy: LRU

### Transaction Management

```java
@Transactional(isolation = Isolation.READ_COMMITTED,
               propagation = Propagation.REQUIRED,
               timeout = 30)
public OrderResponse createOrder(OrderRequest request) {
    // All DB operations in one transaction
    // Rollback on any exception
}
```

### Logging Strategy

```java
log.info("Creating order for userId={}, itemCount={}", userId, items.size());
log.debug("Order details: {}", orderRequest);
log.error("Failed to create order: userId={}", userId, exception);
```

**Log Format**: JSON structured logging
```json
{
  "timestamp": "2024-01-15T10:30:00Z",
  "level": "INFO",
  "logger": "OrderService",
  "message": "Creating order",
  "userId": 12345,
  "correlationId": "abc-123",
  "spanId": "xyz-789"
}
```

### Testing Strategy

#### Unit Tests (JUnit 5 + Mockito)

```java
@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderEventProducer eventProducer;

    @InjectMocks
    private OrderServiceImpl orderService;

    @Test
    void createOrder_ValidInput_ReturnsOrderResponse() {
        // Given
        OrderRequest request = createValidRequest();
        when(orderRepository.save(any())).thenReturn(order);

        // When
        OrderResponse response = orderService.createOrder(request);

        // Then
        assertNotNull(response);
        assertEquals(OrderStatus.PENDING, response.getStatus());
        verify(eventProducer).publishOrderCreated(any());
    }

    @Test
    void createOrder_InvalidInput_ThrowsValidationException() {
        // Test validation logic
    }
}
```

#### Integration Tests (Testcontainers)

```java
@SpringBootTest
@Testcontainers
class OrderControllerIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15");

    @Container
    static KafkaContainer kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.5.0"));

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void createOrder_EndToEnd_Success() {
        // Test full flow with real database and Kafka
    }
}
```

### Configuration

#### application.yml

```yaml
spring:
  application:
    name: order-service

  datasource:
    url: jdbc:postgresql://localhost:5432/ecommerce_orders
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
    hikari:
      maximum-pool-size: 10
      minimum-idle: 5

  kafka:
    bootstrap-servers: localhost:9092
    producer:
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      value-serializer: org.springframework.kafka.support.serializer.JsonSerializer
    consumer:
      group-id: order-service-group
      key-deserializer: org.apache.kafka.common.serialization.StringDeserializer
      value-deserializer: org.springframework.kafka.support.serializer.JsonDeserializer

  cache:
    type: redis
  redis:
    host: localhost
    port: 6379
```

## Sequence Diagrams

### Create Order Flow

```
Client          OrderController    OrderService    OrderRepo    KafkaProducer    Kafka
  |                    |                |             |              |            |
  |--POST /orders----->|                |             |              |            |
  |                    |--createOrder-->|             |              |            |
  |                    |                |--validate-->|              |            |
  |                    |                |--save------>|              |            |
  |                    |                |<--order-----|              |            |
  |                    |                |--publishEvent------------>|            |
  |                    |                |              |            |--send----->|
  |                    |<--response-----|              |            |            |
  |<--201 Created------|                |              |            |            |
```

## Performance Considerations

1. **Database Indexing**: Indexes on user_id, status, created_at
2. **Connection Pooling**: HikariCP with optimal pool size
3. **Caching**: Redis for frequently accessed orders
4. **Async Processing**: Kafka for non-blocking operations
5. **Pagination**: Limit query results, use cursor-based pagination

## Security Considerations

1. **Input Validation**: Bean Validation (@Valid, @NotNull, etc.)
2. **SQL Injection Prevention**: Use JPA/PreparedStatements
3. **Authentication**: JWT token validation (to be implemented)
4. **Authorization**: User can only access their own orders
5. **Rate Limiting**: API rate limiting (future enhancement)

---

**Note**: This LLD template should be created for each microservice (Inventory, Payment, etc.) with similar level of detail.
