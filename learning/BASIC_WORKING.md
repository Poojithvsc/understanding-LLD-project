# Basic Working - How The System Works

## The Complete Flow

```
┌──────┐
│ USER │
└──────┘
   │
   │ 1. Place Order (HTTP POST request with JSON)
   ▼
┌─────────────────────────────────────────────────────────────┐
│                      ORDER SERVICE                           │
│                        (Port 8080)                           │
│                                                              │
│  ┌─────────────────────────────────────────────────────┐    │
│  │ Controller                                           │    │
│  │ • Receives HTTP requests from user                   │    │
│  │ • Validates input (is email valid? name provided?)   │    │
│  │ • Returns HTTP responses (200 OK, 201 Created, etc.) │    │
│  └──────────────────────┬──────────────────────────────┘    │
│                         │                                    │
│                         ▼                                    │
│  ┌─────────────────────────────────────────────────────┐    │
│  │ Service                                              │    │
│  │ • Contains business logic                            │    │
│  │ • Calculates order total (quantity × price)          │    │
│  │ • Generates order number (ORD-20251217-123456)       │    │
│  │ • Decides what happens next                          │    │
│  └──────────────────────┬──────────────────────────────┘    │
│                         │                                    │
│                         ▼                                    │
│  ┌─────────────────────────────────────────────────────┐    │
│  │ Repository                                           │    │
│  │ • Talks to database                                  │    │
│  │ • save() → INSERT into database                      │    │
│  │ • findById() → SELECT from database                  │    │
│  │ • delete() → DELETE from database                    │    │
│  └─────────────────────────────────────────────────────┘    │
│                                                              │
└──────────────────────────┬───────────────────────────────────┘
                           │
              2. Save Order│
                           ▼
                    ┌─────────────┐
                    │ PostgreSQL  │
                    │   orderdb   │
                    │             │
                    │ Tables:     │
                    │ • orders    │
                    │ • order_items│
                    └─────────────┘
```

**After saving, Order Service also does this:**

```
ORDER SERVICE
      │
      │ 3. Publish Event
      │    (sends a message: "Hey, new order created!")
      ▼
┌─────────────────────────────────────────┐
│              KAFKA                       │
│                                          │
│  Topic: "order-created"                  │
│  ─────────────────────                   │
│  • Like a mailbox/notice board           │
│  • Holds messages until someone reads    │
│  • Message contains:                     │
│    - orderId                             │
│    - customerName                        │
│    - items (productId, quantity)         │
│                                          │
└──────────────────────┬───────────────────┘
                       │
                       │ 4. Consume Event
                       │    (reads the message automatically)
                       ▼
┌─────────────────────────────────────────────────────────────┐
│                   INVENTORY SERVICE                          │
│                      (Port 8081)                             │
│                                                              │
│  ┌─────────────────────────────────────────────────────┐    │
│  │ Kafka Consumer                                       │    │
│  │ • Listens to Kafka topic 24/7                        │    │
│  │ • When message arrives → triggers processing         │    │
│  │ • Reads: "Order has 2 laptops, 1 mouse"              │    │
│  └──────────────────────┬──────────────────────────────┘    │
│                         │                                    │
│                         ▼                                    │
│  ┌─────────────────────────────────────────────────────┐    │
│  │ Service                                              │    │
│  │ • Business logic for inventory                       │    │
│  │ • Gets current stock: "Laptop has 10 in stock"       │    │
│  │ • Calculates new stock: 10 - 2 = 8                   │    │
│  │ • Updates the product stock                          │    │
│  └──────────────────────┬──────────────────────────────┘    │
│                         │                                    │
│                         ▼                                    │
│  ┌─────────────────────────────────────────────────────┐    │
│  │ Repository                                           │    │
│  │ • Talks to inventory database                        │    │
│  │ • Updates product stock in database                  │    │
│  └─────────────────────────────────────────────────────┘    │
│                                                              │
└──────────────────────────┬───────────────────────────────────┘
                           │
              5. Update    │
                 Stock     │
                           ▼
                    ┌─────────────┐
                    │ PostgreSQL  │
                    │ inventorydb │
                    │             │
                    │ Tables:     │
                    │ • products  │
                    │ • inventory │
                    └─────────────┘
```

---

## Summary Table

| Block | What It Does | Simple Analogy |
|-------|--------------|----------------|
| **User** | Sends order request | Customer at a store |
| **Controller** | Receives & validates requests | Receptionist |
| **Service** | Business logic & calculations | Manager |
| **Repository** | Database operations | File clerk |
| **PostgreSQL** | Stores data permanently | Filing cabinet |
| **Kafka** | Passes messages between services | Notice board |
| **Kafka Consumer** | Listens for new messages | Employee checking the notice board |

---

## The Flow in Plain English

1. **User** places an order → sends to Order Service
2. **Order Controller** receives it, checks if data is valid
3. **Order Service** calculates total, generates order number
4. **Order Repository** saves to database
5. **Order Service** posts message to Kafka: "New order!"
6. **Kafka Consumer** in Inventory sees the message
7. **Inventory Service** reduces stock (10 laptops → 8 laptops)
8. **Inventory Repository** saves updated stock to database

---

## Key Point

**The user never directly touches Inventory Service** - it all happens automatically through Kafka!

```
USER ──► ORDER SERVICE ──► KAFKA ──► INVENTORY SERVICE
              │                            │
              ▼                            ▼
           orderdb                    inventorydb
```
