## 📍IAM Endpoints

| route               | description
|----------------------|-----------------------------------------------------
| <kbd>POST /api/user/auth/signup</kbd>     | See [request details](#iam-register)
| <kbd>POST /api/user/auth/login</kbd>     | See [request details](#iam-login)
| <kbd>GET /api/user/</kbd>     | See [request details](#iam-get-all-users)
| <kbd>GET /api/user/cpf/{cpf}</kbd>     | See [request details](#iam-get-user-by-cpf)
| <kbd>DELETE /api/user/{id}</kbd>     | See [request details](#iam-delete-user)

<h3 id="iam-register">POST /api/user/auth/signup</h3>

- **Request Body**:
```json
{
    "email": "string",
    "password": "string",
    "cpf": "string",
    "role": "string",
    "fullName": "string"
}
```

- **Response Body**:
```json
{
    "id": "string",
    "email": "string",
    "password": "string",
    "cpf": "string",
    "role": "string",
    "fullName": "string"
}
```

<h3 id="iam-login">POST /api/user/auth/login</h3>

**REQUEST**
```json
{
    "cpf": "string",
    "password": "string"
}
```

**RESPONSE**
```json
{
    "token": "string",
    "expirationTime": "string"
}
```

<h3 id="iam-get-all-users">GET /api/user/</h3>

**RESPONSE**
```json
[
    {
        "id": "string",
        "name": "string",
        "email": "string",
        "cpf": "string",
        "role": "string",
        "password": "string",
    },
    /* All other users */
]
```

<h3 id="iam-get-user-by-cpf">GET /api/user/cpf/{cpf}</h3>

**RESPONSE**
```json
{
    "id": "string",
    "name": "string",
    "email": "string",
    "cpf": "string",
    "role": "string"
}
```

<h3 id="iam-delete-user">DELETE /api/user/{id}</h3>

**RESPONSE**
```json
(empty)
```