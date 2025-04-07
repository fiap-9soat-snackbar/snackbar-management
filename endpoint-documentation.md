# API Endpoints Documentation

## Authentication (`/api/user`)

### Sign Up
- **Method**: POST
- **Endpoint**: `/auth/signup`
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

### Log In
- **Method**: POST
- **Endpoint**: `/auth/signup`
- **Request Body**:
```json
{
    "cpf": "string",
    "password": "string"
}
```
- **Response Body**:
```json
{
    "jwtToken": "string",
    "expirationTime": "integer"
}
```


## Basket Service Endpoints (`/api/baskets`)

### Create Basket
- **Method**: POST
- **Endpoint**: `/api/baskets`
- **Request Body**:
```json
{
    "id": "string",
    "basketDateTime": "date",
    "cpf": "string",
    "name": "string",
    "items": [
        {
            "id": "string",
            "name": "string",
            "quantity":"integer",
            "price":"integer",
            "cookingTime":"integer",
            "customization": "string"
        }
    ],
    "totalPrice": "integer"
}
```

- **Response Body**:
```json
{
    "id": "string",
    "basketDateTime": "date",
    "cpf": "string",
    "name": "string",
    "items": [
        {
            "id": "string",
            "name": "string",
            "quantity":"integer",
            "price":"integer",
            "cookingTime":"integer",
            "customization": "string"
        }
    ],
    "totalPrice": "integer"
}
```


### Get Basket
- **Method**: GET
- **Endpoint**: `/api/baskets/{basketId}`
- **Response Body**:
```json
{
    "id": "string",
    "basketDateTime": "date",
    "cpf": "string",
    "name": "string",
    "items": [
        {
            "id": "string",
            "name": "string",
            "quantity":"integer",
            "price":"integer",
            "cookingTime":"integer",
            "customization": "string"
        }
    ],
    "totalPrice": "integer"
}
```

### Add Item to Basket
- **Method**: POST
- **Endpoint**: `/api/baskets/{basketId}/items`
- **Request Body**:
```json
{
    "productId": "string",
    "quantity": "integer"
}
```
- **Response Body**:
```json
{
    "id": "string",
    "basketDateTime": "date",
    "cpf": "string",
    "name": "string",
    "items": [
        {
            "id": "string",
            "name": "string",
            "quantity":"integer",
            "price":"integer",
            "cookingTime":"integer",
            "customization": "string"
        }
    ],
    "totalPrice": "integer"
}
```

### Delete Item from Basket
- **Method**: DELETE
- **Endpoint**: `/api/baskets/{basketId}/items/{itemId}`
- **Response Body**:
```json
{
    "id": "string",
    "basketDateTime": "date",
    "cpf": "string",
    "name": "string",
    "items": [
        {
            "id": "string",
            "name": "string",
            "quantity":"integer",
            "price":"integer",
            "cookingTime":"integer",
            "customization": "string"
        }
    ],
    "totalPrice": "integer"
}
```

## Cooking Service Endpoints (`/api/cooking`)

### Receive Order
- **Method**: POST
- **Endpoint**: `/api/cooking/receive-order/{id}`
- **Response Body**:
```json
{
    "id": "string",
    "orderId": "string",
    "status": "string"
}
```

### Start Preparation
- **Method**: POST
- **Endpoint**: `/api/cooking/start-preparation/{id}`
- **Response Body**:
```json
{
    "id": "string",
    "orderId": "string",
    "status": "string"
}
```

### Finish Preparation
- **Method**: POST
- **Endpoint**: `/api/cooking/finish-preparation/{id}`
- **Response Body**:
```json
{
    "id": "string",
    "orderId": "string",
    "status": "string"
}
```

### Get All Cookings
- **Method**: GET
- **Endpoint**: `/api/cooking`
- **Response Body**:
```json
{
    [
        {
            "id": "string",
            "orderId": "string",
            "status": "string"
        }
    ]
}
```

### Get Cooking by Order ID
- **Method**: GET
- **Endpoint**: `/api/cooking/{id}`
- **Response Body**:
```json
{
    "id": "string",
    "orderId": "string",
    "status": "string"
}
```

## Order Service Endpoints (`/api/orders`)

### Create Order
- **Method**: POST
- **Endpoint**: `/api/orders`
- **Request Body**:
```json
{
  "id": "string", 
  "orderNumber": "string",
  "orderDateTime": "ISODate", 
  "cpf": "string", 
  "name": "string",
  "items": [
    {
      "name": "string",
      "quantity": "int",
      "price": "decimal",
      "cookingTime": "int",
      "customization": "string"
    }
  ],
  "statusOrder": "string", 
  "paymentMethod": "string", 
  "totalPrice": "decimal", 
  "remainingTime": "long"
}
```
- **Response Body**:
```json
{
  "id": "string", 
  "orderNumber": "string",
  "orderDateTime": "ISODate", 
  "cpf": "string", 
  "name": "string",
  "items": [
    {
      "name": "string",
      "quantity": "int",
      "price": "decimal",
      "cookingTime": "int",
      "customization": "string"
    }
  ],
  "statusOrder": "string", 
  "paymentMethod": "string", 
  "totalPrice": "decimal", 
  "remainingTime": "long"
}
```


### Update Order
- **Method**: PUT
- **Endpoint**: `/api/orders`
- **Request Body**:
```json
{
  "id": "string", 
  "orderNumber": "string",
  "orderDateTime": "ISODate", 
  "cpf": "string", 
  "name": "string",
  "items": [
    {
      "name": "string",
      "quantity": "int",
      "price": "decimal",
      "cookingTime": "int",
      "customization": "string"
    }
  ],
  "statusOrder": "string", 
  "paymentMethod": "string", 
  "totalPrice": "decimal", 
  "remainingTime": "long"
}
```
- **Response Body**:
```json
{
  "id": "string", 
  "orderNumber": "string",
  "orderDateTime": "ISODate", 
  "cpf": "string", 
  "name": "string",
  "items": [
    {
      "name": "string",
      "quantity": "int",
      "price": "decimal",
      "cookingTime": "int",
      "customization": "string"
    }
  ],
  "statusOrder": "string", 
  "paymentMethod": "string", 
  "totalPrice": "decimal", 
  "remainingTime": "long"
}
```

### List Orders
- **Method**: GET
- **Endpoint**: `/api/orders`
- **Response Body**:
```json
[
    {
    "id": "string", 
    "orderNumber": "string",
    "orderDateTime": "ISODate", 
    "cpf": "string", 
    "name": "string",
    "items": [
        {
        "name": "string",
        "quantity": "int",
        "price": "decimal",
        "cookingTime": "int",
        "customization": "string"
        }
    ],
    "statusOrder": "string", 
    "paymentMethod": "string", 
    "totalPrice": "decimal", 
    "remainingTime": "long"
    }
]
```

### Get Order by ID
- **Method**: GET
- **Endpoint**: `/api/orders/{id}`
- **Response Body**:
```json
{
  "id": "string", 
  "orderNumber": "string",
  "orderDateTime": "ISODate", 
  "cpf": "string", 
  "name": "string",
  "items": [
    {
      "name": "string",
      "quantity": "int",
      "price": "decimal",
      "cookingTime": "int",
      "customization": "string"
    }
  ],
  "statusOrder": "string", 
  "paymentMethod": "string", 
  "totalPrice": "decimal", 
  "remainingTime": "long"
}
```

### Get Order by Number
- **Method**: GET
- **Endpoint**: `/api/orders/number/{orderNumber}`
- **Response Body**:
```json
{
  "id": "string", 
  "orderNumber": "string",
  "orderDateTime": "ISODate", 
  "cpf": "string", 
  "name": "string",
  "items": [
    {
      "name": "string",
      "quantity": "int",
      "price": "decimal",
      "cookingTime": "int",
      "customization": "string"
    }
  ],
  "statusOrder": "string", 
  "paymentMethod": "string", 
  "totalPrice": "decimal", 
  "remainingTime": "long"
}
```

## Payment Service Endpoints (`/api/payments`)

### Create Payment
- **Method**: POST
- **Endpoint**: `/api/payments`
- **Request Body**:
```json
{
    "orderId": "string",
    "paymentMethod": "string"
}
```
- **Response Body**:
```json
{
    "id": "string",
    "orderId": "string",
    "totalDue": "integer",
    "paymentStatus": "string",
    "externalPaymentId": "string"
}
```


### Create MercadoPago Payment
- **Method**: POST
- **Endpoint**: `/api/payments/mercadopago`
- **Request Body**:
```json
{
    "paymentId": "string",
    "totalDue": "integer",
    "cpf": "string",
    "callbackURL": "string"
}
```
- **Response Body**:
```json
{
    "id": "string",
    "paymentId": "string",
    "totalDue": "integer",
    "cpf": "string",
    "callbackURL": "string"
}
```

### List Payments
- **Method**: GET
- **Endpoint**: `/api/payments`
- **Response Body**:
```json
{
    [
        {
            "id": "string",
            "orderId": "string",
            "totalDue": "integer",
            "paymentStatus": "string",
            "paymentMethod": "string",
            "externalPaymentId": "string"
        }
    ]
}
```

### Get Payment by ID
- **Method**: GET
- **Endpoint**: `/api/payments/id/{id}`
- **Response Body**:
```json
{
    "id": "string",
    "orderId": "string",
    "totalDue": "integer",
    "paymentStatus": "string",
    "paymentMethod": "string",
    "externalPaymentId": "string"
}
```

### Get Payment by External ID
- **Method**: GET
- **Endpoint**: `/api/payments/externalId/{externalId}`
- **Response Body**:
```json
{
    "id": "string",
    "orderId": "string",
    "totalDue": "integer",
    "paymentStatus": "string",
    "paymentMethod": "string",
    "externalPaymentId": "string"
}
```

### Update Payment Status (Webhook)
- **Method**: PATCH
- **Endpoint**: `/api/payments/updateStatusWebhook`
- **Request Body**:
```json
{
    "externalId": "string",
    "paymentStatus": "string"
}
```
- **Response Body**:
```json
{
    "id": "string",
    "orderId": "string",
    "totalDue": "integer",
    "paymentStatus": "string",
    "paymentMethod": "string",
    "externalPaymentId": "string"
}
```

## Pickup Service Endpoints (`/api/pickup`)

### Notify Customer
- **Method**: POST
- **Endpoint**: `/api/pickup/notify/{orderId}`
- **Response Body**:
```json
"Cliente notificado - Pedido: #{orderId}"
```

### Delivery Order
- **Method**: POST
- **Endpoint**: `/api/pickup/delivery/{orderId}`
- **Response Body**:
```json
"Pedido #{orderId} está Finalizado!"
```

## Product Service Endpoints (`/api/productsv2`)

### Create Product
- **Method**: POST
- **Endpoint**: `/api/products`
- **Request Body**:
```json
{
    "name": "string",
    "category": "string",
    "description": "string",
    "price": "number",
    "cookingTime": "integer"
}
```
- **Response Body**:
```json
{
    "id": "string",
    "name": "string",
    "category": "string",
    "description": "string",
    "price": "number",
    "cookingTime": "integer"
}
```

### Get Product
- **Method**: GET
- **Endpoint**: `/api/productsv2/{id}`
- **Response Body**:
```json
{
    "id": "string",
    "name": "string",
    "category": "string",
    "description": "string",
    "price": "number",
    "cookingTime": "integer"
}
```

### Get All Products
- **Method**: GET
- **Endpoint**: `/api/productsv2`
- **Response Body**:
```json
{
    [
        {
            "id": "string",
            "name": "string",
            "category": "string",
            "description": "string",
            "price": "number",
            "cookingTime": "integer"
        }
    ]
}
```

### Get Products by Category
- **Method**: GET
- **Endpoint**: `/api/productsv2/category/{category}`
- **Response Body**:
```json
{
    [
        {
            "id": "string",
            "name": "string",
            "category": "string",
            "description": "string",
            "price": "number",
            "cookingTime": "integer"
        }
    ]
}
```

### Update Product
- **Method**: PUT
- **Endpoint**: `/api/productsv2/{id}`
- **Request Body**:
```json
{
    "name": "string",
    "category": "string",
    "description": "string",
    "price": "integer",
    "cookingTime": "integer"
}
```
- **Response Body**:
```json
{
    "id": "string",
    "name": "string",
    "category": "string",
    "description": "string",
    "price": "number",
    "cookingTime": "integer"
}
```

### Delete Product
- **Method**: DELETE
- **Endpoint**: `/api/products/{id}`
- **Response**: OK
```