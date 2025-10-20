# grabber API 

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
