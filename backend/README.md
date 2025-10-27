# Grabbler E-commerce API

![Java](https://img.shields.io/badge/Java-17+-blue.svg?logo=java)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.x.x-brightgreen.svg?logo=spring)
![Gradle](https://img.shields.io/badge/Gradle-8.x-blue.svg?logo=gradle)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-blue.svg?logo=postgresql)
![JWT](https://img.shields.io/badge/Security-JWT-black.svg?logo=jsonwebtokens)

## 1. Overview

**Grabbler** is a complete, secure, and robust REST API backend for an e-commerce platform. It is built with **Spring Boot** and secured using **Spring Security** and **JSON Web Tokens (JWT)**.

It provides a full range of e-commerce functionalities, including user authentication, a product catalog, category management, a persistent shopping cart, and a complete order processing workflow. The API features role-based access control, distinguishing between regular customers (`ROLE_CUSTOMER`) and administrators (`ROLE_ADMIN`) for managing store data.

## 2. Features

* **Authentication**: Secure user registration and login endpoints using JWT.
* **Role-Based Access Control**:
    * **Admin (`ROLE_ADMIN`)**: Full CRUD (Create, Read, Update, Delete) access to Products and Categories.
    * **Customer (`ROLE_CUSTOMER`)**: Access to shopping cart, placing orders, and managing their own profile/addresses.
    * **Public**: Access to view products, categories, and register/login.
* **Product Management**: Full CRUD operations for products, including search, pagination, and filtering by category or price range.
* **Category Management**: Full CRUD operations for product categories.
* **Shopping Cart**: Persistent shopping cart for authenticated users. Add, update, and remove items.
* **Order Management**: Customers can create orders from their cart. Admins can view and update order statuses.
* **Address Management**: Users can manage multiple shipping addresses.
* **API Documentation**: Integrated [OpenAPI (Swagger)](http://localhost:8080/swagger-ui/index.html) for live API documentation and testing.

## 3. Technology Stack

* **Framework**: Spring Boot
* **Security**: Spring Security 6, JSON Web Tokens (JWT)
* **Database**: Spring Data JPA (Hibernate)
* **Production DB**: PostgreSQL
* **Test DB**: H2 In-Memory Database
* **Build Tool**: Gradle
* **Validation**: `spring-boot-starter-validation` for request DTOs
* **API Docs**: SpringDoc OpenAPI (`springdoc-openapi-starter-webmvc-ui`)

## 4. API Endpoints

Here is a summary of the primary API endpoints. All endpoints are prefixed with `/api/v1`.

| Method | Endpoint | Description | Access |
| :--- | :--- | :--- | :--- |
| `POST` | `/auth/register` | Registers a new user (customer). | Public |
| `POST` | `/auth/login` | Authenticates a user and returns a JWT. | Public |
| `GET` | `/products` | Gets a paginated list of all products. Supports filtering. | Public |
| `GET` | `/products/{productId}` | Gets a single product by its ID. | Public |
| `GET` | `/products/search` | Searches for products by keyword. | Public |
| `POST` | `/products` | Creates a new product. | **Admin** |
| `PUT` | `/products/{productId}` | Updates an existing product. | **Admin** |
| `DELETE`| `/products/{productId}`| Deletes a product. | **Admin** |
| `GET` | `/categories` | Gets a list of all categories. | Public |
| `POST` | `/categories` | Creates a new category. | **Admin** |
| `PUT` | `/categories/{catId}` | Updates an existing category. | **Admin** |
| `DELETE`| `/categories/{catId}` | Deletes a category. | **Admin** |
| `GET` | `/cart` | Gets the current user's shopping cart. | **Customer** |
| `POST` | `/cart/items` | Adds an item to the user's cart. | **Customer** |
| `PUT` | `/cart/items/{itemId}` | Updates the quantity of an item in the cart. | **Customer** |
| `DELETE`| `/cart/items/{itemId}` | Removes an item from the cart. | **Customer** |
| `POST` | `/orders` | Creates a new order from the user's cart. | **Customer** |
| `GET` | `/orders` | Gets the current user's order history. | **Customer** |
| `GET` | `/orders/{orderId}` | Gets details for a specific order. | **Customer** |
| `PATCH` | `/orders/{orderId}` | Updates the status of an order. | **Admin** |
| `GET` | `/users/me` | Gets the current authenticated user's profile. | **Customer** / **Admin** |
| `GET` | `/users/address` | Gets all addresses for the current user. | **Customer** |
| `POST` | `/users/address` | Adds a new address for the current user. | **Customer** |

## 5. Getting Started

### Prerequisites

* JDK 17 or higher
* Gradle 8.x
* PostgreSQL server running

### Database Setup

1.  Ensure your PostgreSQL server is running.
2.  Create a new database named `grabblerdb`.
3.  Create a user (role) named `grabbler` with the password `password`.
4.  Grant this user full privileges on the `grabblerdb` database.

*Note: These values are set in `src/main/resources/application.yml`. You can modify this file to point to your own database instance.*

### Running the Application

1.  **Clone the repository:**
    ```sh
    git clone <repository-url>
    cd backend
    ```

2.  **Set Environment Variables:**
    The application uses a JWT secret key for signing tokens. Set this as an environment variable or in the `application.yml` file.
    ```sh
    export JWT_SECRET="b4AYNaI6lyiZZ4g9OQpmJnkqnna35FL0qJVFPWLEuno="
    ```
    *(This is the default secret from `application.yml`. It is **strongly** recommended to use a more secure, randomly generated key for production.)*

3.  **Build and Run using Gradle:**
    ```sh
    ./gradlew bootRun
    ```

The application will start on `http://localhost:8080`.

### Accessing API Documentation

Once the application is running, you can view and interact with the API documentation at:
[**http://localhost:8080/swagger-ui/index.html**](http://localhost:8080/swagger-ui/index.html)

## 6. Running Tests

The project is configured with a separate test profile that uses an **in-memory H2 database**. This allows tests to run independently without a running PostgreSQL instance.

To run the full suite of unit and integration tests:
```sh
./gradlew test
```
---

# grabber API (a more detailed explanation) 

## Authentication
This project uses JSON Web Tokens (JWT) for authentication. To access protected endpoints, you need to obtain a token and include it in the `Authorization` header of your requests.

### 1. Register a new user

To get a token, you first need to register a user. Send a `POST` request to the following endpoint with the user's details in the request body:
- Endpoint: `/api/v1/auth/register`
- Method: `POST`
- Request Body:
```json
{
    "firstName": "John",
    "lastName": "Doe",
    "email": "john.doe@example.com",
    "password": "yourpassword"
}
```

### 2. Obtain a token 

After registering, you can obtain a token by sending a `POST` request with the user's credentials to the login endpoint:

- Endpoint: `/api/v1/auth/login`
- Method: `POST`
- Request Body:
```json
{
    "email": "john.doe@example.com",
    "password": "yourpassword"
}
```

The server will respond with a JWT token:
```json
{
    "token": "your_jwt_token"
}
```

### 3. Access proted endpoints
To access a protected endpoint, include the JWT token in the Authorization header of your request, prefixed with "Bearer ".
For example, to access the `/api/v1/users/me` endpoint, which is protected, your request header should look like this:
```
Authorization: Bearer <your_jwt_token>
```
