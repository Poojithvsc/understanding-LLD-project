# Basic Working - How The System Works

## Two Main User Actions

```
                              USER
                                |
            +-------------------+-------------------+
            |                                       |
            v                                       v
   "I want to add/view products"         "I want to place an order"
            |                                       |
            v                                       v
    INVENTORY SERVICE                       ORDER SERVICE
      (Port 8081)                            (Port 8080)
```

---

## Action 1: Managing Products (Inventory Service)

```
USER
  |
  | POST /api/v1/products (add new product)
  | GET /api/v1/products (view all products)
  v
+-------------------------------------------------------------+
|                   INVENTORY SERVICE                          |
|                      (Port 8081)                             |
|                                                              |
|  +-------------------------------------------------------+  |
|  | Controller                                             |  |
|  | - Receives HTTP requests                               |  |
|  | - Validates input                                      |  |
|  | - Returns HTTP responses                               |  |
|  +------------------------+------------------------------+  |
|                           |                                  |
|                           v                                  |
|  +-------------------------------------------------------+  |
|  | Service                                                |  |
|  | - Business logic for products                          |  |
|  | - CRUD operations                                      |  |
|  +------------------------+------------------------------+  |
|                           |                                  |
|                           v                                  |
|  +-------------------------------------------------------+  |
|  | Repository                                             |  |
|  | - Talks to database                                    |  |
|  | - save(), findById(), delete()                         |  |
|  +-------------------------------------------------------+  |
|                                                              |
+------------------------------+-------------------------------+
                               |
                               v
                      +-----------------+
                      |   PostgreSQL    |
                      |   inventorydb   |
                      |                 |
                      | Tables:         |
                      | - products      |
                      | - inventory     |
                      +-----------------+
```

---

## Action 2: Placing an Order (Order Service + Kafka)

```
USER
  |
  | POST /api/v1/orders (place order)
  v
+-------------------------------------------------------------+
|                     ORDER SERVICE                            |
|                      (Port 8080)                             |
|                                                              |
|  +-------------------------------------------------------+  |
|  | Controller                                             |  |
|  | - Receives order request                               |  |
|  | - Validates input                                      |  |
|  +------------------------+------------------------------+  |
|                           |                                  |
|                           v                                  |
|  +-------------------------------------------------------+  |
|  | Service                                                |  |
|  | - Calculates order total                               |  |
|  | - Generates order number                               |  |
|  +------------------------+------------------------------+  |
|                           |                                  |
|          +----------------+----------------+                 |
|          |                                 |                 |
|          v                                 v                 |
|  +----------------+             +----------------------+     |
|  | Repository     |             | Kafka Producer       |     |
|  | save(order)    |             | "Hey, new order!"    |     |
|  +-------+--------+             +----------+-----------+     |
|          |                                 |                 |
+----------+---------------------------------+-----------------+
           |                                 |
           v                                 v
   +-----------------+            +----------------------+
   |   PostgreSQL    |            |       KAFKA          |
   |    orderdb      |            |                      |
   |                 |            | Topic: order-created |
   | Tables:         |            +----------+-----------+
   | - orders        |                       |
   | - order_items   |                       | (message delivered)
   +-----------------+                       v
                              +------------------------------+
                              |     INVENTORY SERVICE        |
                              |                              |
                              | +-------------------------+  |
                              | | Kafka Consumer          |  |
                              | | Listens 24/7            |  |
                              | | "Oh, new order came!"   |  |
                              | +-----------+-------------+  |
                              |             |                |
                              |             v                |
                              | +-------------------------+  |
                              | | Service                 |  |
                              | | "Reduce stock by qty"   |  |
                              | +-----------+-------------+  |
                              |             |                |
                              |             v                |
                              | +-------------------------+  |
                              | | Repository              |  |
                              | | save(updated product)   |  |
                              | +-------------------------+  |
                              +------------------------------+
                                            |
                                            v
                                   +-----------------+
                                   |   PostgreSQL    |
                                   |   inventorydb   |
                                   |                 |
                                   | Stock updated!  |
                                   | 10 -> 8         |
                                   +-----------------+
```

---

## Summary Table

| Action | Service | What Happens |
|--------|---------|--------------|
| Add Product | Inventory | Direct save to database |
| View Products | Inventory | Direct read from database |
| Place Order | Order | Save order + send Kafka event |
| Stock Update | Inventory | Auto-triggered by Kafka |

---

## The Key Point

- **User interacts with BOTH services directly** for different purposes
- **Kafka connects them** for automatic stock updates
- **User never manually calls** the stock update - it happens automatically!

```
User Action          |  Result
---------------------|------------------------------------------
Add product          |  Product saved in inventorydb
Place order          |  Order saved + Kafka triggers stock update
```
