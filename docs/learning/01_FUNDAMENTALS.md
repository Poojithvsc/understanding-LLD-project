# 📚 Fundamentals: APIs, REST, HTTP, and Databases

**Your Reference Guide** - Read this anytime you have doubts!

---

## 🌐 Part 1: What is an API?

### Real-World Example
Imagine you're at a restaurant:
- **You** (Client) = Your web browser or mobile app
- **Waiter** (API) = The middleman who takes your order
- **Kitchen** (Server/Database) = Where the actual work happens

**The API is like the waiter:**
- You tell the waiter what you want (make a request)
- Waiter goes to kitchen (server processes)
- Waiter brings your food (returns response)

### Technical Definition
**API (Application Programming Interface)** = A set of rules that lets two applications talk to each other.

**Example:**
- When you use a weather app, it calls a weather API
- When you login to Facebook, the app calls Facebook's API
- When you order food on Swiggy, the app calls Swiggy's API

---

## 🔄 Part 2: What is REST?

**REST (Representational State Transfer)** = A style of building APIs using standard HTTP methods.

### The 5 Main Operations (CRUD)

| Operation | HTTP Method | What It Does | Real Example |
|-----------|-------------|--------------|--------------|
| **Create** | POST | Add new data | Sign up for account |
| **Read** | GET | Retrieve data | View your profile |
| **Update** | PUT/PATCH | Modify data | Edit your profile |
| **Delete** | DELETE | Remove data | Delete your account |
| **List** | GET | Get all items | See all your orders |

### REST API Structure
```
http://localhost:8080/api/v1/orders
     ↑              ↑    ↑      ↑
   Protocol       Host  Base   Resource

Examples:
GET    /api/v1/orders          → Get all orders
GET    /api/v1/orders/123      → Get order #123
POST   /api/v1/orders          → Create new order
PUT    /api/v1/orders/123      → Update order #123
DELETE /api/v1/orders/123      → Delete order #123
```

---

## 🌍 Part 3: Understanding HTTP

**HTTP (HyperText Transfer Protocol)** = The language browsers and servers use to communicate.

### HTTP Request (What you send)
```
POST /api/v1/orders HTTP/1.1          ← Method + URL
Host: localhost:8080                   ← Where to send
Content-Type: application/json         ← What type of data

{                                      ← Request Body (data)
  "customerName": "John",
  "email": "john@example.com"
}
```

### HTTP Response (What you get back)
```
HTTP/1.1 201 Created                   ← Status Code
Content-Type: application/json         ← Response type

{                                      ← Response Body (data)
  "orderId": "abc-123",
  "customerName": "John",
  "status": "PENDING"
}
```

### HTTP Status Codes (What they mean)

| Code | Meaning | Example |
|------|---------|---------|
| **200** | OK | Request successful |
| **201** | Created | New item created |
| **400** | Bad Request | Invalid data sent |
| **404** | Not Found | Item doesn't exist |
| **500** | Server Error | Something broke on server |

---

## 🗄️ Part 4: Understanding Databases

### What is a Database?
A **database** is like a collection of Excel spreadsheets that store your data permanently.

### Database Table (Like a Spreadsheet)

**orders** table:
| order_id | customer_name | email | total_amount | status |
|----------|---------------|-------|--------------|--------|
| 1 | John Doe | john@... | 2029.97 | PENDING |
| 2 | Jane Smith | jane@... | 500.00 | CONFIRMED |

**order_items** table:
| item_id | order_id | product_name | quantity | price |
|---------|----------|--------------|----------|-------|
| 1 | 1 | Laptop | 2 | 999.99 |
| 2 | 1 | Mouse | 1 | 29.99 |
| 3 | 2 | Keyboard | 1 | 500.00 |

### Table Relationships
**One-to-Many**: One order can have many items
```
Order #1 (John's order)
  ├─ Item #1: Laptop
  ├─ Item #2: Mouse
  └─ Item #3: Keyboard
```

The `order_id` in `order_items` table is called a **Foreign Key** - it links to the `orders` table.

---

## 🆚 Part 5: H2 vs PostgreSQL

### H2 Database (What we used initially)
- ✅ **In-Memory**: Data stored in RAM (super fast)
- ❌ **Temporary**: Data lost when app stops
- ✅ **Easy**: No setup needed
- ✅ **Use Case**: Development, testing, learning basics

### PostgreSQL (What we'll use now)
- ✅ **Persistent**: Data saved to disk (survives restarts)
- ✅ **Production-Ready**: Used by companies worldwide
- ✅ **Powerful**: Advanced features (transactions, indexing, etc.)
- ✅ **Free & Open Source**
- ✅ **Use Case**: Real applications

---

## 🔑 Key Terms You'll Use

| Term | What It Means |
|------|---------------|
| **Endpoint** | A specific URL in your API (e.g., `/api/v1/orders`) |
| **Request** | Data sent FROM client TO server |
| **Response** | Data sent FROM server TO client |
| **JSON** | Format for sending data (looks like JavaScript object) |
| **Entity** | A Java class that represents a database table |
| **Repository** | Interface to access database (read/write data) |
| **Service** | Business logic layer (where calculations happen) |
| **Controller** | Handles HTTP requests and responses |

---

## 📝 JSON Example (Data Format)

JSON is how data is sent between client and server:

```json
{
  "customerName": "John Doe",
  "email": "john@example.com",
  "orderItems": [
    {
      "productName": "Laptop",
      "quantity": 2,
      "price": 999.99
    },
    {
      "productName": "Mouse",
      "quantity": 1,
      "price": 29.99
    }
  ]
}
```

**Key Points:**
- Curly braces `{}` = Object
- Square brackets `[]` = Array (list)
- Colon `:` separates name and value
- Comma `,` separates items

---

## 🎯 Quiz Yourself

1. **What does GET /api/v1/orders do?**
   - Answer: Retrieves all orders

2. **What HTTP status code means "created successfully"?**
   - Answer: 201

3. **What's the difference between H2 and PostgreSQL?**
   - Answer: H2 is in-memory (temporary), PostgreSQL saves to disk (permanent)

4. **What is a Foreign Key?**
   - Answer: A field that links one table to another

---

**Next Steps:** Read "02_SPRING_BOOT_BASICS.md" to learn about Spring Boot!
