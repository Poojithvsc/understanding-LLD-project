# High-Level Design (HLD)

## System Overview

The E-Commerce Order Processing System is a distributed microservices-based application that handles the complete lifecycle of an order from creation to fulfillment.

## Business Requirements

1. Users can create orders for products
2. System validates inventory availability
3. Payment processing for orders
4. Order status notifications to users
5. Generate and store receipts/invoices in cloud storage
6. Real-time order tracking

## Architecture Style

**Microservices with Event-Driven Architecture**

## Core Components

### 1. Order Service
- **Responsibility**: Manage order lifecycle
- **Endpoints**:
  - POST /api/v1/orders (Create order)
  - GET /api/v1/orders/{id} (Get order details)
  - GET /api/v1/orders (List orders with pagination)
  - PUT /api/v1/orders/{id}/cancel (Cancel order)
- **Database**: PostgreSQL (orders, order_items tables)
- **Events Published**:
  - OrderCreated
  - OrderCancelled
  - OrderCompleted

### 2. Inventory Service
- **Responsibility**: Track product stock levels
- **Endpoints**:
  - GET /api/v1/inventory/{productId}
  - PUT /api/v1/inventory/{productId}/reserve
  - PUT /api/v1/inventory/{productId}/release
- **Database**: PostgreSQL (inventory table)
- **Events Consumed**: OrderCreated, OrderCancelled
- **Events Published**: InventoryReserved, InventoryInsufficient

### 3. Payment Service
- **Responsibility**: Process payments
- **Endpoints**:
  - POST /api/v1/payments
  - GET /api/v1/payments/{id}
- **Database**: PostgreSQL (payments table)
- **Events Consumed**: InventoryReserved
- **Events Published**: PaymentCompleted, PaymentFailed

### 4. Notification Service
- **Responsibility**: Send notifications (email/SMS simulation)
- **Endpoints**: Internal only
- **Database**: PostgreSQL (notification_log table)
- **Events Consumed**: OrderCreated, PaymentCompleted, OrderCompleted

### 5. File Storage Service
- **Responsibility**: Generate and store documents
- **Endpoints**:
  - POST /api/v1/files/upload
  - GET /api/v1/files/{id}/download
- **Storage**: AWS S3 (LocalStack for dev)
- **Database**: PostgreSQL (file_metadata table)
- **Events Consumed**: PaymentCompleted (to generate invoice)

## Data Flow

### Happy Path: Order Creation

```
1. Client → Order Service: POST /api/v1/orders
2. Order Service → Database: Save order (status: PENDING)
3. Order Service → Kafka: Publish OrderCreated event
4. Inventory Service ← Kafka: Consume OrderCreated
5. Inventory Service → Database: Check & reserve stock
6. Inventory Service → Kafka: Publish InventoryReserved
7. Payment Service ← Kafka: Consume InventoryReserved
8. Payment Service → External: Process payment
9. Payment Service → Database: Save payment record
10. Payment Service → Kafka: Publish PaymentCompleted
11. File Storage Service ← Kafka: Consume PaymentCompleted
12. File Storage Service → S3: Generate & upload invoice
13. Order Service ← Kafka: Consume PaymentCompleted
14. Order Service → Database: Update order (status: COMPLETED)
15. Notification Service ← Kafka: Send confirmation
```

## Technology Decisions

### Why Kafka?
- Decouples services
- Provides event sourcing capability
- Handles high throughput
- Built-in fault tolerance

### Why PostgreSQL?
- ACID compliance for financial transactions
- Robust and mature
- Good for relational data (orders, items)

### Why Redis?
- Fast caching for frequently accessed data
- Reduce database load
- Session management

### Why S3?
- Scalable object storage
- Industry standard
- Cost-effective for file storage

## Non-Functional Requirements

### Performance
- API response time: < 200ms (p95)
- Kafka message processing: < 100ms
- Support 1000 concurrent users

### Scalability
- Horizontal scaling for all services
- Kafka partitioning for parallelism

### Reliability
- 99.9% uptime
- Retry mechanism for failed events
- Dead letter queues for poison messages

### Security
- JWT-based authentication
- HTTPS for all APIs
- Encrypted data at rest
- Secret management

## Deployment Architecture

```
[Load Balancer]
      ↓
[API Gateway] (Future enhancement)
      ↓
[Microservices] (Docker containers)
      ↓
[Kafka Cluster]
      ↓
[PostgreSQL] [Redis] [S3/LocalStack]
```

## Monitoring & Observability

- **Logging**: Structured JSON logs (SLF4J + Logback)
- **Metrics**: Micrometer + Prometheus
- **Tracing**: Spring Cloud Sleuth
- **Health Checks**: Spring Actuator

## Future Enhancements

1. API Gateway (Spring Cloud Gateway)
2. Service Discovery (Eureka/Consul)
3. Circuit Breaker (Resilience4j)
4. Distributed Caching
5. GraphQL API
6. Kubernetes deployment
7. Elasticsearch for order search

## Assumptions

1. Single region deployment initially
2. No real payment gateway integration (simulated)
3. Email/SMS notifications are logged only
4. LocalStack for local S3 development

## Risks & Mitigation

| Risk | Impact | Mitigation |
|------|--------|------------|
| Kafka downtime | High | Retry logic, fallback to direct API calls |
| Database failure | High | Replication, automated backups |
| S3 unavailability | Medium | Queue file uploads, retry mechanism |
| Service cascade failure | High | Circuit breakers, timeouts |

## Success Metrics

- Order processing success rate > 99%
- Average order processing time < 5 seconds
- Zero data loss
- All services have > 80% test coverage
