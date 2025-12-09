# API Standards & Guidelines

## Overview

This document defines the API design standards for all microservices in the E-Commerce Order Processing System.

## REST API Design Principles

### 1. Resource Naming Conventions

**Use nouns, not verbs**
```
✅ GET /api/v1/orders
❌ GET /api/v1/getOrders

✅ POST /api/v1/orders
❌ POST /api/v1/createOrder
```

**Use plural nouns for collections**
```
✅ GET /api/v1/orders
❌ GET /api/v1/order
```

**Use kebab-case for multi-word resources**
```
✅ GET /api/v1/order-items
❌ GET /api/v1/orderItems
❌ GET /api/v1/order_items
```

### 2. HTTP Methods

| Method | Purpose | Idempotent | Safe |
|--------|---------|------------|------|
| GET | Retrieve resource(s) | Yes | Yes |
| POST | Create new resource | No | No |
| PUT | Update entire resource | Yes | No |
| PATCH | Partial update | No | No |
| DELETE | Remove resource | Yes | No |

### 3. URL Structure

```
https://{host}/api/{version}/{resource}/{id}/{sub-resource}

Examples:
GET /api/v1/orders/123
GET /api/v1/orders/123/items
POST /api/v1/orders
PUT /api/v1/orders/123
DELETE /api/v1/orders/123
```

### 4. Versioning

**Use URL versioning**
```
/api/v1/orders
/api/v2/orders
```

Major version in URL, minor changes backward compatible.

### 5. HTTP Status Codes

#### Success Codes
- **200 OK**: Successful GET, PUT, PATCH, DELETE
- **201 Created**: Successful POST (return created resource)
- **204 No Content**: Successful DELETE (no response body)

#### Client Error Codes
- **400 Bad Request**: Invalid request body/parameters
- **401 Unauthorized**: Missing or invalid authentication
- **403 Forbidden**: Authenticated but not authorized
- **404 Not Found**: Resource doesn't exist
- **409 Conflict**: Resource conflict (e.g., duplicate)
- **422 Unprocessable Entity**: Validation errors

#### Server Error Codes
- **500 Internal Server Error**: Unexpected server error
- **503 Service Unavailable**: Service temporarily down

### 6. Request/Response Format

#### Request Body (JSON)
```json
{
  "userId": 12345,
  "items": [
    {
      "productId": 1,
      "quantity": 2,
      "price": 29.99
    }
  ]
}
```

**Standards:**
- Use camelCase for field names
- Include required fields validation
- Use appropriate data types (number, string, boolean, array, object)
- Date/time in ISO 8601 format: "2024-01-15T10:30:00Z"

#### Response Body

**Success Response:**
```json
{
  "orderId": 1001,
  "orderNumber": "ORD-2024-001",
  "userId": 12345,
  "totalAmount": 109.97,
  "status": "PENDING",
  "items": [...],
  "createdAt": "2024-01-15T10:30:00Z",
  "updatedAt": "2024-01-15T10:30:00Z"
}
```

**Error Response:**
```json
{
  "timestamp": "2024-01-15T10:30:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Invalid order request",
  "path": "/api/v1/orders",
  "errors": [
    {
      "field": "items",
      "rejectedValue": [],
      "message": "Items list cannot be empty"
    }
  ],
  "traceId": "abc123xyz"
}
```

### 7. Pagination

**Query Parameters:**
```
GET /api/v1/orders?page=0&size=20&sort=createdAt,desc
```

**Response:**
```json
{
  "content": [...],
  "page": {
    "size": 20,
    "number": 0,
    "totalElements": 150,
    "totalPages": 8
  }
}
```

**Standards:**
- Default page size: 20
- Max page size: 100
- Page numbers start at 0
- Support multiple sort fields

### 8. Filtering & Searching

```
GET /api/v1/orders?status=PENDING
GET /api/v1/orders?userId=123&status=COMPLETED
GET /api/v1/orders?search=ORD-2024
GET /api/v1/orders?minAmount=100&maxAmount=500
```

### 9. Field Selection (Sparse Fieldsets)

```
GET /api/v1/orders?fields=orderId,status,totalAmount
```

Returns only specified fields to reduce payload size.

### 10. Headers

#### Request Headers
```
Content-Type: application/json
Accept: application/json
Authorization: Bearer {jwt-token}
X-Correlation-ID: {uuid}
```

#### Response Headers
```
Content-Type: application/json
X-Correlation-ID: {uuid}
X-RateLimit-Limit: 100
X-RateLimit-Remaining: 95
X-RateLimit-Reset: 1234567890
```

## API Documentation

### Swagger/OpenAPI

All APIs must be documented using OpenAPI 3.0 specification.

**Example Annotation:**
```java
@Operation(
    summary = "Create a new order",
    description = "Creates a new order and publishes OrderCreated event"
)
@ApiResponses(value = {
    @ApiResponse(
        responseCode = "201",
        description = "Order created successfully",
        content = @Content(schema = @Schema(implementation = OrderResponse.class))
    ),
    @ApiResponse(
        responseCode = "400",
        description = "Invalid request",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))
    )
})
@PostMapping
public ResponseEntity<OrderResponse> createOrder(
    @Valid @RequestBody OrderRequest request
) {
    // Implementation
}
```

## Validation Rules

### Input Validation

```java
public class OrderRequest {

    @NotNull(message = "User ID is required")
    @Positive(message = "User ID must be positive")
    private Long userId;

    @NotEmpty(message = "Items list cannot be empty")
    @Size(min = 1, max = 50, message = "Items count must be between 1 and 50")
    @Valid
    private List<OrderItemDto> items;
}

public class OrderItemDto {

    @NotNull(message = "Product ID is required")
    @Positive(message = "Product ID must be positive")
    private Long productId;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    @Max(value = 100, message = "Quantity cannot exceed 100")
    private Integer quantity;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    @Digits(integer = 8, fraction = 2, message = "Invalid price format")
    private BigDecimal price;
}
```

## Security

### Authentication
- Use JWT tokens in Authorization header
- Token format: `Bearer {token}`
- Include user context in token claims

### Authorization
- Implement role-based access control (RBAC)
- Users can only access their own resources
- Admins can access all resources

### Rate Limiting
- Default: 100 requests per minute per user
- Return 429 Too Many Requests when exceeded
- Include rate limit headers in response

## Performance

### Response Time SLAs
- Simple GET: < 100ms (p95)
- Complex GET with joins: < 200ms (p95)
- POST/PUT operations: < 300ms (p95)

### Caching
- Use ETags for conditional requests
- Implement cache headers:
  ```
  Cache-Control: max-age=3600
  ETag: "33a64df551425fcc55e4d42a148795d9f25f89d4"
  ```

### Compression
- Enable GZIP compression for responses > 1KB
- `Accept-Encoding: gzip`

## Error Handling Best Practices

### Error Response Structure
```json
{
  "timestamp": "2024-01-15T10:30:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "path": "/api/v1/orders",
  "traceId": "abc123",
  "errors": [
    {
      "field": "items[0].quantity",
      "rejectedValue": -1,
      "message": "Quantity must be at least 1"
    }
  ]
}
```

### Error Messages
- Use clear, actionable error messages
- Don't expose internal implementation details
- Include correlation/trace ID for debugging
- Localize messages when possible

## Idempotency

### Idempotent Operations
- Implement idempotency for POST operations
- Use `Idempotency-Key` header:
  ```
  Idempotency-Key: {uuid}
  ```
- Store key with result for 24 hours
- Return same response for duplicate requests

## Backward Compatibility

### Breaking Changes
Avoid breaking changes. If necessary:
1. Increment major version (v1 → v2)
2. Support old version for at least 6 months
3. Communicate deprecation clearly

### Non-Breaking Changes (Safe)
- Adding new endpoints
- Adding optional fields to request
- Adding new fields to response
- Making required field optional

### Breaking Changes (Avoid)
- Removing endpoints
- Removing fields from response
- Adding required fields to request
- Changing field types
- Changing field names

## Testing

### API Testing Checklist
- [ ] Happy path scenarios
- [ ] Validation errors (400)
- [ ] Not found errors (404)
- [ ] Unauthorized access (401)
- [ ] Forbidden access (403)
- [ ] Server errors (500)
- [ ] Pagination edge cases
- [ ] Filtering and sorting
- [ ] Concurrent requests
- [ ] Rate limiting

## Monitoring

### Metrics to Track
- Request count per endpoint
- Response time percentiles (p50, p95, p99)
- Error rate per endpoint
- 4xx and 5xx error counts
- Rate limit hits

### Logging
Log every API request with:
```json
{
  "timestamp": "2024-01-15T10:30:00Z",
  "method": "POST",
  "path": "/api/v1/orders",
  "status": 201,
  "duration": 123,
  "userId": 12345,
  "traceId": "abc123",
  "spanId": "xyz789"
}
```

## Examples

### Complete API Example

```java
@RestController
@RequestMapping("/api/v1/orders")
@Tag(name = "Orders", description = "Order management API")
@Validated
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @Operation(summary = "Create order")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<OrderResponse> createOrder(
        @Valid @RequestBody OrderRequest request,
        @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey
    ) {
        OrderResponse response = orderService.createOrder(request, idempotencyKey);
        return ResponseEntity
            .created(URI.create("/api/v1/orders/" + response.getOrderId()))
            .body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get order by ID")
    public ResponseEntity<OrderResponse> getOrder(
        @PathVariable @Positive Long id
    ) {
        return ResponseEntity.ok(orderService.getOrder(id));
    }

    @GetMapping
    @Operation(summary = "List orders")
    public ResponseEntity<Page<OrderResponse>> listOrders(
        @RequestParam(required = false) Long userId,
        @RequestParam(required = false) OrderStatus status,
        @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC)
        Pageable pageable
    ) {
        return ResponseEntity.ok(orderService.listOrders(userId, status, pageable));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Cancel order")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> cancelOrder(@PathVariable @Positive Long id) {
        orderService.cancelOrder(id);
        return ResponseEntity.noContent().build();
    }
}
```

## References
- [REST API Tutorial](https://restfulapi.net/)
- [OpenAPI Specification](https://swagger.io/specification/)
- [HTTP Status Codes](https://httpstatuses.com/)
- [RFC 7231 - HTTP Semantics](https://tools.ietf.org/html/rfc7231)
