# Flash Sale System

Hệ thống bán hàng chớp nhoáng (Flash Sale) với khả năng xử lý đồng thời cao, đảm bảo tính công bằng và chống gian lận.

## 🎯 Mục tiêu

- Xử lý hàng ngàn yêu cầu trong thời gian ngắn
- Đảm bảo công bằng (FIFO - First In First Out)
- Chống overselling (bán quá số lượng)
- Hạn chế gian lận (mỗi người chỉ mua được một lần)
- Xử lý đồng thời an toàn

## 🛠 Công nghệ sử dụng

### Backend
- Java 21
- Spring Boot 3.4.5
- Spring Data JPA
- Spring Data Redis
- Spring Kafka
- MySQL
- Redis
- Kafka

### Cấu trúc dữ liệu (DSA)
1. **Queue (FIFO)**
   - Xử lý đơn hàng theo thứ tự
   - Kafka message queue
   - Dead Letter Queue (DLQ) cho xử lý lỗi

2. **Set (HashSet)**
   - Lưu danh sách user đã mua
   - Kiểm tra O(1) user đã mua chưa
   - Redis Set implementation

3. **HashMap**
   - Cache thông tin sản phẩm
   - Mapping productId → Inventory
   - Redis Hash implementation

4. **Atomic Counter**
   - Trừ kho an toàn
   - Redis INCR/DECR commands
   - Lua script atomic operations

5. **Sliding Window**
   - Rate limiting per user
   - Chống spam/bot
   - Time-based request tracking

## 📋 Tính năng chính

### 1. Quản lý sản phẩm
- Xem danh sách sản phẩm flash sale
- Tìm kiếm sản phẩm theo giá
- Khởi tạo stock cho sản phẩm

### 2. Xử lý đơn hàng
- Đặt mua sản phẩm flash sale
- Kiểm tra tồn kho realtime
- Xử lý đơn hàng bất đồng bộ
- Theo dõi trạng thái đơn hàng

### 3. Bảo mật & Chống gian lận
- Rate limiting (global & per-user)
- Kiểm tra user đã mua
- Atomic stock checking
- Validation đầu vào

### 4. Xử lý lỗi & Retry
- Dead Letter Queue
- Cơ chế retry
- Logging chi tiết
- Error handling

## 🏗 Kiến trúc hệ thống

### 1. API Layer
```
/api/flash-sale/
├── POST /purchase           # Đặt mua sản phẩm
├── POST /products/{id}/initialize  # Khởi tạo stock
├── GET /products/active     # Danh sách flash sale đang diễn ra
├── GET /products/search     # Tìm kiếm sản phẩm
└── GET /orders/{userId}     # Lịch sử đơn hàng
```

### 2. Service Layer
- FlashSaleService: Xử lý logic chính
- OrderProcessor: Xử lý đơn hàng bất đồng bộ
- Rate Limiting: Kiểm soát request

### 3. Data Layer
- MySQL: Lưu trữ dữ liệu chính
- Redis: Cache & Atomic operations
- Kafka: Message queue

## 🔄 Luồng xử lý

1. **Khởi tạo sản phẩm**
```bash
curl -X POST "http://localhost:8080/api/flash-sale/products/1/initialize?stock=100"
```

2. **Đặt mua**
```bash
curl -X POST "http://localhost:8080/api/flash-sale/purchase" \
     -H "Content-Type: application/json" \
     -d '{
           "productId": 1,
           "userId": 1,
           "quantity": 1
         }'
```

3. **Xem danh sách flash sale**
```bash
curl "http://localhost:8080/api/flash-sale/products/active"
```

4. **Tìm kiếm sản phẩm**
```bash
curl "http://localhost:8080/api/flash-sale/products/search?minPrice=100&maxPrice=1000"
```

5. **Xem lịch sử đơn hàng**
```bash
curl "http://localhost:8080/api/flash-sale/orders/1"
```

## ⚙️ Cấu hình

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

## 🔒 Bảo mật

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

### Yêu cầu hệ thống
- Java 21
- MySQL 8.0+
- Redis 6.0+
- Kafka 3.0+

### Các bước triển khai
1. Clone repository
2. Cấu hình database
3. Cấu hình Redis
4. Cấu hình Kafka
5. Build và chạy ứng dụng

## 📈 Khả năng mở rộng

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