# ShortURL - URL Shortening Service

A Spring Boot application that provides URL shortening functionality using one-way SHA-256 hashing with Base62 encoding.

## 📋 Table of Contents
- [Prerequisites](#prerequisites)
- [Running Locally](#running-locally)
- [Running with Docker](#running-with-docker)
- [API Documentation](#api-documentation)
- [Project Structure](#project-structure)
- [Technologies Used](#technologies-used)
- [Configuration](#configuration)

## 🔧 Prerequisites

Before running this application, ensure you have the following installed: 

- **Java 21** or higher
- **Maven 3.8+** (or use the included Maven wrapper)
- **Docker** (optional, for containerized deployment)
- **Docker Compose** (optional, for easy container orchestration)

## 🚀 Running Locally

### Option 1: Using Maven Wrapper (Recommended)

1. **Clone the repository:**
   ```bash
   git clone https://github.com/slavau/shorturl. git
   cd shorturl
   ```

2. **Build the application:**
   ```bash
   ./mvnw clean package
   ```

3. **Run the application:**
   ```bash
   ./mvnw spring-boot:run
   ```

4. **Access the application:**
   - Open your browser and navigate to:  `http://localhost:8080`

### Option 2: Using Installed Maven

1. **Clone the repository:**
   ```bash
   git clone https://github.com/slavau/shorturl.git
   cd shorturl
   ```

2. **Build the application:**
   ```bash
   mvn clean package
   ```

3. **Run the JAR file:**
   ```bash
   java -jar target/shorturl-0.0.1-SNAPSHOT. jar
   ```

### Option 3: Using Your IDE

1. **Import the project:**
   - Open your IDE (IntelliJ IDEA, Eclipse, VS Code)
   - Import as a Maven project
   - Wait for dependencies to download

2. **Run the application:**
   - Locate `ShorturlApplication.java` in `src/main/java/com/example/origin/technical/exercise/shorturl/`
   - Right-click and select "Run" or "Debug"

3. **Access the application:**
   - Application will start on `http://localhost:8080`

## 🐳 Running with Docker

### Using Docker (Single Container)

1. **Build the Docker image:**
   ```bash
   docker build -t shorturl:latest .
   ```

2. **Run the container:**
   ```bash
   docker run -d -p 8080:8080 --name shorturl-app shorturl:latest
   ```

3. **View logs:**
   ```bash
   docker logs -f shorturl-app
   ```

4. **Stop the container:**
   ```bash
   docker stop shorturl-app
   docker rm shorturl-app
   ```

### Using Docker Compose (Recommended)

1. **Start the application:**
   ```bash
   docker-compose up -d
   ```

2. **View logs:**
   ```bash
   docker-compose logs -f
   ```

3. **Stop the application:**
   ```bash
   docker-compose down
   ```

4. **Rebuild and restart:**
   ```bash
   docker-compose up -d --build
   ```

## 📚 API Documentation

### Create Short URL

**Endpoint:** `POST /api/v1/shorten`

**Request Body:**
```json
{
  "url": "https://www.example.com/very/long/url"
}
```

**Response:**
```json
{
  "shortUrl": "abc123X",
  "originalUrl": "https://www.example.com/very/long/url",
  "createdAt": "2026-01-15T10:30:00"
}
```

### Redirect to Original URL

**Endpoint:** `GET /{shortUrl}`

**Example:** `http://localhost:8080/abc123X`

**Response:** Redirects to the original URL

## 📁 Project Structure

```
shorturl/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/example/origin/technical/exercise/shorturl/
│   │   │       ├── ShorturlApplication.java
│   │   │       ├── config/
│   │   │       │   └── UrlShortenerConfig. java
│   │   │       ├── model/
│   │   │       │   └── UrlMapping. java
│   │   │       ├── repository/
│   │   │       │   ├── UrlMappingRepository.java
│   │   │       │   └── InMemoryUrlMappingRepository.java
│   │   │       └── service/
│   │   │           └── UrlShortenerService.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       └── java/
│           └── com/example/origin/technical/exercise/shorturl/
│               └── repository/
│                   └── InMemoryUrlMappingRepositoryTest.java
├── Dockerfile
├── docker-compose. yml
├── . dockerignore
├── pom.xml
├── openapi.yaml
└── README. md
```

## 🛠️ Technologies Used

- **Java 21** - Programming language
- **Spring Boot 4.0.1** - Application framework
- **Maven** - Build tool
- **Lombok** - Boilerplate code reduction
- **OpenAPI 3.0** - API specification
- **JUnit 5** - Testing framework
- **Docker** - Containerization
- **Google Distroless** - Minimal container base image

## ⚙️ Configuration

### Application Properties

Create or modify `src/main/resources/application.properties`:

```properties
# Server Configuration
server.port=8080

# URL Shortener Configuration
url.shortener.base-url=http://localhost:8080
url.shortener.short-url-length=7

# Logging Configuration
logging.level.root=INFO
logging.level.com.example.origin. technical.exercise.shorturl=DEBUG
```

### Environment Variables

You can override properties using environment variables:

```bash
# Using Maven
URL_SHORTENER_BASE_URL=https://myshorturl.com ./mvnw spring-boot:run

# Using Docker
docker run -d -p 8080:8080 \
  -e URL_SHORTENER_BASE_URL=https://myshorturl.com \
  -e URL_SHORTENER_SHORT_URL_LENGTH=8 \
  shorturl:latest
```

## 🔐 Security Features

- **One-way SHA-256 hashing** - Cannot reverse engineer original URLs from short codes
- **Base62 encoding** - URL-safe characters (0-9, A-Z, a-z)
- **Non-sequential IDs** - Prevents enumeration attacks
- **Distroless container** - Minimal attack surface (no shell, no package manager)
- **Non-root user** - Container runs as non-privileged user

## 🧪 Running Tests

### Run all tests: 
```bash
./mvnw test
```

### Run specific test class:
```bash
./mvnw test -Dtest=InMemoryUrlMappingRepositoryTest
```

### Run tests with coverage:
```bash
./mvnw clean test jacoco:report
```

## 🐛 Troubleshooting

### Port Already in Use

If port 8080 is already in use: 

```bash
# Change port in application.properties
server.port=9090

# Or use environment variable
SERVER_PORT=9090 ./mvnw spring-boot:run

# Or with Docker
docker run -d -p 9090:8080 shorturl:latest
```

### Maven Build Fails

```bash
# Clean and rebuild
./mvnw clean install

# Skip tests if needed
./mvnw clean package -DskipTests
```

### Docker Build Issues

```bash
# Clean Docker cache
docker system prune -a

# Rebuild without cache
docker build --no-cache -t shorturl:latest .
```

## 📊 Performance

- **Image Size:** ~80MB (Distroless)
- **Startup Time:** ~5-10 seconds
- **Memory Usage:** ~256MB (typical)
- **Hash Generation:** O(1) constant time

## 📝 License

This project is created for educational purposes.

## 👥 Author

- **GitHub:** [@slavau](https://github.com/slavau)

## 🤝 Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

---

**Happy URL Shortening! 🚀**
