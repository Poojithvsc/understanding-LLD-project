# Test Strategy

## Overview

This document outlines the comprehensive testing strategy for the E-Commerce Order Processing System, covering all levels of testing from unit to end-to-end.

## Testing Pyramid

```
        /\
       /E2E\         10% - End-to-End Tests
      /------\
     /  Int   \      20% - Integration Tests
    /----------\
   /   Unit     \    70% - Unit Tests
  /--------------\
```

## Test Coverage Goals

- **Overall Code Coverage**: Minimum 80%
- **Critical Business Logic**: Minimum 90%
- **Service Layer**: Minimum 85%
- **Controller Layer**: Minimum 75%
- **Repository Layer**: Minimum 80%

## 1. Unit Tests

### Purpose
Test individual components in isolation with mocked dependencies.

### Scope
- Service layer methods
- Utility classes
- Mappers
- Validators
- Custom exceptions

### Tools
- **Framework**: JUnit 5
- **Mocking**: Mockito
- **Assertions**: AssertJ

### Example: OrderService Unit Test

```java
@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderEventProducer eventProducer;

    @Mock
    private OrderMapper orderMapper;

    @InjectMocks
    private OrderServiceImpl orderService;

    @Test
    @DisplayName("Should create order successfully with valid input")
    void createOrder_ValidInput_Success() {
        // Given
        OrderRequest request = OrderTestData.createValidRequest();
        Order order = OrderTestData.createPendingOrder();
        OrderResponse expectedResponse = OrderTestData.createOrderResponse();

        when(orderMapper.toEntity(request)).thenReturn(order);
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        when(orderMapper.toResponse(order)).thenReturn(expectedResponse);

        // When
        OrderResponse actualResponse = orderService.createOrder(request);

        // Then
        assertThat(actualResponse).isNotNull();
        assertThat(actualResponse.getStatus()).isEqualTo(OrderStatus.PENDING);
        assertThat(actualResponse.getTotalAmount()).isEqualByComparingTo(new BigDecimal("109.97"));

        verify(orderRepository).save(any(Order.class));
        verify(eventProducer).publishOrderCreated(any(OrderCreatedEvent.class));
    }

    @Test
    @DisplayName("Should throw exception when order not found")
    void getOrder_NotFound_ThrowsException() {
        // Given
        Long orderId = 999L;
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        // When / Then
        assertThatThrownBy(() -> orderService.getOrder(orderId))
            .isInstanceOf(OrderNotFoundException.class)
            .hasMessage("Order not found with id: 999");

        verify(orderRepository).findById(orderId);
    }

    @Test
    @DisplayName("Should not publish event when save fails")
    void createOrder_SaveFails_NoEventPublished() {
        // Given
        OrderRequest request = OrderTestData.createValidRequest();
        when(orderRepository.save(any())).thenThrow(new DataAccessException("DB Error"));

        // When / Then
        assertThatThrownBy(() -> orderService.createOrder(request))
            .isInstanceOf(OrderCreationException.class);

        verify(eventProducer, never()).publishOrderCreated(any());
    }
}
```

### Best Practices
- Follow AAA pattern (Arrange, Act, Assert)
- Use descriptive test names
- One assertion per test (when possible)
- Test edge cases and error scenarios
- Use test data builders for complex objects

## 2. Integration Tests

### Purpose
Test multiple components working together, including real database and message broker.

### Scope
- Repository layer with real database
- Controller layer with full context
- Kafka producer/consumer integration
- Cache integration

### Tools
- **Framework**: Spring Boot Test
- **Containers**: Testcontainers (PostgreSQL, Kafka, Redis)
- **HTTP Testing**: MockMvc / TestRestTemplate

### Example: Order Repository Integration Test

```java
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
class OrderRepositoryIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
        .withDatabaseName("test_db")
        .withUsername("test")
        .withPassword("test");

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void saveOrder_ValidOrder_PersistsSuccessfully() {
        // Given
        Order order = Order.builder()
            .orderNumber("ORD-TEST-001")
            .userId(100L)
            .totalAmount(new BigDecimal("99.99"))
            .status(OrderStatus.PENDING)
            .build();

        // When
        Order savedOrder = orderRepository.save(order);
        entityManager.flush();
        entityManager.clear();

        // Then
        Order foundOrder = orderRepository.findById(savedOrder.getId()).orElseThrow();
        assertThat(foundOrder.getOrderNumber()).isEqualTo("ORD-TEST-001");
        assertThat(foundOrder.getUserId()).isEqualTo(100L);
    }

    @Test
    void findByUserId_MultipleOrders_ReturnsCorrectOrders() {
        // Given
        createOrderForUser(100L);
        createOrderForUser(100L);
        createOrderForUser(200L);
        entityManager.flush();

        // When
        List<Order> orders = orderRepository.findByUserId(100L);

        // Then
        assertThat(orders).hasSize(2);
        assertThat(orders).allMatch(o -> o.getUserId().equals(100L));
    }
}
```

### Example: Controller Integration Test

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class OrderControllerIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15");

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private OrderRepository orderRepository;

    @BeforeEach
    void setup() {
        orderRepository.deleteAll();
    }

    @Test
    void createOrder_ValidRequest_Returns201() {
        // Given
        OrderRequest request = OrderRequest.builder()
            .userId(100L)
            .items(List.of(
                OrderItemDto.builder()
                    .productId(1L)
                    .quantity(2)
                    .price(new BigDecimal("29.99"))
                    .build()
            ))
            .build();

        // When
        ResponseEntity<OrderResponse> response = restTemplate.postForEntity(
            "/api/v1/orders",
            request,
            OrderResponse.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(OrderStatus.PENDING);

        // Verify persistence
        List<Order> orders = orderRepository.findAll();
        assertThat(orders).hasSize(1);
    }

    @Test
    void getOrder_NonExistent_Returns404() {
        // When
        ResponseEntity<ErrorResponse> response = restTemplate.getForEntity(
            "/api/v1/orders/999",
            ErrorResponse.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}
```

### Example: Kafka Integration Test

```java
@SpringBootTest
@Testcontainers
@EmbeddedKafka
class OrderKafkaIntegrationTest {

    @Container
    static KafkaContainer kafka = new KafkaContainer(
        DockerImageName.parse("confluentinc/cp-kafka:7.5.0")
    );

    @Autowired
    private OrderEventProducer producer;

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Test
    void publishOrderCreated_ValidEvent_MessageSentToKafka() throws Exception {
        // Given
        OrderCreatedEvent event = OrderCreatedEvent.builder()
            .orderId(100L)
            .orderNumber("ORD-001")
            .userId(1L)
            .build();

        CountDownLatch latch = new CountDownLatch(1);

        // When
        producer.publishOrderCreated(event);

        // Then
        boolean messageReceived = latch.await(10, TimeUnit.SECONDS);
        assertThat(messageReceived).isTrue();
    }
}
```

## 3. End-to-End Tests

### Purpose
Test complete user workflows across multiple services.

### Scope
- Full order creation flow
- Order cancellation flow
- Payment and notification flow

### Tools
- **Framework**: Spring Boot Test
- **Containers**: Testcontainers with all services
- **HTTP Client**: RestAssured

### Example: E2E Test

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class OrderProcessingE2ETest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15");

    @Container
    static KafkaContainer kafka = new KafkaContainer(
        DockerImageName.parse("confluentinc/cp-kafka:7.5.0")
    );

    @Container
    static GenericContainer<?> redis = new GenericContainer<>("redis:7-alpine")
        .withExposedPorts(6379);

    @LocalServerPort
    private int port;

    @Test
    void completeOrderFlow_HappyPath() {
        // Given - Create order
        OrderRequest request = createOrderRequest();

        // When - Submit order
        OrderResponse orderResponse = RestAssured
            .given()
                .contentType(ContentType.JSON)
                .body(request)
            .when()
                .post("http://localhost:" + port + "/api/v1/orders")
            .then()
                .statusCode(201)
                .extract()
                .as(OrderResponse.class);

        // Then - Verify order created
        assertThat(orderResponse.getStatus()).isEqualTo(OrderStatus.PENDING);

        // Wait for async processing
        await()
            .atMost(Duration.ofSeconds(10))
            .pollInterval(Duration.ofMillis(500))
            .until(() -> {
                OrderResponse updated = getOrder(orderResponse.getOrderId());
                return updated.getStatus() == OrderStatus.COMPLETED;
            });

        // Verify final state
        OrderResponse finalOrder = getOrder(orderResponse.getOrderId());
        assertThat(finalOrder.getStatus()).isEqualTo(OrderStatus.COMPLETED);

        // Verify side effects
        verifyInventoryReserved(request.getItems());
        verifyPaymentProcessed(orderResponse.getOrderId());
        verifyNotificationSent(orderResponse.getUserId());
    }
}
```

## 4. Contract Tests

### Purpose
Ensure service APIs don't break when changes are made.

### Tools
- **Framework**: Spring Cloud Contract
- **Consumer Driven Contracts**

### Example: Contract Definition

```groovy
Contract.make {
    description "should return order by id"
    request {
        method GET()
        url "/api/v1/orders/1"
    }
    response {
        status 200
        headers {
            contentType(applicationJson())
        }
        body([
            orderId: 1,
            orderNumber: "ORD-2024-001",
            status: "PENDING"
        ])
    }
}
```

## 5. Performance Tests

### Purpose
Ensure system meets performance requirements.

### Tools
- **Framework**: JMeter or Gatling
- **Metrics**: Response time, throughput, error rate

### Test Scenarios
1. Load Test: 100 concurrent users
2. Stress Test: Gradually increase load
3. Spike Test: Sudden traffic increase
4. Endurance Test: Sustained load over time

## Test Data Management

### Test Data Builders

```java
public class OrderTestData {

    public static OrderRequest createValidRequest() {
        return OrderRequest.builder()
            .userId(100L)
            .items(List.of(createOrderItem()))
            .build();
    }

    public static OrderItemDto createOrderItem() {
        return OrderItemDto.builder()
            .productId(1L)
            .quantity(2)
            .price(new BigDecimal("29.99"))
            .build();
    }

    public static Order createPendingOrder() {
        return Order.builder()
            .id(1L)
            .orderNumber("ORD-2024-001")
            .userId(100L)
            .status(OrderStatus.PENDING)
            .totalAmount(new BigDecimal("59.98"))
            .build();
    }
}
```

## CI/CD Integration

### GitHub Actions Workflow

```yaml
name: Test Suite

on: [push, pull_request]

jobs:
  unit-tests:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - name: Set up JDK 21
        uses: actions/setup-java@v3
        with:
          java-version: '21'
      - name: Run unit tests
        run: mvn test

  integration-tests:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - name: Run integration tests
        run: mvn verify -Pintegration-tests

  coverage:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - name: Generate coverage report
        run: mvn jacoco:report
      - name: Upload to Codecov
        uses: codecov/codecov-action@v3
```

## Test Execution Strategy

### Local Development
```bash
# Run unit tests only
mvn test

# Run integration tests
mvn verify -Pintegration-tests

# Run all tests with coverage
mvn clean verify jacoco:report
```

### CI Pipeline
1. Pull Request: Run unit + integration tests
2. Merge to main: Run full test suite including E2E
3. Nightly: Run performance tests

## Test Maintenance

### Code Review Checklist
- [ ] New features have corresponding tests
- [ ] Tests follow naming conventions
- [ ] No hardcoded values (use constants or test data builders)
- [ ] Tests are independent and can run in any order
- [ ] Cleanup is performed after tests (@AfterEach)
- [ ] Test coverage meets minimum threshold

### Red Flags
- Tests that are commented out
- Tests with `@Disabled` without reason
- Flaky tests that pass/fail intermittently
- Tests that take more than 5 seconds

## Glossary

- **TDD**: Test-Driven Development
- **BDD**: Behavior-Driven Development
- **AAA**: Arrange-Act-Assert pattern
- **SUT**: System Under Test
- **Mock**: Simulated object that mimics behavior
- **Stub**: Minimal implementation for testing

## Resources

- [JUnit 5 User Guide](https://junit.org/junit5/docs/current/user-guide/)
- [Testcontainers Documentation](https://www.testcontainers.org/)
- [AssertJ Documentation](https://assertj.github.io/doc/)
