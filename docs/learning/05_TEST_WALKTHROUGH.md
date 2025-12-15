# Step-by-Step Test Walkthrough

Let's walk through ONE test from start to finish, explaining EVERY single line.

---

## The Test File: OrderServiceImplTest.java

### Part 1: The Setup (Class Level)

```java
package com.ecommerce.order.service;
// ↑ This test is in the same package structure as the class it tests
// Real class: src/main/java/com/ecommerce/order/service/OrderServiceImpl.java
// Test class: src/test/java/com/ecommerce/order/service/OrderServiceImplTest.java

import org.junit.jupiter.api.Test;
// ↑ Import the @Test annotation (marks methods as tests)

import org.junit.jupiter.api.BeforeEach;
// ↑ Import @BeforeEach (runs setup before each test)

import org.junit.jupiter.api.extension.ExtendWith;
// ↑ Import @ExtendWith (enables extensions like Mockito)

import org.mockito.Mock;
// ↑ Import @Mock annotation (creates fake objects)

import org.mockito.InjectMocks;
// ↑ Import @InjectMocks (injects mocks into real objects)

import org.mockito.junit.jupiter.MockitoExtension;
// ↑ The Mockito extension for JUnit 5

import static org.junit.jupiter.api.Assertions.*;
// ↑ Import all assertion methods (assertEquals, assertNotNull, etc.)

import static org.mockito.Mockito.*;
// ↑ Import all Mockito methods (when, verify, any, times, etc.)
```

**Key Point:** All the `import static` statements let you write `assertEquals(...)` instead of `Assertions.assertEquals(...)`. It's just shorter.

---

### Part 2: Class Declaration

```java
@ExtendWith(MockitoExtension.class)
// ↑ THIS IS CRITICAL!
// This tells JUnit: "Hey, this test uses Mockito"
// Without this, @Mock and @InjectMocks won't work

@DisplayName("OrderServiceImpl Unit Tests")
// ↑ Optional: Gives a nice name when tests run
// In test output you'll see "OrderServiceImpl Unit Tests" instead of just the class name

class OrderServiceImplTest {
    // ↑ Test class name = ClassBeingTested + "Test"
    // We're testing: OrderServiceImpl
    // So test class is: OrderServiceImplTest
```

---

### Part 3: Mock Objects

```java
    @Mock
    private OrderRepository orderRepository;
    // ↑ This creates a FAKE OrderRepository
    //
    // What does this mean?
    // - It's not a real repository (no database connection)
    // - It's a "mock" - a pretend object
    // - You tell it what to do with when() statements
    // - It tracks what methods are called on it
    //
    // Why do we need this?
    // - OrderServiceImpl needs an OrderRepository to work
    // - But we don't want to test the database (too slow, too complex)
    // - We only want to test the SERVICE LOGIC
    // - So we give it a fake repository that we control
```

```java
    @InjectMocks
    private OrderServiceImpl orderService;
    // ↑ This creates a REAL OrderServiceImpl
    //
    // What does @InjectMocks do?
    // 1. Creates new OrderServiceImpl()
    // 2. Looks at what OrderServiceImpl needs (it needs OrderRepository)
    // 3. Automatically injects the @Mock orderRepository into it
    //
    // It's like:
    // orderService = new OrderServiceImpl(orderRepository);
    //                                     ↑ the mock gets injected here
    //
    // Now orderService is REAL, but uses a FAKE repository
```

---

### Part 4: Test Data Setup

```java
    private Order testOrder;
    private OrderRequest testOrderRequest;
    private UUID testOrderId;
    // ↑ These are instance variables
    // They'll be used by multiple tests
    // We set them up in @BeforeEach method
```

```java
    @BeforeEach
    void setUp() {
        // ↑ This method runs BEFORE EACH test
        //
        // If you have 12 tests, this runs 12 times
        // Each test gets fresh, clean data
        //
        // Why?
        // - Tests should be independent
        // - Test 1 shouldn't affect Test 2
        // - Fresh data for each test ensures this
```

```java
        testOrderId = UUID.randomUUID();
        // ↑ Create a random UUID for testing
        // Example: "550e8400-e29b-41d4-a716-446655440000"
```

```java
        testOrder = new Order();
        testOrder.setOrderId(testOrderId);
        testOrder.setCustomerName("John Doe");
        testOrder.setEmail("john@example.com");
        testOrder.setTotalAmount(new BigDecimal("1049.99"));
        testOrder.setStatus(Order.OrderStatus.PENDING);
        // ↑ Create a fake Order with test data
        // This is what we'll pretend the repository returns
```

```java
        testOrderRequest = new OrderRequest();
        testOrderRequest.setCustomerName("John Doe");
        testOrderRequest.setEmail("john@example.com");
        // ↑ Create a fake request
        // This is what would come from the REST API
    }
```

---

### Part 5: A Complete Test - Line by Line

```java
    @Test
    // ↑ THIS IS A TEST METHOD
    // JUnit will run this method automatically when you do "mvn test"

    @DisplayName("Should create order successfully")
    // ↑ Optional: Nice description shown in test output

    void testCreateOrder_Success() {
        // ↑ Test method name
        // Convention: test + MethodName + Scenario
        // Examples:
        // - testCreateOrder_Success (happy path)
        // - testCreateOrder_InvalidEmail (error case)
        // - testGetOrder_NotFound (error case)
```

#### ARRANGE Phase

```java
        // ============ ARRANGE ============
        // This is where you set up everything the test needs
```

```java
        when(orderRepository.save(any(Order.class)))
            .thenReturn(testOrder);
        // ↑ THIS IS THE MOST IMPORTANT LINE!
        //
        // Let's break it down:
        //
        // when(orderRepository.save(any(Order.class)))
        //      ↑ "When someone calls save() on the mock repository"
        //                         ↑ "with ANY Order object as the parameter"
        //
        // .thenReturn(testOrder);
        //  ↑ "Then return this testOrder object"
        //
        // In plain English:
        // "Hey mock repository, when save() is called with any Order,
        //  don't actually save to database, just return testOrder"
        //
        // Why any(Order.class)?
        // - We don't care about the exact Order object passed to save()
        // - We're testing the service logic, not what Order looks like
        // - any(Order.class) means "match any Order object"
        //
        // Alternative: You could be specific:
        // when(orderRepository.save(eq(specificOrder))).thenReturn(...)
```

```java
        when(orderRepository.existsByOrderNumber(anyString()))
            .thenReturn(false);
        // ↑ Another mock setup
        // "When existsByOrderNumber() is called with ANY string,
        //  return false (order number doesn't exist yet)"
        //
        // Why false?
        // - The service generates a unique order number
        // - It checks if it already exists
        // - We want it to NOT exist (so it doesn't try to generate a new one)
```

#### ACT Phase

```java
        // ============ ACT ============
        // This is where you actually call the method you're testing
```

```java
        OrderResponse response = orderService.createOrder(testOrderRequest);
        // ↑ Call the REAL method on the REAL service
        //
        // What happens inside createOrder()?
        // 1. Service creates an Order object
        // 2. Service calls orderRepository.save(order)
        // 3. But orderRepository is a MOCK!
        // 4. So instead of saving to database, it returns testOrder
        // 5. Service converts testOrder to OrderResponse
        // 6. Service returns the OrderResponse
        //
        // We get back the OrderResponse and store it in 'response'
```

#### ASSERT Phase

```java
        // ============ ASSERT ============
        // This is where you check if everything worked correctly
```

```java
        assertNotNull(response);
        // ↑ Check that response is not null
        //
        // Translation: "The response should exist"
        //
        // If response is null, this test FAILS
        // The test output will say:
        // "Expected: not null, but was: null"
```

```java
        assertEquals("John Doe", response.getCustomerName());
        // ↑ Check that customer name is correct
        //
        // assertEquals(expected, actual)
        //              ↑         ↑
        //              what it   what we
        //              should be actually got
        //
        // Translation: "Customer name should be 'John Doe'"
        //
        // If they don't match, test FAILS with:
        // "Expected: John Doe, but was: Jane Doe"
```

```java
        assertEquals("john@example.com", response.getEmail());
        // ↑ Check that email is correct
```

```java
        assertEquals(Order.OrderStatus.PENDING, response.getStatus());
        // ↑ Check that status is PENDING (new orders start as PENDING)
```

```java
        assertEquals(2, response.getOrderItems().size());
        // ↑ Check that there are 2 order items
        // (testOrderRequest had 2 items)
```

#### VERIFY Phase

```java
        verify(orderRepository, times(1)).save(any(Order.class));
        // ↑ Verify that save() was called exactly once
        //
        // Let's break it down:
        //
        // verify(orderRepository, times(1))
        //        ↑                ↑
        //        which mock?      how many times?
        //
        // .save(any(Order.class));
        //  ↑
        //  which method?
        //
        // Translation:
        // "Check that save() was called exactly 1 time on orderRepository"
        //
        // Why is this important?
        // - Ensures the service actually tried to save the order
        // - If the service has a bug and doesn't call save(), this catches it
        //
        // Other options:
        // verify(mock, never()).method()       // Should never be called
        // verify(mock, atLeast(2)).method()    // Called at least 2 times
        // verify(mock, atMost(3)).method()     // Called at most 3 times
    }
```

---

## Part 6: What Actually Happens When You Run This Test?

Let's trace through the execution:

```
1. JUnit starts
   └─ Finds OrderServiceImplTest.java
      └─ Finds @Test methods
```

```
2. For testCreateOrder_Success():

   Step 1: Run @BeforeEach setUp()
   └─ testOrder created
   └─ testOrderRequest created

   Step 2: Create mocks (@Mock)
   └─ orderRepository = fake repository created

   Step 3: Create real object (@InjectMocks)
   └─ orderService = new OrderServiceImpl(orderRepository)
                                          ↑ mock injected

   Step 4: Run test method
   └─ when(...).thenReturn(...) executed
      ↳ Mock is now programmed with behavior

   └─ orderService.createOrder(...) called
      ↳ Service runs real code
      ↳ Service calls orderRepository.save(...)
      ↳ Mock intercepts the call
      ↳ Mock returns testOrder (as programmed)
      ↳ Service continues and returns response

   └─ assertEquals(...) executes
      ↳ Checks if response.getCustomerName() == "John Doe"
      ↳ ✓ PASS

   └─ verify(...) executes
      ↳ Checks if save() was called
      ↳ ✓ PASS (it was called once)

   Step 5: Test complete
   └─ Result: PASS ✓
```

---

## Part 7: What If the Test Fails?

### Scenario 1: Assertion Fails

```java
assertEquals("John Doe", response.getCustomerName());
// But response.getCustomerName() returns "Jane Doe"
```

**Output:**
```
testCreateOrder_Success  FAILED
org.opentest4j.AssertionFailedError:
Expected :John Doe
Actual   :Jane Doe
    at OrderServiceImplTest.testCreateOrder_Success(OrderServiceImplTest.java:75)
```

**What this tells you:**
- Line 75 in the test failed
- Expected "John Doe" but got "Jane Doe"
- There's a bug in the code (maybe setting the wrong customer name)

### Scenario 2: Verification Fails

```java
verify(orderRepository, times(1)).save(any(Order.class));
// But save() was never called
```

**Output:**
```
testCreateOrder_Success  FAILED
Wanted but not invoked:
orderRepository.save(<any>);

Actually, there were zero interactions with this mock.
```

**What this tells you:**
- save() was supposed to be called, but wasn't
- There's a bug (service didn't try to save the order)

### Scenario 3: Exception Thrown

```java
OrderResponse response = orderService.createOrder(testOrderRequest);
// But createOrder() throws NullPointerException
```

**Output:**
```
testCreateOrder_Success  ERROR
java.lang.NullPointerException: Cannot invoke "setCustomerName" on null object
    at OrderServiceImpl.createOrder(OrderServiceImpl.java:45)
```

**What this tells you:**
- Test crashed with NullPointerException
- Line 45 in OrderServiceImpl has the problem
- Probably trying to call a method on a null object

---

## Part 8: Testing Error Scenarios

Not all tests are about happy paths. Let's see an error test:

```java
    @Test
    @DisplayName("Should throw exception when order not found by ID")
    void testGetOrderById_NotFound() {
        // ============ ARRANGE ============
        UUID nonExistentId = UUID.randomUUID();
        // ↑ Create a random ID that doesn't exist
```

```java
        when(orderRepository.findById(nonExistentId))
            .thenReturn(Optional.empty());
        // ↑ Tell the mock: "When findById() is called with this ID,
        //                   return empty (nothing found)"
        //
        // Optional.empty() = "nothing found"
        // Optional.of(order) = "found this order"
```

```java
        // ============ ACT & ASSERT ============
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            orderService.getOrderById(nonExistentId);
        });
        // ↑ This is special!
        //
        // assertThrows(ExceptionType.class, () -> { code })
        //              ↑                    ↑
        //              what exception       code that should
        //              should be thrown     throw it
        //
        // Translation:
        // "Run this code, and it SHOULD throw a RuntimeException"
        //
        // If it throws RuntimeException: PASS ✓
        // If it doesn't throw: FAIL ✗
        // If it throws different exception: FAIL ✗
```

```java
        assertTrue(exception.getMessage().contains("Order not found"));
        // ↑ Check that the exception message contains "Order not found"
        // Makes sure it's the RIGHT exception with the RIGHT message
```

```java
        verify(orderRepository, times(1)).findById(nonExistentId);
        // ↑ Verify findById() was called once
    }
```

---

## Part 9: Controller Test Example

Controller tests are different - they test HTTP!

```java
@Test
void testCreateOrder_Success() throws Exception {
    // ↑ "throws Exception" is needed for MockMvc
```

```java
    // ARRANGE
    when(orderService.createOrder(any(OrderRequest.class)))
        .thenReturn(testOrderResponse);
    // ↑ Mock the SERVICE (not repository)
    // Controller tests mock the service layer
```

```java
    // ACT & ASSERT
    mockMvc.perform(
        // ↑ mockMvc = simulates HTTP requests

        post("/api/v1/orders")
        // ↑ HTTP POST to this URL

        .contentType(MediaType.APPLICATION_JSON)
        // ↑ Set Content-Type header to application/json

        .content(objectMapper.writeValueAsString(testOrderRequest))
        // ↑ Convert testOrderRequest to JSON string
        // Example: {"customerName":"John","email":"john@example.com"}
    )
```

```java
    .andExpect(status().isCreated())
    // ↑ Expect HTTP status 201 (Created)
    //
    // Other options:
    // status().isOk() → 200
    // status().isNoContent() → 204
    // status().isBadRequest() → 400
    // status().isNotFound() → 404
```

```java
    .andExpect(jsonPath("$.orderId").value(testOrderId.toString()))
    // ↑ Check JSON response
    //
    // jsonPath("$.orderId")
    //           ↑
    //           $ = root of JSON
    //           .orderId = field named "orderId"
    //
    // Example JSON:
    // {
    //   "orderId": "123-456",    ← This value
    //   "customerName": "John"
    // }
    //
    // .value(...) checks if it equals the given value
```

```java
    .andExpect(jsonPath("$.customerName").value("John Doe"))
    // ↑ Check that customerName field equals "John Doe"
```

```java
    .andExpect(jsonPath("$.orderItems").isArray())
    // ↑ Check that orderItems is an array
```

```java
    .andExpect(jsonPath("$.orderItems", hasSize(2)));
    // ↑ Check that the array has exactly 2 elements
```

```java
    verify(orderService, times(1)).createOrder(any(OrderRequest.class));
    // ↑ Verify the controller actually called the service
}
```

---

## Part 10: Common Questions

### Q: Why do we need both Service tests AND Controller tests?

**A:** They test different things!

**Service Tests:**
- Test business logic
- "Does calculateTotal() work correctly?"
- "Does it throw exception when order not found?"
- No HTTP, no JSON, just Java code

**Controller Tests:**
- Test HTTP layer
- "Does POST /orders return 201?"
- "Is the JSON response correct?"
- "Does validation work?"
- No business logic (that's mocked)

### Q: What's the difference between @Mock and @InjectMocks?

**A:**

```java
@Mock
private OrderRepository repository;  // FAKE object (you control it)

@InjectMocks
private OrderServiceImpl service;    // REAL object (with fakes injected)
```

Think of it like:
- @Mock = prop gun (fake, you control when it "fires")
- @InjectMocks = real actor (but using prop guns, not real ones)

### Q: What does when(...).thenReturn(...) actually do?

**A:** It programs the mock's behavior.

```java
when(repository.findById(id)).thenReturn(order);
```

It's like saying:
"Dear mock repository, I'm teaching you what to do.
 When someone calls findById(id), return this order.
 Don't go to the database, don't do anything real,
 just return what I told you to return."

### Q: Why use verify()?

**A:** To make sure methods were actually called.

```java
verify(repository).save(any(Order.class));
```

Imagine if the service has this bug:
```java
public Order createOrder(OrderRequest request) {
    Order order = new Order();
    order.setCustomerName(request.getCustomerName());
    // BUG: Forgot to call repository.save(order)!
    return order;  // Returns unsaved order
}
```

Your assertions might pass (order looks correct), but the bug is that it's never saved!

`verify()` catches this:
```
Wanted but not invoked:
repository.save(<any>);
```

---

## Summary

**A Test Has 4 Parts:**

1. **ARRANGE** - Set up test data and mock behavior
   ```java
   when(mock.method()).thenReturn(value);
   ```

2. **ACT** - Call the method you're testing
   ```java
   result = service.method(input);
   ```

3. **ASSERT** - Check the result is correct
   ```java
   assertEquals(expected, result);
   ```

4. **VERIFY** - Check that methods were called
   ```java
   verify(mock).method();
   ```

**Key Concepts:**
- **Mock** = Fake object you control
- **when()** = Program mock behavior
- **verify()** = Check mock was used correctly
- **assertEquals()** = Check values match
- **assertThrows()** = Check exception was thrown

**Now you know how tests work!** 🎉

Want to try writing a test yourself? Or have questions about any part?
