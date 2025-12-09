# E-Commerce Order Processing System

A comprehensive learning project demonstrating industry-standard practices in building a distributed system with Java, Kafka, databases, and cloud storage.

## Project Overview

This system simulates an e-commerce order processing pipeline with the following components:
- Order Management Service
- Inventory Service
- Notification Service
- Payment Service
- File Storage Service (receipts, invoices stored in S3)

## Technology Stack

- **Backend Framework**: Spring Boot 3.x
- **Language**: Java 21
- **Message Broker**: Apache Kafka
- **Primary Database**: PostgreSQL
- **Cache**: Redis
- **Object Storage**: AWS S3 (LocalStack for local dev)
- **Containerization**: Docker & Docker Compose
- **Build Tool**: Maven
- **Testing**: JUnit 5, Mockito, Testcontainers
- **API Documentation**: SpringDoc OpenAPI (Swagger)
- **Database Migration**: Flyway
- **Logging**: SLF4J with Logback

## Project Structure

```
.
├── docs/                           # All documentation
│   ├── architecture/
│   │   ├── HLD.md                 # High-Level Design
│   │   ├── LLD.md                 # Low-Level Design
│   │   └── diagrams/              # Architecture diagrams
│   ├── api/                        # API documentation
│   ├── testing/                    # Test strategy and plans
│   └── decisions/                  # Architecture Decision Records (ADR)
├── services/
│   ├── order-service/
│   ├── inventory-service/
│   ├── notification-service/
│   ├── payment-service/
│   └── file-storage-service/
├── infrastructure/
│   ├── docker/                     # Docker configs
│   ├── kubernetes/                 # K8s configs (optional)
│   └── terraform/                  # Infrastructure as Code (optional)
├── scripts/                        # Utility scripts
├── .github/                        # CI/CD workflows
└── docker-compose.yml             # Local development setup
```

## Getting Started

(Instructions will be added as we build)

## Development Workflow

1. Create feature branch from `main`
2. Write/update LLD if needed
3. Implement feature with unit tests (TDD approach)
4. Write integration tests
5. Create PR with proper description
6. Code review
7. Merge to main

## Learning Milestones

### Phase 1: Foundation (Weeks 1-2)
- [ ] Set up development environment
- [ ] Create project structure
- [ ] Set up Docker Compose with all services
- [ ] Implement Order Service basic CRUD

### Phase 2: Database & Persistence (Weeks 3-4)
- [ ] Design database schema
- [ ] Implement Flyway migrations
- [ ] Add PostgreSQL integration
- [ ] Implement Redis caching
- [ ] Write repository layer with tests

### Phase 3: Kafka Integration (Weeks 5-6)
- [ ] Set up Kafka topics
- [ ] Implement event producers
- [ ] Implement event consumers
- [ ] Add error handling and dead letter queues

### Phase 4: Cloud Storage (Week 7)
- [ ] Integrate LocalStack for S3
- [ ] Implement file upload/download
- [ ] Generate and store invoices/receipts

### Phase 5: Advanced Topics (Weeks 8-10)
- [ ] Add distributed tracing
- [ ] Implement circuit breakers
- [ ] Add monitoring with Prometheus/Grafana
- [ ] Performance testing

### Phase 6: Production Readiness (Weeks 11-12)
- [ ] Security implementation (OAuth2/JWT)
- [ ] API rate limiting
- [ ] Comprehensive documentation
- [ ] CI/CD pipeline

## Contributing

(Self-learning project - guidelines for code quality)

## Resources

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Apache Kafka Documentation](https://kafka.apache.org/documentation/)
- [AWS S3 Documentation](https://docs.aws.amazon.com/s3/)
