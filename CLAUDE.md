# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

A cooking mini-program backend system built with Spring Boot + MyBatis Plus + Redis + MySQL, with a separate Vue 3 admin management interface. The system serves as the backend for a WeChat mini-program that allows users to browse recipes, manage favorites, shopping carts, and follow other users.

## Technology Stack

### Backend (Java)
- **Java 11** - Runtime environment
- **Spring Boot 2.7.14** - Main framework
- **MyBatis Plus 3.5.3.1** - ORM framework
- **MySQL 8.0** - Database
- **Redis** - Caching layer
- **JWT** - Authentication
- **MinIO** - File storage (external service at http://file.xuaq.top:19000)
- **Lombok** - Code generation
- **HuTool** - Java utilities
- **FastJSON** - JSON processing
- **Spring Security Crypto** - Password encryption (BCrypt)

### Frontend (Vue 3)
- **Vue 3.4** - Frontend framework
- **Vite** - Build tool
- **Element Plus** - UI component library
- **Vue Router 4** - Routing
- **Pinia** - State management
- **Axios** - HTTP client

## Development Commands

### Backend

```bash
# Install dependencies (first time)
mvn clean install

# Run development server (uses application-dev.yml)
mvn spring-boot:run

# Run with specific profile
mvn spring-boot:run -Dspring-boot.run.profiles=prod

# Package for production (uses application-prod.yml)
mvn clean package -Dspring-boot.run.profiles=prod

# Run packaged jar with production profile
java -jar target/cooking-app-1.0.0.jar --spring.profiles.active=prod
```

Backend runs on port 8080.

### Frontend

```bash
cd cooking-admin-web

# Install dependencies
npm install

# Run development server (port 3000)
npm run dev

# Build for production
npm run build

# Preview production build
npm run preview
```

### Database Setup

```bash
# Execute schema to create database and tables
mysql -u root -p < src/main/resources/db/schema.sql
```

### Docker Deployment

```bash
# Build Docker image
docker build -t cooking-app:latest .

# Run container with production profile
docker run -d \
  --name cooking-app \
  -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e DB_USERNAME=your_db_user \
  -e DB_PASSWORD=your_db_password \
  -e REDIS_HOST=your_redis_host \
  -e REDIS_PASSWORD=your_redis_password \
  -e JWT_SECRET=your_jwt_secret \
  -e WECHAT_APPID=your_wechat_appid \
  -e WECHAT_SECRET=your_wechat_secret \
  -e MINIO_ENDPOINT=your_minio_endpoint \
  -e MINIO_ACCESS_KEY=your_minio_access_key \
  -e MINIO_SECRET_KEY=your_minio_secret_key \
  -e MINIO_BUCKET_NAME=your_bucket_name \
  -v /path/to/logs:/app/logs \
  cooking-app:latest

# View logs
docker logs -f cooking-app

# Stop and remove container
docker stop cooking-app && docker rm cooking-app
```

## Architecture

### Layered Architecture

The backend follows a standard layered architecture:

```
src/main/java/com/cooking/
├── common/          # Cross-cutting concerns (Result wrapper, exceptions, interceptors)
├── config/          # Configuration classes (Redis, MyBatis Plus, MinIO, etc.)
├── controller/      # Request handling
│   ├── admin/       # Admin management endpoints (/admin/*)
│   └── app/         # Mini-program endpoints (/app/*)
├── dto/             # Data Transfer Objects
├── entity/          # Database entities
├── mapper/          # MyBatis Plus mappers
├── service/         # Business logic
│   └── impl/        # Service implementations
└── util/            # Utility classes
```

### Authentication System

The system uses **JWT-based authentication with dual token support**:

1. **App User Token** (`JwtAuthenticationInterceptor`): For mini-program users
   - Intercepts requests to `/app/*` endpoints (excluding login)
   - Token in request header: `token`
   - Extracts `userId` from token claims

2. **Admin Token** (`AdminAuthenticationInterceptor`): For admin management
   - Intercepts requests to `/admin/*` endpoints (excluding login)
   - Token in request header: `token`
   - Extracts `adminId` from token claims

**Default Admin Account**: Username `admin`, Password `123456` (BCrypt encrypted)

### Response Format

All API endpoints return a unified `Result<T>` wrapper:
- Success: `code=200` with data
- Failure: `code=500` with error message

### Database Design

Key tables:
- `user` - User accounts
- `admin` - Admin accounts
- `user_follow` - User following relationships
- `category` - Recipe categories
- `dish` - Recipes/dishes
- `dish_step` - Recipe preparation steps
- `dish_ingredient` - Recipe ingredients
- `user_favorite` - User favorites
- `shopping_cart` - Shopping cart items
- `banner` - Banner carousel

**Logical Delete**: MyBatis Plus logical deletion is enabled (field: `deleted`, value 1 = deleted)

### API Endpoint Structure

**Admin Endpoints** (`/admin/*`):
- `/admin/login` - Admin login (no auth required)
- `/admin/category/*` - Category CRUD
- `/admin/dish/*` - Dish CRUD and status management
- `/admin/banner/*` - Banner CRUD and status management

**Mini-program Endpoints** (`/app/*`):
- `/app/user/login` - WeChat login (no auth required)
- `/app/category/list` - Get categories (public)
- `/app/dish/*` - Dish listing, search, details (public, details increments view count)
- `/app/banner/list` - Get active banners (public)
- `/app/favorite/*` - Favorites management (auth required)
- `/app/cart/*` - Shopping cart management (auth required)
- `/app/follow/*` - User follow/unfollow management (auth required)

## Configuration

### Environment Configuration

The project supports multiple environments using Spring Boot profiles:

- **application.yml**: Default configuration file with active profile selection
- **application-dev.yml**: Development environment configuration (default)
- **application-prod.yml**: Production environment configuration

To switch environments, modify `spring.profiles.active` in `application.yml` or use command-line arguments:
```bash
# Development (default)
mvn spring-boot:run

# Production
mvn spring-boot:run -Dspring-boot.run.profiles=prod
```

### Development Environment (`application-dev.yml`)

- **Database**: Local MySQL at `localhost:3306/cooking_db` (no SSL)
- **Redis**: Localhost:6379 (no password)
- **Logging**: Debug level for `com.cooking`, SQL logging enabled
- **MyBatis Plus**: SQL output to console (`StdOutImpl`)

### Production Environment (`application-prod.yml`)

- **Database**: Production MySQL server (SSL enabled, use environment variables)
  - `DB_USERNAME`, `DB_PASSWORD`: Database credentials
- **Redis**: Production Redis server (password required via `REDIS_PASSWORD`)
- **Logging**: Info level for `com.cooking`, SQL logging disabled (`NoLoggingImpl`)
  - Logs written to `logs/cooking-app.log` (10MB max, 30 days retention)
- **Connection Pools**: Optimized for production (max-active: 20)
- **External Configuration**: Uses environment variables for sensitive data:
  - `JWT_SECRET`: JWT signing key
  - `WECHAT_APPID`, `WECHAT_SECRET`: WeChat mini-program credentials
  - `MINIO_ENDPOINT`, `MINIO_ACCESS_KEY`, `MINIO_SECRET_KEY`, `MINIO_BUCKET_NAME`: MinIO configuration

### Application Configuration

Key configuration sections (common to all environments):
- **MySQL**: `spring.datasource` - Database connection to `cooking_db`
- **Redis**: `spring.redis` - Redis caching
- **JWT**: Token secret and 7-day expiration (604800000ms)
- **MinIO**: External file storage service
- **WeChat**: AppID and AppSecret for mini-program login
- **File Upload**: 10MB limit

### Frontend Proxy Configuration

The frontend uses Vite proxy to forward API requests:
- All `/admin` requests proxied to `http://localhost:8080`
- Runs on port 3000

## Key Implementation Details

### MinIO File Storage

Images are stored on an external MinIO service (http://file.xuaq.top:19000). The system automatically creates a bucket named `test` if it doesn't exist.

### User ID Extraction

User and admin IDs are extracted from JWT tokens in interceptors. Controllers do not need to receive these as request parameters - they're available through the token claims.

### Automatic Counters

Several operations automatically update counters:
- Viewing dish details increments `view_count`
- Sharing a dish increments `share_count`
- Favoriting/unfavoriting updates `favorite_count`

### Caching

Redis is integrated for caching (configured but usage depends on specific implementation needs).

## Important Notes

1. **Token Header**: Both admin and app endpoints expect the JWT token in the `token` request header
2. **Password Security**: Admin passwords are BCrypt encrypted; the default admin password is `123456`
3. **Logical Delete**: All "deletions" are logical deletes - data remains in database
4. **WeChat Integration**: Requires actual WeChat mini-program AppID and AppSecret for production use
5. **File Storage**: Depends on external MinIO service availability
6. **Development vs Production**: Development uses Vite proxy; production requires Nginx configuration for CORS and static files
7. **Environment Variables**: Production environment requires setting environment variables for sensitive configuration (database passwords, JWT secret, etc.) - never commit actual secrets to version control
8. **Profile Switching**: Default profile is `dev`; change `spring.profiles.active` in `application.yml` or use command-line arguments to switch to `prod`
9. **Docker Multi-stage Build**: The Dockerfile uses multi-stage builds - Maven builds in a builder stage, then only the JAR is copied to a lightweight JRE runtime image, reducing final image size
10. **Docker Volume Mounting**: When running with Docker, mount a volume to `/app/logs` to persist logs outside the container