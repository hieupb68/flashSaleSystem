# Flash Sale System

A high-concurrency flash sale system that ensures fairness and prevents fraud.

## 🎯 Objectives

- Handle thousands of requests in a short time
- Ensure fairness (FIFO - First In First Out)
- Prevent overselling (selling more than available stock)
- Prevent fraud (one purchase per user)
- Safe concurrent processing

## 🛠 Technologies Used

### Backend
- Java 21
- Spring Boot 3.4.5
- Spring Data JPA
- Spring Data Redis
- Spring Kafka
- MySQL
- Redis
- Kafka

### Data Structures (DSA)
1. **Queue (FIFO)**
   - Order processing in sequence
   - Kafka message queue
   - Dead Letter Queue (DLQ) for error handling

2. **Set (HashSet)**
   - Store purchased user list
   - O(1) user purchase check
   - Redis Set implementation

3. **HashMap**
   - Cache product information
   - Mapping productId → Inventory
   - Redis Hash implementation

4. **Atomic Counter**
   - Safe stock deduction
   - Redis INCR/DECR commands
   - Lua script atomic operations

5. **Sliding Window**
   - Rate limiting per user
   - Anti-spam/bot protection
   - Time-based request tracking

## 📋 Core Features

### 1. Product Management
- View active flash sale products
- Search products by price range
- Initialize product stock

### 2. Order Processing
- Purchase flash sale products
- Real-time stock checking
- Asynchronous order processing
- Order status tracking

### 3. Security & Anti-Fraud
- Rate limiting (global & per-user)
- User purchase verification
- Atomic stock checking
- Input validation

### 4. Error Handling & Retry
- Dead Letter Queue
- Retry mechanism
- Detailed logging
- Error handling

## 🏗 System Architecture

### 1. API Layer
```
/api/flash-sale/
├── POST /purchase           # Purchase product
├── POST /products/{id}/initialize  # Initialize stock
├── GET /products/active     # List active flash sales
├── GET /products/search     # Search products
└── GET /orders/{userId}     # User order history
```

### 2. Service Layer
- FlashSaleService: Core business logic
- OrderProcessor: Async order processing
- Rate Limiting: Request control

### 3. Data Layer
- MySQL: Primary data storage
- Redis: Cache & Atomic operations
- Kafka: Message queue

## 🔄 Processing Flow

1. **Initialize Product**
```bash
curl -X POST "http://localhost:8080/api/flash-sale/products/1/initialize?stock=100"
```

2. **Purchase**
```bash
curl -X POST "http://localhost:8080/api/flash-sale/purchase" \
     -H "Content-Type: application/json" \
     -d '{
           "productId": 1,
           "userId": 1,
           "quantity": 1
         }'
```

3. **View Active Flash Sales**
```bash
curl "http://localhost:8080/api/flash-sale/products/active"
```

4. **Search Products**
```bash
curl "http://localhost:8080/api/flash-sale/products/search?minPrice=100&maxPrice=1000"
```

5. **View Order History**
```bash
curl "http://localhost:8080/api/flash-sale/orders/1"
```

## ⚙️ Configuration

### 1. Database (MySQL)
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/flash_sale
    username: root
    password: root
```

### 2. Redis
```yaml
spring:
  redis:
    host: localhost
    port: 6379
```

### 3. Kafka
```yaml
spring:
  kafka:
    bootstrap-servers: localhost:9092
```

## 🧪 Testing

### 1. Unit Tests
- Service layer tests
- Repository tests
- Controller tests

### 2. Integration Tests
- API endpoint tests
- Redis operations tests
- Kafka message tests

### 3. Performance Tests
- Load testing
- Stress testing
- Concurrency testing

## 📊 Monitoring

### 1. Metrics
- Request rate
- Response time
- Error rate
- Queue size

### 2. Logging
- Request logs
- Error logs
- Performance logs

## 🔒 Security

### 1. Rate Limiting
- Global: 100 requests/second
- Per-user: 10 requests/second

### 2. Validation
- Input validation
- Business rule validation
- Data integrity checks

### 3. Error Handling
- Graceful degradation
- Error recovery
- Retry mechanism

## 🚀 Deployment

### System Requirements
- Java 21
- MySQL 8.0+
- Redis 6.0+
- Kafka 3.0+

### Deployment Steps
1. Clone repository
2. Configure database
3. Configure Redis
4. Configure Kafka
5. Build and run application

## 📈 Scalability

### 1. Horizontal Scaling
- Multiple application instances
- Load balancing
- Database sharding

### 2. Performance Optimization
- Cache optimization
- Query optimization
- Connection pooling

### 3. Feature Extensions
- Payment integration
- Notification system
- Analytics dashboard

## 🤝 Contributing

1. Fork repository
2. Create feature branch
3. Commit changes
4. Push to branch
5. Create Pull Request

## 📝 License

MIT License 