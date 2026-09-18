# Event Booking System

A backend-focused **Event Booking System** built with Java and Spring Boot, designed to demonstrate REST APIs, microservices architecture, PostgreSQL, Redis distributed locking, service-to-service communication, validation, exception handling, and backend system design concepts.

The project is intentionally kept small enough to build and understand within a short development cycle while covering concepts commonly discussed in Java backend interviews.

---

## 🚀 Project Overview

The Event Booking System allows users to:

* Create events
* View available events
* Update event details
* Delete events
* View a specific event
* Reserve seats for an event
* Create and manage bookings
* Validate booking requests
* Prevent concurrent booking conflicts using Redis distributed locking

The system is being developed as a **microservices-based application** with separate services responsible for events and bookings.

---

## 🏗️ Architecture

```text
                         Client
                           |
                           |
                  +--------+--------+
                  |                 |
                  v                 v
            Event Service     Booking Service
                  |                 |
                  |                 |
                  v                 v
            PostgreSQL         PostgreSQL
                  |                 |
                  |            Redis Lock
                  |                 |
                  +--------+--------+
                           |
                    Service-to-Service
                      Communication
```

### Services

#### Event Service

Responsible for event-related operations:

* Create events
* Retrieve events
* Retrieve event by ID
* Update events
* Delete events
* Check seat availability
* Reserve seats

**Port:** `8080`

#### Booking Service

Responsible for booking-related operations:

* Create bookings
* Retrieve bookings
* Retrieve booking by ID
* Update bookings
* Delete bookings
* Validate booking requests
* Communicate with Event Service
* Acquire Redis locks during booking

**Port:** `8081`

---

## 🛠️ Technology Stack

| Technology        | Purpose                         |
| ----------------- | ------------------------------- |
| Java              | Backend programming language    |
| Spring Boot       | Backend application framework   |
| Spring Web        | REST API development            |
| Spring Data JPA   | Database interaction            |
| Hibernate         | ORM                             |
| PostgreSQL        | Persistent relational database  |
| Spring Data Redis | Redis integration               |
| Redis             | Distributed locking             |
| Docker            | Running Redis in a container    |
| Maven             | Build and dependency management |
| JUnit 5           | Testing                         |
| Mockito           | Unit testing and mocking        |

---

## 📂 Project Structure

The project contains two independent Spring Boot services.

```text
event-booking-system/
│
├── event-service/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/ankita/eventservice/
│   │   │   │   ├── controller/
│   │   │   │   ├── service/
│   │   │   │   ├── repository/
│   │   │   │   ├── model/
│   │   │   │   ├── dto/
│   │   │   │   └── exception/
│   │   │   └── resources/
│   │   │       └── application.properties
│   │   └── test/
│   │
│   └── pom.xml
│
├── booking-service/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/ankita/bookingservice/
│   │   │   │   ├── controller/
│   │   │   │   ├── service/
│   │   │   │   ├── repository/
│   │   │   │   ├── model/
│   │   │   │   ├── dto/
│   │   │   │   ├── client/
│   │   │   │   ├── config/
│   │   │   │   └── exception/
│   │   │   └── resources/
│   │   │       └── application.properties
│   │   └── test/
│   │
│   └── pom.xml
│
└── README.md
```

---

# 🔹 Event Service

The Event Service owns all event-related data.

### Event Entity

```text
Event
├── id
├── name
├── location
└── availableSeats
```

### Event API

| Method | Endpoint               | Description     |
| ------ | ---------------------- | --------------- |
| GET    | `/events`              | Get all events  |
| GET    | `/events/{id}`         | Get event by ID |
| POST   | `/events`              | Create an event |
| PUT    | `/events/{id}`         | Update an event |
| DELETE | `/events/{id}`         | Delete an event |
| POST   | `/events/{id}/reserve` | Reserve seats   |

### Example: Create Event

```http
POST /events
```

Request:

```json
{
  "name": "Java Conference",
  "location": "Kolkata",
  "availableSeats": 100
}
```

Response:

```json
{
  "id": 1,
  "name": "Java Conference",
  "location": "Kolkata",
  "availableSeats": 100
}
```

The API returns `201 Created` when an event is successfully created.

---

# 🔹 Booking Service

The Booking Service owns booking-related data.

### Booking Entity

```text
Booking
├── id
├── eventId
├── customerName
└── numberOfSeats
```

### Booking API

| Method | Endpoint         | Description       |
| ------ | ---------------- | ----------------- |
| GET    | `/bookings`      | Get all bookings  |
| GET    | `/bookings/{id}` | Get booking by ID |
| POST   | `/bookings`      | Create a booking  |
| PUT    | `/bookings/{id}` | Update a booking  |
| DELETE | `/bookings/{id}` | Delete a booking  |

---

# 🔄 Booking Flow

When a user creates a booking:

```text
Client
   |
   | POST /bookings
   v
Booking Service
   |
   | Acquire Redis Lock
   v
Redis
   |
   | Lock acquired
   v
Booking Service
   |
   | Reserve seats
   v
Event Service
   |
   | Check availability
   | Decrease available seats
   v
Event Database
   |
   v
Booking Service
   |
   | Save booking
   v
Booking Database
   |
   v
Release Redis Lock
```

This ensures that concurrent booking requests for the same event are coordinated before modifying seat availability.

---

# 🔐 Redis Distributed Locking

Redis is used as a **distributed locking mechanism** for event booking.

For example, when booking seats for Event `1`, the Booking Service creates a lock similar to:

```text
lock:event:1
```

A unique token is stored as the lock value.

The lock is acquired using a "set if absent" operation with a short TTL.

Conceptually:

```text
SET lock:event:1 <unique-token> NX EX 5
```

Where:

* `NX` → create the key only if it doesn't already exist
* `EX 5` → automatically expire the lock after 5 seconds

This prevents multiple booking requests from simultaneously entering the critical section for the same event.

The lock is released only when the stored token belongs to the request that acquired it.

---

# 🐳 Redis with Docker

Redis is currently running inside a Docker container.

Container:

```text
event-booking-redis
```

Redis port:

```text
6379
```

The container is exposed as:

```text
localhost:6379
```

To start the Redis container:

```bash
docker run --name event-booking-redis -p 6379:6379 -d redis
```

To check running containers:

```bash
docker ps
```

To stop Redis:

```bash
docker stop event-booking-redis
```

To start it again:

```bash
docker start event-booking-redis
```

---

# 🗄️ Database Configuration

The application uses separate PostgreSQL databases for the two services.

```text
Event Service
     |
     └── eventdb

Booking Service
     |
     └── bookingdb
```

Each service owns its own database.

This follows an important microservices principle:

> A service should own its data instead of directly accessing another service's database.

---

# 🔗 Service-to-Service Communication

The Booking Service does not directly access the Event Service database.

Instead:

```text
Booking Service
      |
      | HTTP request
      v
Event Service
      |
      v
Event Database
```

The Booking Service contains an `EventServiceClient` that communicates with the Event Service.

For example, when a booking is created:

```text
Booking Service
      |
      | POST /events/{id}/reserve
      v
Event Service
      |
      v
Reserve seats
```

This keeps responsibilities separated between services.

---

# 📦 DTO Architecture

The project separates API DTOs from database entities.

For example:

```text
JSON Request
      |
      v
BookingRequest
      |
      v
Controller
      |
      v
Booking Entity
      |
      v
Service
      |
      v
PostgreSQL
      |
      v
Booking Entity
      |
      v
BookingResponse
      |
      v
JSON Response
```

### Why DTOs?

DTOs prevent the API layer from being tightly coupled to database entities.

They also allow the API contract and database model to evolve independently.

---

# ✅ Validation

The application uses Spring Boot's validation support.

Examples:

* Event name cannot be blank
* Location cannot be blank
* Available seats must be at least `1`
* Event ID is required for a booking
* Customer name cannot be blank
* Number of seats must be at least `1`

Example invalid request:

```json
{
  "eventId": 1,
  "customerName": "",
  "numberOfSeats": 0
}
```

The application returns a `400 Bad Request` with validation errors.

---

# ⚠️ Exception Handling

A global exception handler is used to provide consistent API error responses.

For example:

```json
{
  "name": "Event name is required",
  "location": "Location is required",
  "availableSeats": "Available seats must be at least 1"
}
```

This avoids putting repetitive exception-handling logic inside every controller.

---

# 🧪 Testing

The Event Service contains unit tests using:

* JUnit 5
* Mockito

Current tests cover scenarios such as:

* Retrieving an existing event
* Handling a nonexistent event
* Creating an event
* Updating an event
* Deleting an existing event
* Handling deletion of nonexistent events
* Handling updates for nonexistent events

Mockito is used to mock the repository layer so that service logic can be tested independently of PostgreSQL.

---

# ⚙️ Running the Project

## Prerequisites

Make sure the following are installed:

* Java
* Maven
* PostgreSQL
* Docker Desktop
* Git

---

## 1. Start Redis

```bash
docker start event-booking-redis
```

If the container doesn't exist yet:

```bash
docker run --name event-booking-redis -p 6379:6379 -d redis
```

Verify:

```bash
docker ps
```

---

## 2. Start Event Service

Navigate to:

```text
event-service/
```

Run:

```bash
./mvnw spring-boot:run
```

On Windows:

```bash
mvnw.cmd spring-boot:run
```

Event Service:

```text
http://localhost:8080
```

---

## 3. Start Booking Service

Navigate to:

```text
booking-service/
```

Run:

```bash
./mvnw spring-boot:run
```

On Windows:

```bash
mvnw.cmd spring-boot:run
```

Booking Service:

```text
http://localhost:8081
```

---

# 🎯 Key Backend Concepts Demonstrated

This project is designed to demonstrate practical backend concepts including:

* REST API development
* HTTP methods and status codes
* Spring Boot
* Dependency Injection
* Layered architecture
* Controller-Service-Repository pattern
* JPA/Hibernate
* PostgreSQL
* DTO pattern
* Input validation
* Global exception handling
* Microservices
* Service-to-service communication
* Distributed locking
* Redis
* Docker
* Unit testing
* Concurrent request handling

---

# 🔮 Planned Improvements & Roadmap

The project is intentionally being developed incrementally. Future improvements will expand the system from a basic microservices backend into a more complete full-stack application while introducing additional backend and system-design concepts.

### 🏗️ Microservices & Architecture

* [ ] API Gateway
* [ ] Service discovery
* [ ] Centralized configuration
* [ ] Improve service-to-service communication
* [ ] Resilience and failure handling
* [ ] Retry and timeout strategies
* [ ] Circuit breaker pattern
* [ ] Distributed tracing

### 📨 Asynchronous Communication

* [ ] Kafka or RabbitMQ
* [ ] Event-driven booking flow
* [ ] Notification Service
* [ ] Booking events
* [ ] Producer/consumer concepts
* [ ] Handling message failures
* [ ] Dead-letter queues

### 🔐 Security

* [ ] Spring Security
* [ ] JWT authentication
* [ ] User registration and login
* [ ] Role-based authorization
* [ ] Secure API endpoints
* [ ] Password hashing

### ⚡ Performance & Scalability

* [ ] Redis caching
* [ ] Improve distributed locking
* [ ] Database-level concurrency control
* [ ] Pagination and filtering
* [ ] Database indexing
* [ ] Query optimization
* [ ] Rate limiting
* [ ] Handling high-concurrency booking requests

### 🐳 DevOps & Deployment

* [ ] Dockerize Event Service
* [ ] Dockerize Booking Service
* [ ] Docker Compose for the complete application
* [ ] Environment-specific configuration
* [ ] CI/CD pipeline
* [ ] Jenkins/GitHub Actions
* [ ] Cloud deployment
* [ ] Basic application monitoring

### 🧪 Testing

* [ ] Additional unit tests
* [ ] Controller tests
* [ ] Integration tests
* [ ] Service-to-service integration tests
* [ ] Redis-related tests
* [ ] Concurrent booking tests
* [ ] API testing

### 🖥️ Frontend

* [ ] Build a frontend for the Event Booking System
* [ ] Event listing page
* [ ] Event details page
* [ ] Seat availability display
* [ ] Booking form
* [ ] Booking confirmation page
* [ ] My Bookings page
* [ ] Login and registration screens
* [ ] Basic responsive UI
* [ ] Connect frontend with backend REST APIs

### 🎨 Frontend Technology

The frontend may be developed using:

* React
* JavaScript/TypeScript
* HTML/CSS
* Tailwind CSS
* REST API integration

### 📊 Additional Backend Concepts

* [ ] API versioning
* [ ] Custom exception hierarchy
* [ ] Logging
* [ ] Request/response logging
* [ ] Correlation IDs
* [ ] Health checks
* [ ] Actuator
* [ ] Metrics
* [ ] Database transactions
* [ ] Optimistic/pessimistic locking
* [ ] Idempotency
* [ ] Saga pattern
* [ ] Distributed transactions
* [ ] Eventual consistency
* [ ] Caching strategies
* [ ] Database connection pooling

### 🧠 System Design Concepts

* [ ] Horizontal vs vertical scaling
* [ ] Load balancing
* [ ] Stateless services
* [ ] Database scaling
* [ ] Database replication concepts
* [ ] CAP theorem
* [ ] Consistency models
* [ ] Distributed systems fundamentals
* [ ] Fault tolerance
* [ ] High availability
* [ ] Concurrency and race conditions
* [ ] Caching strategies
* [ ] Message-driven architecture

---

# 🎯 Long-Term Goal

The goal is to evolve this project into a small but realistic **full-stack distributed application** that demonstrates the complete backend development lifecycle:


                         Event Booking System
                                  |
             ┌────────────────────┼────────────────────┐
             ↓                    ↓                    ↓
          Frontend            API Gateway          Services
             │                    │                    │
             │                    │          ┌─────────┴─────────┐
             │                    │          ↓                   ↓
             │                    │    Event Service       Booking Service
             │                    │          │                   │
             │                    │          ↓                   ↓
             │                    │     PostgreSQL            PostgreSQL
             │                    │                              │
             │                    │                           Redis
             │                    │                              │
             │                    │                        Message Broker
             │                    │                              │
             │                    │                     Notification Service
             └────────────────────┴──────────────────────────────┘


The project will gradually cover:

**Java → Spring Boot → REST APIs → PostgreSQL → JPA → DTOs → Validation → Testing → Microservices → Service Communication → Redis → Messaging → Security → Docker → CI/CD → Cloud → Frontend → System Design**

---

# 💡 System Design Considerations

Some of the main design questions explored through this project are:

### Why microservices?

To separate event management and booking responsibilities so that each service can evolve and scale independently.

### Why separate databases?

Each service owns its data and communicates through APIs rather than directly accessing another service's database.

### Why Redis?

To coordinate concurrent booking requests using a distributed lock.

### Why PostgreSQL?

It remains the persistent source of truth for event and booking data.

### Why Docker?

To run infrastructure such as Redis in a consistent and isolated environment and prepare the application for containerized deployment.

### What happens if Event Service fails?

The Booking Service should not create the booking if seat reservation fails.

### What happens if Booking Service saves the booking but seat reservation fails?

The booking operation must be ordered so that a booking is not persisted unless seat reservation succeeds.

### What happens if seat reservation succeeds but saving the booking fails?

This introduces a distributed transaction problem. A more advanced version could use compensation or a Saga-style approach.

---

# 📌 Project Status

**Currently in development**

The project is being built incrementally to understand each backend concept rather than simply assembling a large application from pre-built components.

Current major functionality:

Event Service
    ✅ REST APIs
    ✅ PostgreSQL
    ✅ JPA/Hibernate
    ✅ CRUD
    ✅ Validation
    ✅ DTOs
    ✅ Exception handling
    ✅ Seat reservation
    ✅ Unit tests

Booking Service
    ✅ REST APIs
    ✅ PostgreSQL
    ✅ JPA/Hibernate
    ✅ CRUD
    ✅ Validation
    ✅ DTOs
    ✅ Exception handling
    ✅ Event Service communication
    ✅ Redis integration
    ✅ Distributed locking
    🔄 Further improvements in progress

---

## 👩‍💻 Author

**Ankita Kondala**

Java Backend Developer | Spring Boot | PostgreSQL | Microservices
