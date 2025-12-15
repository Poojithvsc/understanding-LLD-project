# Unit Testing Explained - From Zero to Hero

## What is Testing and Why Do We Need It?

### The Problem Without Tests

Imagine you built a calculator app. How do you know it works correctly?

**Without tests:**
```
You manually type: 2 + 2
You check: Does it show 4? ✓
You manually type: 10 - 5
You check: Does it show 5? ✓
You manually type: 3 * 4
You check: Does it show 12? ✓
```

**Problems:**
1. Takes forever to test everything manually
2. Every time you change code, you have to test EVERYTHING again
3. You might forget to test some cases
4. Boring and error-prone

**With automated tests:**
```java
@Test
void testAddition() {
    assertEquals(4, calculator.add(2, 2));
}

@Test
void testSubtraction() {
    assertEquals(5, calculator.subtract(10, 5));
}

@Test
void testMultiplication() {
    assertEquals(12, calculator.multiply(3, 4));
}
```

Run once: `mvn test` - All tests run automatically in seconds! ✅

---

## Part 1: Understanding the Basics

### 1.1 What is JUnit 5?

JUnit is a testing framework - think of it as a tool that helps you write and run tests.

**Key Components:**

```java
import org.junit.jupiter.api.Test;  // ← Import the Test annotation
import static org.junit.jupiter.api.Assertions.*;  // ← Import assertion methods

class CalculatorTest {  // ← Test class (ends with "Test")

    @Test  // ← This annotation says "this is a test method"
    void testAddition() {  // ← Test method (starts with "test")
        // Your test code here
    }
}
```

### 1.2 The AAA Pattern (Arrange-Act-Assert)

Every test follows this pattern:

```java
@Test
void testCreateOrder() {
    // ARRANGE: Set up test data
    String customerName = "John Doe";
    String email = "john@example.com";

    // ACT: Call the method you're testing
    Order order = orderService.createOrder(customerName, email);

    // ASSERT: Verify the result is correct
    assertEquals("John Doe", order.getCustomerName());
    assertEquals("john@example.com", order.getEmail());
}
```

**Think of it like cooking:**
- **Arrange**: Get ingredients ready (flour, eggs, sugar)
- **Act**: Bake the cake
- **Assert**: Taste it - is it good? ✓

### 1.3 Common Assertion Methods

```java
// Check if two values are equal
assertEquals(expected, actual);
assertEquals(4, calculator.add(2, 2));  // Is 2+2 equal to 4?

// Check if something is true or false
assertTrue(order.isValid());   // Should be true
assertFalse(order.isEmpty());  // Should be false

// Check if something is null or not null
assertNotNull(order);  // Order should exist
assertNull(error);     // Error should be null (no error)

// Check if exception is thrown
assertThrows(RuntimeException.class, () -> {
    orderService.getOrder(invalidId);  // This should throw exception
});
```

---

## Part 2: What is Mockito and Why Do We Need It?

### The Problem: Testing in Isolation

Let's say you want to test `OrderService`, but it depends on `OrderRepository` (which talks to the database).

**Problem:**
```java
class OrderServiceImpl {
    private OrderRepository repository;  // ← Depends on database!

    public Order createOrder(OrderRequest request) {
        Order order = new Order();
        // ... set order fields ...
        return repository.save(order);  // ← Saves to database
    }
}
```

If you test `OrderService`:
- You need a real database running
- Tests are slow (database is slow)
- Tests can fail if database is down
- You're testing TWO things (service + database)

**We want to test ONLY the service logic, not the database!**

### The Solution: Mocking

A **mock** is a fake object that pretends to be the real thing.

```java
@Mock
private OrderRepository mockRepository;  // ← Fake repository (no real database!)

@Test
void testCreateOrder() {
    // Tell the mock what to do
    when(mockRepository.save(any(Order.class)))
        .thenReturn(fakeOrder);  // ← When save() is called, return this fake order

    // Now test the service
    Order result = orderService.createOrder(request);

    // The service called save(), but no real database was used!
}
```

**Think of it like movie props:**
- Real gun in a movie = dangerous
- Fake prop gun = safe, looks real, makes bang sound
- Mock object = fake object, looks real, does what you tell it

---

## Part 3: Let's Break Down One Test - Line by Line

Let's look at a real test from `OrderServiceImplTest.java`:

```java
@ExtendWith(MockitoExtension.class)  // ← Enable Mockito
@DisplayName("OrderServiceImpl Unit Tests")
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;  // ← Create fake repository

    @InjectMocks
    private OrderServiceImpl orderService;  // ← Create real service, inject mock into it

    @Test
    @DisplayName("Should create order successfully")
    void testCreateOrder_Success() {
        // ============ ARRANGE ============
        // 1. Create test data
        OrderRequest request = new OrderRequest();
        request.setCustomerName("John Doe");
        request.setEmail("john@example.com");

        Order fakeOrder = new Order();
        fakeOrder.setOrderId(UUID.randomUUID());
        fakeOrder.setCustomerName("John Doe");

        // 2. Tell the mock what to do when save() is called
        when(orderRepository.save(any(Order.class)))
            .thenReturn(fakeOrder);

        // ============ ACT ============
        // 3. Call the method we're testing
        OrderResponse response = orderService.createOrder(request);

        // ============ ASSERT ============
        // 4. Check if the result is correct
        assertNotNull(response);  // Response should exist
        assertEquals("John Doe", response.getCustomerName());  // Name should match

        // 5. Verify that save() was called exactly once
        verify(orderRepository, times(1)).save(any(Order.class));
    }
}
```

### Let's Break It Down:

#### Step 1: The Annotations

```java
@ExtendWith(MockitoExtension.class)
```
- This enables Mockito in your test
- Like turning on the power for your testing tools

```java
@Mock
private OrderRepository orderRepository;
```
- Creates a **fake** OrderRepository
- No real database connection
- You control what it does

```java
@InjectMocks
private OrderServiceImpl orderService;
```
- Creates a **real** OrderServiceImpl
- Automatically injects the mock repository into it
- Like building a car with a fake engine for testing

#### Step 2: The when() Statement

```java
when(orderRepository.save(any(Order.class)))
    .thenReturn(fakeOrder);
```

**Translation to English:**
- "When someone calls `save()` on the mock repository"
- "With ANY Order object as parameter"
- "Then return this fake order"

**Why?**
- The real `save()` would save to database and return the saved order
- The mock just returns what you tell it to
- This way you can test the service logic without a real database

#### Step 3: The verify() Statement

```java
verify(orderRepository, times(1)).save(any(Order.class));
```

**Translation to English:**
- "Check that `save()` was called exactly 1 time"
- "On the orderRepository mock"
- "With any Order object as parameter"

**Why?**
- Makes sure the service actually called save()
- If the service has a bug and doesn't call save(), this test would catch it

---

## Part 4: Different Types of Tests

### 4.1 Unit Tests (Service Layer)

Tests **business logic** in isolation.

```java
@Test
void testCalculateTotalAmount() {
    // ARRANGE
    OrderItem item1 = new OrderItem();
    item1.setPrice(new BigDecimal("100.00"));
    item1.setQuantity(2);  // 2 * 100 = 200

    OrderItem item2 = new OrderItem();
    item2.setPrice(new BigDecimal("50.00"));
    item2.setQuantity(3);  // 3 * 50 = 150

    List<OrderItem> items = List.of(item1, item2);

    // ACT
    BigDecimal total = orderService.calculateTotal(items);

    // ASSERT
    assertEquals(new BigDecimal("350.00"), total);  // 200 + 150 = 350
}
```

**What are we testing?**
- Does the service correctly calculate: (quantity × price) for each item?
- Does it add them up correctly?
- **NOT testing**: Database, HTTP requests, etc.

### 4.2 Integration Tests (Controller Layer)

Tests **HTTP endpoints** (REST API).

```java
@Test
void testCreateOrderEndpoint() throws Exception {
    // ARRANGE
    OrderRequest request = new OrderRequest();
    request.setCustomerName("John Doe");
    request.setEmail("john@example.com");

    OrderResponse mockResponse = new OrderResponse(...);
    when(orderService.createOrder(any())).thenReturn(mockResponse);

    // ACT & ASSERT
    mockMvc.perform(post("/api/v1/orders")  // ← Fake HTTP POST request
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())  // ← Expect HTTP 201
            .andExpect(jsonPath("$.customerName").value("John Doe"));  // ← Check JSON
}
```

**What are we testing?**
- Does POST /api/v1/orders work?
- Does it return HTTP 201 (Created)?
- Does the JSON response have the right data?
- **NOT testing**: Real service logic (that's mocked)

---

## Part 5: Common Testing Scenarios

### Scenario 1: Testing Happy Path (Everything Works)

```java
@Test
void testGetOrderById_Success() {
    // ARRANGE
    UUID orderId = UUID.randomUUID();
    Order mockOrder = new Order();
    mockOrder.setOrderId(orderId);

    when(orderRepository.findById(orderId))
        .thenReturn(Optional.of(mockOrder));  // ← Return the order (it exists)

    // ACT
    Order result = orderService.getOrderById(orderId);

    // ASSERT
    assertNotNull(result);
    assertEquals(orderId, result.getOrderId());
}
```

### Scenario 2: Testing Error Case (Something Goes Wrong)

```java
@Test
void testGetOrderById_NotFound() {
    // ARRANGE
    UUID orderId = UUID.randomUUID();

    when(orderRepository.findById(orderId))
        .thenReturn(Optional.empty());  // ← Return empty (order doesn't exist)

    // ACT & ASSERT
    assertThrows(RuntimeException.class, () -> {
        orderService.getOrderById(orderId);  // ← This should throw exception
    });
}
```

### Scenario 3: Testing Validation

```java
@Test
void testCreateOrder_InvalidEmail() throws Exception {
    // ARRANGE
    OrderRequest request = new OrderRequest();
    request.setEmail("invalid-email");  // ← Not a valid email format

    // ACT & ASSERT
    mockMvc.perform(post("/api/v1/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());  // ← Expect HTTP 400
}
```

---

## Part 6: Understanding MockMvc (HTTP Testing)

MockMvc simulates HTTP requests **without starting a real server**.

### Basic Structure:

```java
mockMvc.perform(...)      // Send HTTP request
       .andExpect(...)    // Check response
       .andExpect(...);   // Check more things
```

### Examples:

#### GET Request:
```java
mockMvc.perform(get("/api/v1/orders/{id}", orderId))
       .andExpect(status().isOk())  // HTTP 200
       .andExpect(jsonPath("$.orderId").value(orderId.toString()));
```

#### POST Request:
```java
mockMvc.perform(post("/api/v1/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"customerName\":\"John\"}"))
       .andExpect(status().isCreated());  // HTTP 201
```

#### DELETE Request:
```java
mockMvc.perform(delete("/api/v1/orders/{id}", orderId))
       .andExpect(status().isNoContent());  // HTTP 204
```

### Understanding JSON Path:

```json
{
  "orderId": "123",
  "customerName": "John Doe",
  "orderItems": [
    {"productName": "Laptop", "price": 999.99},
    {"productName": "Mouse", "price": 25.00}
  ]
}
```

Check values in JSON response:
```java
.andExpect(jsonPath("$.orderId").value("123"))
.andExpect(jsonPath("$.customerName").value("John Doe"))
.andExpect(jsonPath("$.orderItems").isArray())
.andExpect(jsonPath("$.orderItems", hasSize(2)))
.andExpect(jsonPath("$.orderItems[0].productName").value("Laptop"))
```

---

## Part 7: Test Lifecycle (@BeforeEach)

You don't want to copy-paste test data in every test. Use `@BeforeEach`:

```java
class OrderServiceImplTest {

    private Order testOrder;
    private OrderRequest testRequest;

    @BeforeEach  // ← Runs BEFORE each test
    void setUp() {
        // Create test data once
        testOrder = new Order();
        testOrder.setOrderId(UUID.randomUUID());
        testOrder.setCustomerName("John Doe");

        testRequest = new OrderRequest();
        testRequest.setCustomerName("John Doe");
        testRequest.setEmail("john@example.com");
    }

    @Test
    void test1() {
        // testOrder and testRequest are already set up!
        // Use them here
    }

    @Test
    void test2() {
        // Fresh testOrder and testRequest are set up again!
        // Each test gets clean data
    }
}
```

**Lifecycle:**
1. Run `setUp()` → Test 1 runs → Cleanup
2. Run `setUp()` → Test 2 runs → Cleanup
3. ...

---

## Part 8: Reading the Test Output

When you run `mvn test`, you see:

```
[INFO] Running com.ecommerce.order.service.OrderServiceImplTest
[INFO] Tests run: 12, Failures: 0, Errors: 0, Skipped: 0
```

**What it means:**
- **Tests run: 12** - 12 tests were executed
- **Failures: 0** - All assertions passed ✓
- **Errors: 0** - No exceptions or crashes ✓
- **Skipped: 0** - All tests ran (none were skipped)

If a test fails:
```
[ERROR] testCreateOrder_Success  Time elapsed: 0.1 s  <<< FAILURE!
Expected: "John Doe"
Actual:   "Jane Doe"
```

**This tells you:**
- Which test failed: `testCreateOrder_Success`
- What was wrong: Expected "John Doe" but got "Jane Doe"
- Now you can debug the code!

---

## Part 9: Why Each Test Exists

Let me explain WHY we wrote each test:

### OrderServiceImplTest (Service Layer)

| Test | Why? |
|------|------|
| `testCreateOrder_Success` | Make sure creating an order works correctly |
| `testGetOrderById_Success` | Make sure we can retrieve an order that exists |
| `testGetOrderById_NotFound` | Make sure we handle missing orders properly (throw exception) |
| `testGetAllOrders_Success` | Make sure we can get multiple orders |
| `testUpdateOrder_Success` | Make sure updating an order works |
| `testUpdateOrder_NotFound` | Make sure we can't update an order that doesn't exist |
| `testDeleteOrder_Success` | Make sure deleting works |
| `testDeleteOrder_NotFound` | Make sure we can't delete an order that doesn't exist |
| `testGetOrderByOrderNumber_Success` | Make sure we can find by order number |
| `testGetOrdersByEmail_Success` | Make sure we can find all orders for a customer |
| `testUpdateOrderStatus_Success` | Make sure changing status works |
| `testUpdateOrderStatus_NotFound` | Make sure we can't change status of non-existent order |

### OrderControllerTest (REST API)

| Test | Why? |
|------|------|
| `testCreateOrder_Success` | POST endpoint returns 201 with correct JSON |
| `testGetOrderById_Success` | GET by ID returns 200 with order |
| `testGetOrderById_NotFound` | GET by ID returns error for missing order |
| `testGetAllOrders_Success` | GET all returns array of orders |
| `testUpdateOrder_Success` | PUT updates and returns 200 |
| `testDeleteOrder_Success` | DELETE returns 204 (no content) |
| `testGetOrderByOrderNumber_Success` | GET by order number works |
| `testGetOrdersByEmail_Success` | GET by email query param works |
| `testUpdateOrderStatus_Success` | PATCH status update works |
| `testCreateOrder_ValidationFailure` | Invalid email returns 400 |
| `testGetAllOrders_EmptyList` | Returns empty array when no orders |
| `testGetOrdersByEmail_EmptyList` | Returns empty array when customer has no orders |

---

## Part 10: Quick Reference Cheat Sheet

### JUnit Annotations:
```java
@Test                    // This is a test method
@BeforeEach              // Run before each test
@AfterEach               // Run after each test
@DisplayName("...")      // Human-readable test name
```

### Mockito Annotations:
```java
@Mock                    // Create a mock object
@InjectMocks            // Create real object with mocks injected
@ExtendWith(MockitoExtension.class)  // Enable Mockito
```

### Mockito Methods:
```java
when(mock.method()).thenReturn(value);  // Set up mock behavior
verify(mock).method();                   // Verify method was called
verify(mock, times(2)).method();        // Verify called exactly 2 times
verify(mock, never()).method();         // Verify never called
any(Class.class)                        // Match any argument
```

### Assertions:
```java
assertEquals(expected, actual);     // Values equal?
assertNotNull(object);             // Not null?
assertTrue(condition);             // True?
assertFalse(condition);            // False?
assertThrows(Exception.class, () -> {...});  // Throws exception?
```

### MockMvc:
```java
mockMvc.perform(get("/path"))           // GET request
       .perform(post("/path")           // POST request
                .content("..."))
       .andExpect(status().isOk())      // HTTP 200
       .andExpect(status().isCreated()) // HTTP 201
       .andExpect(jsonPath("$.field").value("value"));
```

---

## Summary

**Unit Testing = Automated quality checks for your code**

1. **JUnit 5** - The testing framework
2. **Mockito** - Create fake objects to test in isolation
3. **MockMvc** - Test REST APIs without starting a server
4. **AAA Pattern** - Arrange, Act, Assert (structure of every test)

**Next:** Try writing a test yourself! Or ask me to explain any specific part in more detail.
