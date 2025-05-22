# Hexagonal Architecture Demo

This project demonstrates the implementation of Hexagonal Architecture (also known as Ports and Adapters) using Spring Boot. The developed example is a product management system that implements basic CRUD operations following the principles of hexagonal architecture.

[Here](https://leandrofranchi.medium.com/hexagonal-architecture-with-spring-boot-building-truly-scalable-systems-7948472406ed) a link to post.

## Project Structure

The project follows the following package structure:

```
src/main/java/com/hexagonal/demo
│
├── application/           # Use cases that orchestrate the domain
│   ├── port/
│   │   ├── input/         # Primary ports (input)
│   │   └── output/        # Secondary ports (output)
│   └── service/           # Use case implementations
│
├── domain/                # Entities, rules, and business logic
│   ├── model/             # Domain objects
│   ├── exception/         # Domain exceptions
│   └── vo/                # Value Objects
│
├── infrastructure/        # Concrete implementations (adapters)
│   ├── input/             # Primary adapters (inputs)
│   │   ├── rest/          # REST controllers
│   │   └── config/        # Input adapter configurations
│   │
│   └── output/            # Secondary adapters (outputs)
│       ├── persistence/   # Repositories, JPA entities
│       └── config/        # Output adapter configurations
│
└── HexagonalDemoApplication.java
```

## Technologies Used

- Java 17
- Spring Boot 3.2.x
- Spring Data JPA
- H2 Database (for development)
- MapStruct (for object mapping)
- Lombok (to reduce boilerplate code)
- JUnit 5 & Mockito (for testing)

## Requirements

- Java 17 or higher
- Maven 3.6.x or higher

## How to Run

1. Clone the repository:
```bash
git clone https://github.com/your-username/hexagonal-architecture-demo.git
cd hexagonal-architecture-demo
```

2. Build the project:
```bash
mvn clean package
```

3. Run the application:
```bash
java -jar target/hexagonal-demo-0.0.1-SNAPSHOT.jar
```

Or run using Maven:
```bash
mvn spring-boot:run
```

The application will be available at: http://localhost:8080

## API Endpoints

The API provides the following endpoints:

| Method | URL                      | Description                    |
|--------|--------------------------|--------------------------------|
| GET    | /api/products            | List all products              |
| GET    | /api/products/{id}       | Find a product by ID           |
| POST   | /api/products            | Create a new product           |
| PUT    | /api/products/{id}       | Update an existing product     |
| DELETE | /api/products/{id}       | Remove a product               |
| PATCH  | /api/products/{id}/activate | Activate a product          |
| PATCH  | /api/products/{id}/deactivate | Deactivate a product      |

## License

This project is licensed under the MIT license - see the [LICENSE](LICENSE) file for details.
