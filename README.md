# ShortURL - URL Shortening Service

A Spring Boot application that provides URL shortening functionality using one-way SHA-256 hashing with Base62 encoding.

## 📋 Table of Contents
- [Design considerations](#design-considerations)
- [Prerequisites](#prerequisites)
- [Running Locally](#running-locally)
- [Running with Docker](#running-with-docker)

## Design considerations
- The system should be highly available. This is required because if the service is down, all the URL redirections will fail.
- The system will be read-heavy. We might be able to improve read performance by caching frequently accessed URLs.
- The system should clean up all and expired URLs to free up storage space.
- The real implementation should be based on a persistent storage (e.g., relational database, NoSQL database) instead of in-memory storage to ensure data durability and scalability.
- Instead of generating short URLs during API call, we can pre-generate a batch of them offline and store them in the datastore to reduce latency during URL shortening requests.
- In a production environment, we should consider rate limiting requests to prevent abuse of the URL shortening service.

## 🔧 Prerequisites

Before running this application, ensure you have the following installed:

- **Java 21** or higher
- **Maven 3.8+** (or use the included Maven wrapper)
- **Docker** (optional, for containerized deployment)
- **Docker Compose** (optional, for easy container orchestration)

## 🚀 Running Locally

### Option 1: Using Installed Maven

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
   java -jar target/shorturl-0.0.1-SNAPSHOT.jar
   ```

### Option 2: Using Your IDE

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

Bruno collection can be found in the project ROOT directory as [ShortUrlAPI](ShortUrlAPI)

### Create Short URL

**Endpoint:** `POST api/v1/url/short`

**Request Body:**
```json
{
  "url": "https://www.originenergy.com.au"
}
```

**Response:**
```json
{
  "shortUrl": "http://localhost:8080/abc123X"
}
```

### Redirect to Original URL

**Endpoint:** `GET /{shortUrl}`

**Example:** `http://localhost:8080/abc123X`

**Response:** Redirects to the original URL

### Get Original URL details

**Endpoint:** `GET /api/v1/url/full?shortUrl={shortUrl}`

**Example:** `http://localhost:8080/api/v1/url/full?shortUrl=http://localhost:8080/abc123X`

**Response:** Redirects to the original URL
```json
{
  "fullUrl": "https://www.originenergy.com.au",
  "accessCount": 0,
  "createdAt": "2026-01-16T12:26:55.104669",
  "expiresAt": "2027-01-11T12:26:55.105089",
  "lastAccessedAt": "2026-01-16T12:26:55.105073"
}
```

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
