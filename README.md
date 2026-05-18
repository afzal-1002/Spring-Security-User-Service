# User Service

Spring Boot user-management service with JWT authentication, role-based authorization, MySQL persistence, and Redis configuration.

## Technology Stack

- Java 21
- Spring Boot 4.0.6
- Spring Web MVC
- Spring Security
- Spring Data JPA / JDBC
- MySQL
- Redis
- JJWT 0.12.5
- Lombok
- Maven

## Project Structure

```text
src/main/java/com/afzora/nova/cart
├── config                  # Authentication, JWT, Redis, and Security configuration
├── controller              # REST API controllers
├── dto                     # Request and response DTOs
├── entity                  # JPA entities
├── exception               # Custom exceptions and global exception handler
├── jwtauthfilter           # JWT request filter
├── mapper                  # Entity/DTO mapping
├── repository              # Spring Data repositories
├── security                # UserDetails and UserDetailsService implementations
└── service                 # Service interfaces and implementations
```

## Build Steps Implemented

### 1. Created the Users Entity

File: `src/main/java/com/afzora/nova/cart/entity/Users.java`

The `Users` entity represents application users and is mapped to the `users` table.

Main fields:

- `id`
- `firstName`
- `lastName`
- `userName`
- `email`
- `phoneNumber`
- `password`
- `roles`
- `enabled`
- `accountNonLocked`
- `failedLoginAttempts`
- `lockTime`
- `emailVerified`
- `phoneVerified`
- `passwordChangedAt`
- `lastLoginAt`
- `profileImageUrl`
- `provider`
- `providerId`
- `createdAt`
- `updatedAt`

Important implementation details:

- `@Entity` and `@Table(name = "users")` map the class to the database.
- `@Id` and `@GeneratedValue(strategy = GenerationType.IDENTITY)` create the primary key.
- `userName`, `email`, and `phoneNumber` are unique fields.
- Password is stored as an encoded value.
- `@ManyToMany(fetch = FetchType.EAGER)` connects users to roles through the `user_roles` join table.
- `@PrePersist` sets default values before first save.
- `@PreUpdate` updates the `updatedAt` timestamp.

### 2. Created the Role Entity

File: `src/main/java/com/afzora/nova/cart/entity/Role.java`

The `Role` entity represents authorization roles and is mapped to the `roles` table.

Fields:

- `id`
- `name`
- `description`

The role `name` is unique. Examples of role names used in the project are:

- `ADMIN`
- `MANAGER`
- `CUSTOMER`

### 3. Created Repositories

Files:

- `src/main/java/com/afzora/nova/cart/repository/UsersRepository.java`
- `src/main/java/com/afzora/nova/cart/repository/RoleRepository.java`

`UsersRepository` extends `JpaRepository<Users, Long>` and provides:

- `findByUserName(String userName)`
- `findByEmail(String userEmail)`
- `existsByEmail(String userEmail)`
- `existsByUserName(String userName)`

`RoleRepository` extends `JpaRepository<Role, Long>` and provides:

- `findByName(String name)`
- `existsByName(String name)`

These repositories are used by service classes and security classes to read and write users and roles.

### 4. Created DTOs

Request DTOs:

- `RegisterRequest`
- `LoginRequest`
- `RefreshTokenRequest`
- `CreateUserRequest`
- `UpdateUserRequest`

Response DTOs:

- `AuthResponse`
- `UserResponse`
- `UserProfileResponse`
- `ErrorResponse`

DTOs keep API input and output separate from the database entities.

### 5. Created Mappers

Files:

- `AuthMapper`
- `UsersMapper`
- `ErrorResponseMapper`

`AuthMapper` builds the authentication response after register, login, or token refresh.

`UsersMapper` converts `Users` entities into:

- `UserResponse`
- `UserProfileResponse`

### 6. Created JwtConfig

File: `src/main/java/com/afzora/nova/cart/config/JwtConfig.java`

`JwtConfig` reads JWT settings from `application.properties`:

```properties
jwt.secret-key=...
jwt.access-expiration=60000
jwt.refresh-expiration=300000
```

These values are used by `JwtServiceImplementation` to create and validate tokens.

### 7. Created JwtService

Files:

- `src/main/java/com/afzora/nova/cart/service/JwtService.java`
- `src/main/java/com/afzora/nova/cart/service/implementation/JwtServiceImplementation.java`

The JWT service handles token logic.

Implemented methods:

- `generateAccessToken(UserDetails userDetails)`
- `generateRefreshToken(UserDetails userDetails)`
- `extractUsername(String token)`
- `extractAllClaims(String token)`
- `isTokenValid(String token, UserDetails userDetails)`
- `isRefreshTokenValid(String token, UserDetails userDetails)`
- `isTokenExpired(String token)`
- `getSignInKey()`
- `refreshAccessToken(String refreshToken)`

The implementation uses JJWT to:

- create signed JWT tokens
- extract claims
- extract the username from the subject
- check token expiration
- validate token ownership
- generate a new access token from a valid refresh token

### 8. Created Security UserDetails

Files:

- `src/main/java/com/afzora/nova/cart/security/CustomUserDetails.java`
- `src/main/java/com/afzora/nova/cart/security/CustomUserDetailsService.java`

`CustomUserDetails` adapts the project `Users` entity to Spring Security's `UserDetails` contract.

Important behavior:

- `getUsername()` returns `users.getUserName()`.
- `getPassword()` returns the encoded password.
- `getAuthorities()` converts each `Role` into a Spring Security authority using the `ROLE_` prefix.

Example:

```text
CUSTOMER -> ROLE_CUSTOMER
ADMIN    -> ROLE_ADMIN
MANAGER  -> ROLE_MANAGER
```

`CustomUserDetailsService` implements `UserDetailsService`.

It loads users from `UsersRepository` by username and returns `CustomUserDetails`.

Spring Security uses this class during login authentication and JWT validation.

### 9. Created AuthenticationConfig

File: `src/main/java/com/afzora/nova/cart/config/AuthenticationConfig.java`

`AuthenticationConfig` defines authentication-related beans:

- `AuthenticationManager`
- `AuthenticationProvider`
- `PasswordEncoder`

Important implementation details:

- `DaoAuthenticationProvider` uses `CustomUserDetailsService`.
- `BCryptPasswordEncoder` is used to encode and verify passwords.
- `AuthenticationManager` is used by `AuthServiceImplementation` during login.

### 10. Created JwtAuthenticationFilter

File: `src/main/java/com/afzora/nova/cart/jwtauthfilter/JwtAuthenticationFilter.java`

`JwtAuthenticationFilter` extends `OncePerRequestFilter`.

Request flow:

1. Read the `Authorization` header.
2. Check that it starts with `Bearer `.
3. Extract the JWT token.
4. Extract the username from the token.
5. Load the user with `CustomUserDetailsService`.
6. Validate the token with `JwtService`.
7. Create a `UsernamePasswordAuthenticationToken`.
8. Set the authenticated user in `SecurityContextHolder`.
9. Continue the filter chain.

This filter makes authenticated JWT requests available to Spring Security.

### 11. Created SecurityConfig

File: `src/main/java/com/afzora/nova/cart/config/SecurityConfig.java`

`SecurityConfig` defines the application security rules.

Implemented rules:

- CSRF is disabled.
- Session management is stateless.
- Form login is disabled.
- `/auth/**` is public.
- `/admin/**` requires `ADMIN`.
- `/manager/**` requires `MANAGER`.
- `/customer/**` requires `CUSTOMER`.
- Any other request must be authenticated.
- `JwtAuthenticationFilter` runs before `UsernamePasswordAuthenticationFilter`.

This makes the application use JWT instead of server sessions.

### 12. Created AuthService

Files:

- `src/main/java/com/afzora/nova/cart/service/AuthService.java`
- `src/main/java/com/afzora/nova/cart/service/implementation/AuthServiceImplementation.java`

`AuthService` handles authentication workflows.

Implemented methods:

- `register(RegisterRequest request)`
- `login(LoginRequest request)`
- `refreshToken(RefreshTokenRequest request)`

Register flow:

1. Read requested roles.
2. Use `CUSTOMER` when no role is provided.
3. Load or create roles through `RoleService`.
4. Encode the password with `PasswordEncoder`.
5. Save the user.
6. Generate access and refresh tokens.
7. Return `AuthResponse`.

Login flow:

1. Authenticate username and password with `AuthenticationManager`.
2. Load the user from `UsersRepository`.
3. Generate access and refresh tokens.
4. Return `AuthResponse`.

Refresh-token flow:

1. Read refresh token from request.
2. Extract username from refresh token.
3. Load the user.
4. Validate refresh token.
5. Generate a new access token.
6. Return `AuthResponse`.

### 13. Created UsersService

Files:

- `src/main/java/com/afzora/nova/cart/service/UsersService.java`
- `src/main/java/com/afzora/nova/cart/service/implementation/UsersServiceImplementation.java`

`UsersService` handles user-management operations.

Implemented methods:

- `findByUserName(String username)`
- `findByEmail(String email)`
- `findById(Long id)`
- `getProfile()`
- `getAllUsers()`
- `createCustomer(CreateUserRequest request)`
- `updateUser(Long id, UpdateUserRequest request)`
- `deleteUser(Long id)`
- `existsByEmail(String email)`
- `existsByUserName(String username)`

Important implementation details:

- Checks duplicate email and username before creating users.
- Encodes passwords before saving users.
- Uses `RoleService` to resolve roles.
- Uses `SecurityContextHolder` to load the current authenticated user profile.
- Validates which roles can be assigned by the current user.

Role assignment rules:

- Unauthenticated users can only create `CUSTOMER` users.
- `CUSTOMER` users cannot create `ADMIN` or `MANAGER` users.
- `MANAGER` users cannot create `ADMIN` users.

### 14. Created RoleService and Implementation

Files:

- `src/main/java/com/afzora/nova/cart/service/RoleService.java`
- `src/main/java/com/afzora/nova/cart/service/implementation/RoleServiceImplementation.java`

`RoleService` handles role-management operations.

Implemented methods:

- `createRole(Role role)`
- `createRoles(List<Role> roles)`
- `findByName(String name)`
- `findById(Long id)`
- `getAllRoles()`
- `deleteRole(Long id)`
- `findRolesByNames(Set<String> roleNames)`

Important implementation details:

- Role names are normalized to uppercase.
- Duplicate roles are not saved.
- `findRolesByNames` creates missing roles automatically.

### 15. Created Controllers

Files:

- `AuthController`
- `UsersController`
- `RoleController`

Controllers expose the service layer through REST APIs.

## API Endpoints

### Authentication

Base path: `/auth`

| Method | Endpoint | Description |
| --- | --- | --- |
| POST | `/auth/register` | Register a new user |
| POST | `/auth/login` | Login and receive tokens |
| POST | `/auth/refresh` | Generate a new access token |

Register request:

```json
{
  "firstName": "Afzal",
  "lastName": "Khan",
  "userName": "afzal",
  "email": "afzal@example.com",
  "phoneNumber": "1234567890",
  "password": "password123",
  "roles": ["CUSTOMER"]
}
```

Login request:

```json
{
  "userName": "afzal",
  "password": "password123"
}
```

Refresh token request:

```json
{
  "refreshToken": "your-refresh-token"
}
```

Authentication response:

```json
{
  "accessToken": "jwt-access-token",
  "refreshToken": "jwt-refresh-token",
  "tokenType": "Bearer",
  "expiresIn": 60000,
  "roles": ["CUSTOMER"]
}
```

### Users

Base path: `/users`

| Method | Endpoint | Description |
| --- | --- | --- |
| POST | `/users` | Create a user |
| GET | `/users` | Get all users |
| GET | `/users/{id}` | Get user by ID |
| GET | `/users/username/{username}` | Get user by username |
| GET | `/users/email/{email}` | Get user by email |
| GET | `/users/profile` | Get current authenticated user profile |
| PUT | `/users/{id}` | Update a user |
| DELETE | `/users/{id}` | Delete a user |

Create user request:

```json
{
  "firstName": "Ali",
  "lastName": "Ahmad",
  "userName": "ali",
  "email": "ali@example.com",
  "phoneNumber": "1234567890",
  "password": "password123",
  "roles": ["CUSTOMER"]
}
```

Update user request:

```json
{
  "firstName": "Ali",
  "lastName": "Khan",
  "email": "ali.khan@example.com",
  "phoneNumber": "1234567890"
}
```

### Roles

Base path: `/roles`

| Method | Endpoint | Description |
| --- | --- | --- |
| POST | `/roles` | Create one role |
| POST | `/roles/bulk` | Create multiple roles |
| GET | `/roles` | Get all roles |

Create role request:

```json
{
  "name": "ADMIN",
  "description": "Administrator role"
}
```

Bulk create roles request:

```json
[
  {
    "name": "ADMIN",
    "description": "Administrator role"
  },
  {
    "name": "MANAGER",
    "description": "Manager role"
  },
  {
    "name": "CUSTOMER",
    "description": "Customer role"
  }
]
```

## Security Flow

### Public Requests

Requests to `/auth/**` are allowed without a JWT token.

Example:

```text
POST /auth/login
POST /auth/register
POST /auth/refresh
```

### Protected Requests

All other requests need an `Authorization` header:

```text
Authorization: Bearer <access-token>
```

The JWT filter validates the token and stores the authenticated user in the Spring Security context.

### Role-Based Access

The current security rules include:

```text
/admin/**    -> ADMIN
/manager/**  -> MANAGER
/customer/** -> CUSTOMER
any other    -> authenticated
```

Spring Security expects role authorities in this format:

```text
ROLE_ADMIN
ROLE_MANAGER
ROLE_CUSTOMER
```

`CustomUserDetails` adds the `ROLE_` prefix automatically.

## Configuration

Main configuration file:

```text
src/main/resources/application.properties
```

Database configuration:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/user-db
spring.datasource.username=root
spring.datasource.password=root
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
```

JPA configuration:

```properties
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect
```

JWT configuration:

```properties
jwt.secret-key=-----
jwt.access-expiration=60000
jwt.refresh-expiration=300000
```

Redis configuration:

```properties
spring.data.redis.host=localhost
spring.data.redis.port=6379
spring.data.redis.password=yourPassword
spring.data.redis.timeout=60000
```

## How to Run

### 1. Start MySQL

Create the database:

```sql
CREATE DATABASE nova_user;
```

Make sure the username and password match `application.properties`.

### 2. Start Redis

Start Redis locally on port `6379`.

### 3. Run the Application

Using Maven wrapper:

```bash
./mvnw spring-boot:run
```

Or build and run the jar:

```bash
./mvnw clean package
java -jar target/User-Service-0.0.1-SNAPSHOT.jar
```

The application starts on the default Spring Boot port:

```text
http://localhost:8080
```

## Example Authorization Usage

After login or register, copy the `accessToken` from the response and use it in protected requests:

```bash
curl -H "Authorization: Bearer <access-token>" \
  http://localhost:8080/users/profile
```

## Implementation Summary

The project was built in this order:

1. Created `Users` entity.
2. Created `Role` entity.
3. Added JPA repositories for users and roles.
4. Added request and response DTOs.
5. Added mappers for authentication and user responses.
6. Added `JwtConfig` to read JWT properties.
7. Added `JwtService` and `JwtServiceImplementation`.
8. Added `CustomUserDetails`.
9. Added `CustomUserDetailsService`.
10. Added `AuthenticationConfig`.
11. Added `JwtAuthenticationFilter`.
12. Added `SecurityConfig`.
13. Added `AuthService` and implementation.
14. Added `UsersService` and implementation.
15. Added `RoleService` and implementation.
16. Added controllers for auth, users, and roles.
17. Added application properties for MySQL, JPA, JWT, and Redis.

