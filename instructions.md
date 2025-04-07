# API Instructions

## Client Side:

### 1. Sign-up
**POST** `/api/user/auth/signup`

Request body:
```json
{
    "email": "string",
    "password": "string",
    "cpf": "string",
    "role": "string",
    "name": "string",
}
```

Response body:
```json
{
    "name": "string",
    "email": "string",
    "cpf": "string",
    "role": "string",
    "password": "string"
}
```


### 2. Log-in
**POST** `/api/user/auth/login`

Request body:
```json
{
    "email": "string",
    "password": "string"
}
```

Response body:
```json
{
    "token": "string",
    "expiresIn": "integer"
}
```

### 3. Get data from all Products
**GET** `/api/productsv2`

Response body:
```json
[
    {
        "id": "string",
        "name": "string",
        "category": "string",
        "description": "string",
        "price": "integer",
        "cookingTime": "integer"
    }
]
```


### 4. Get data from a specific Product
**GET** `/api/productsv2/id/{{product_id}}`

Response body:
```json
{
    "id": "string",
    "name": "string",
    "category": "string",
    "description": "string",
    "price": "integer",
    "cookingTime": "integer"
}
```


### 5. Get data from Products of specific category
**GET** `/api/productsv2/category/{{category}}`

Response body:
```json
[
    {
        "id": "string",
        "name": "string",
        "category": "string",
        "description": "string",
        "price": "integer",
        "cookingTime": "integer"
    }
]
```

### 6. Check the order out
**POST** `/api/checkout/{basket_id}`

Response body:
```json
"Pagamento do pedido #{baskted_id} pago via Mercado Pago (QR Code) com sucesso!"
```

## Kitchen Side:

### 1. Receive an Order
**POST** `/api/cooking/receive-order/{{order_id}}`

Response body:
```json
{
    "id": "string",
    "orderId": "string",
    "status": "RECEBIDO"
}
```


### 2. Start Preparation
**POST** `/api/cooking/start-preparation/{{order_id}}`

Response body:
```json
{
    "id": "string",
    "orderId": "string",
    "status": "PREPARACAO"
}
```

### 3. Finish Preparation
**POST** `/api/cooking/finish-preparation/{{order_id}}`

Response body:
```json
{
    "id": "string",
    "orderId": "string",
    "status": "PRONTO"
}
```

### 4. Check the preparation status of a specific order which was already received
**GET** `/api/cooking/{{order_id}}`

Response body:
```json
{
    "id": "string",
    "orderId": "string",
    "status": "string"
}
```

### 5. Check the preparation status of all orders which were already received
**GET** `/api/cooking`

Response body:
```json
[
    {
        "id": "string",
        "orderId": "string",
        "status": "string"
    }
]
```

### 6. Notify customer about order readiness
**POST** `/api/pickup/notify/{{order_id}}`

Response body:
```json
"Cliente notificado - Pedido: #{order_id}"
```

### 7. Deliver order
**POST** `/api/pickup/delivery/{{order_id}}`

Response body:
```json
"Pedido #{order_id} está Finalizado!"
```