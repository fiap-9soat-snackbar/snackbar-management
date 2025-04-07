# Snackbar API Documentation

This document provides a comprehensive list of all available endpoints in the Snackbar application.

## Products API (v2)
Base path: `/api/productsv2`

- POST `/` - Create a new product
- GET `/` - List all products
- GET `/id/{id}` - Get a product by ID
- GET `/category/{category}` - Get products by category
- GET `/name/{name}` - Get a product by name
- PUT `/id/{id}` - Update a product by ID
- DELETE `/id/{id}` - Delete a product

## Basket API
Base path: `/api/baskets`

- POST `/` - Create a new basket
- GET `/{basketId}` - Find a basket by ID
- POST `/{basketId}/items` - Add item to basket
- DELETE `/{basketId}/items/{itemId}` - Delete item from basket

## Cooking API
Base path: `/api/cooking`

- POST `/receive-order/{id}` - Receive an order for cooking
- POST `/start-preparation/{id}` - Start order preparation
- POST `/finish-preparation/{id}` - Finish order preparation
- GET `/` - Get all cookings
- GET `/{id}` - Get cooking by order ID

## Orders API
Base path: `/api/ordersrefactored`

- POST `/` - Create a new order
- PUT `/` - Update an order
- GET `/` - List all orders
- GET `/{id}` - Search order by ID
- GET `/number/{orderNumber}` - Get order by order number
- GET `/sorted` - Get sorted orders
- PUT `/status` - Update order status

## Payment API
Base path: `/api/payments`

- POST `/` - Create a payment
- GET `/` - List all payments
- GET `/id/{id}` - Get payment by ID
- POST `/mercadopago` - Create MercadoPago payment
- GET `/externalId/{externalId}` - Get payment by external ID
- PATCH `/updateStatusWebhook` - Update payment status by external ID

## Pickup API
Base path: `/api/pickup`

- POST `/notify/{orderId}` - Notify customer about order
- POST `/delivery/{orderId}` - Mark order as delivered

Note: All endpoints return appropriate HTTP status codes and response bodies. For detailed request/response schemas, please refer to the respective controller classes.