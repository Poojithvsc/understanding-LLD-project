# Apache Kafka - Complete Visual Guide

## What is Kafka?

**Kafka is like a post office for your microservices.**

Imagine:
- **Order Service** = Person sending a letter
- **Kafka** = Post Office
- **Inventory Service** = Person receiving the letter
- **Topic** = Mailbox for a specific type of letter

```
┌─────────────────┐                    ┌──────────────────┐
│  Order Service  │                    │ Inventory Service│
│                 │                    │                  │
│  "I created     │                    │  "Let me check   │
│   an order!"    │                    │   my mailbox"    │
└────────┬────────┘                    └────────▲─────────┘
         │                                      │
         │ 1. Publish Event                    │ 3. Consume Event
         │                                      │
         ▼                                      │
    ┌────────────────────────────────────────────────┐
    │              KAFKA (Post Office)               │
    │                                                │
    │  Topic: "order-created" (Mailbox)              │
    │  ┌──────────────────────────────────────┐     │
    │  │ Message 1: Order #123 created        │     │
    │  │ Message 2: Order #124 created    ────┼─────┘
    │  │ Message 3: Order #125 created        │  2. Store & Forward
    │  └──────────────────────────────────────┘     │
    └────────────────────────────────────────────────┘
```

## Why Use Kafka?

### Without Kafka (Direct Communication)
```
Order Service ──HTTP──> Inventory Service
                        ↓
                    What if it's down?
                    What if it's slow?
                    Order Service waits!
```

**Problems:**
- ❌ Order Service must wait for Inventory Service
- ❌ If Inventory Service crashes, order creation fails
- ❌ Tight coupling between services

### With Kafka (Event-Driven)
```
Order Service ──Event──> Kafka ──Event──> Inventory Service
     ✓                     ✓                    ✓
  Continues            Stores              Processes
   working            forever             when ready
```

**Benefits:**
- ✅ Order Service doesn't wait
- ✅ If Inventory Service is down, event waits in Kafka
- ✅ Services are independent (loose coupling)
- ✅ Can add more consumers anytime

## Key Concepts (Simple Analogies)

### 1. **Topic** = Mailbox Category
- `order-created` topic = Mailbox for order events
- `payment-processed` topic = Mailbox for payment events
- `user-registered` topic = Mailbox for user events

### 2. **Producer** = Sender
- Order Service sends (produces) events to Kafka
- Like putting a letter in a mailbox

### 3. **Consumer** = Receiver
- Inventory Service receives (consumes) events from Kafka
- Like checking your mailbox for letters

### 4. **Message/Event** = The Letter
- Contains data about what happened
- Example: "Order #123 was created with 5 MacBooks"

### 5. **Offset** = Position in Mailbox
- Messages are numbered: 0, 1, 2, 3...
- Consumer remembers: "I read up to message #5"
- If consumer restarts, it continues from #6

### 6. **Consumer Group** = Team of Receivers
- Multiple Inventory Services can work together
- Each message is processed by only ONE member of the team
- Load balancing!

## What You Should See in Docker Desktop

### Containers Tab:
```
✅ kafka (Port 9092)         - The message broker
✅ zookeeper (Port 2181)     - Kafka's coordinator
✅ postgres-orderdb (5432)   - Your database
```

### How to Check:
1. Open Docker Desktop
2. Click "Containers" on the left
3. You should see 3 green containers running

### What Each Does:

**Zookeeper:**
- Kafka's brain/coordinator
- Tracks which Kafka brokers are alive
- Manages topic metadata
- **You don't interact with it directly**

**Kafka:**
- The actual message broker
- Stores messages in topics
- Handles producer and consumer connections
- **This is the post office!**

**PostgreSQL:**
- Your database
- Stores actual order and product data
- Kafka events trigger database updates

## How Our System Works (Step by Step)

### Scenario: Customer orders 5 MacBooks

**Step 1: Order Created**
```
User → POST /api/v1/orders
      ↓
Order Service creates order in database
      ↓
Order saved to PostgreSQL (orderdb)
```

**Step 2: Event Published**
```
Order Service → Creates OrderCreatedEvent
              → KafkaProducerService.publishOrderCreatedEvent()
              → Sends to Kafka topic "order-created"

Event contains:
{
  "orderId": "abc-123",
  "orderNumber": "ORD-20251216-001",
  "orderItems": [
    {
      "productId": "xyz-789",
      "productName": "MacBook Pro",
      "quantity": 5
    }
  ]
}
```

**Step 3: Kafka Stores Event**
```
Kafka receives event
    ↓
Stores in "order-created" topic
    ↓
Assigns offset number (e.g., offset 10)
    ↓
Keeps forever (or until retention period)
```

**Step 4: Inventory Service Consumes Event**
```
Inventory Service is listening to "order-created" topic
    ↓
KafkaConsumerService.consumeOrderCreatedEvent() called
    ↓
Reads event data
    ↓
Gets product from database: "MacBook Pro has 100 units"
    ↓
Calculates: 100 - 5 = 95 units left
    ↓
Updates product in database
    ↓
Commits offset to Kafka: "I processed message #10"
```

**Step 5: Result**
```
✅ Order created in orderdb
✅ Event sent to Kafka
✅ Stock updated in inventorydb
✅ All done asynchronously!
```

## How to See Kafka in Action

### Method 1: Docker Desktop Logs
1. Open Docker Desktop
2. Click on "kafka" container
3. Click "Logs" tab
4. You'll see Kafka startup logs

### Method 2: Command Line
```bash
# Check Kafka container logs
docker logs kafka

# Check if Kafka is responding
docker exec kafka kafka-topics --list --bootstrap-server localhost:9092
```

### Method 3: View Topics
```bash
# List all topics (you should see "order-created")
docker exec kafka kafka-topics --list --bootstrap-server localhost:9092

# Describe the topic
docker exec kafka kafka-topics --describe --topic order-created --bootstrap-server localhost:9092
```

### Method 4: See Messages in Topic
```bash
# Read all messages from beginning
docker exec kafka kafka-console-consumer --bootstrap-server localhost:9092 --topic order-created --from-beginning
```

## Common Kafka Configurations

### Producer (Order Service)
```properties
# Where is Kafka?
spring.kafka.bootstrap-servers=localhost:9092

# How to serialize the key (String)
spring.kafka.producer.key-serializer=StringSerializer

# How to serialize the value (JSON)
spring.kafka.producer.value-serializer=JsonSerializer

# What topic to send to
kafka.topic.order-created=order-created
```

### Consumer (Inventory Service)
```properties
# Where is Kafka?
spring.kafka.bootstrap-servers=localhost:9092

# What group am I part of?
spring.kafka.consumer.group-id=inventory-service-group

# How to deserialize the key
spring.kafka.consumer.key-deserializer=StringDeserializer

# How to deserialize the value (JSON)
spring.kafka.consumer.value-deserializer=JsonDeserializer

# Trust all packages for JSON deserialization
spring.kafka.consumer.properties.spring.json.trusted.packages=*

# What topic to listen to
kafka.topic.order-created=order-created
```

## Real-World Example: Amazon

When you order on Amazon:

1. **Order Service** creates your order → Publishes `OrderCreatedEvent` to Kafka
2. **Inventory Service** consumes event → Reduces stock
3. **Shipping Service** consumes event → Prepares shipment label
4. **Email Service** consumes event → Sends confirmation email
5. **Analytics Service** consumes event → Updates dashboards

**One event, multiple consumers, all happening asynchronously!**

## Debugging Kafka Issues

### Check if Kafka is running:
```bash
docker ps | grep kafka
```

### Check if topic exists:
```bash
docker exec kafka kafka-topics --list --bootstrap-server localhost:9092
```

### Check consumer group:
```bash
docker exec kafka kafka-consumer-groups --bootstrap-server localhost:9092 --list
docker exec kafka kafka-consumer-groups --bootstrap-server localhost:9092 --describe --group inventory-service-group
```

### Check messages in topic:
```bash
docker exec kafka kafka-console-consumer --bootstrap-server localhost:9092 --topic order-created --from-beginning
```

## Testing Checklist

When testing Kafka integration:

- [ ] All 3 containers running in Docker
- [ ] Order Service starts without errors
- [ ] Inventory Service starts without errors
- [ ] Create an order via API
- [ ] Check Order Service logs for "Successfully published"
- [ ] Check Inventory Service logs for "Received order created event"
- [ ] Check database - stock should decrease
- [ ] View Kafka topic to see the message

## Next Steps for Learning

1. ✅ Understand the concept (this document)
2. ⏳ See it working visually
3. ⏳ Debug the current issue
4. ⏳ Write tests for Kafka integration
5. ⏳ Update project documentation

---

**Remember:** Kafka seems complex, but it's just a reliable post office for your services to communicate!
