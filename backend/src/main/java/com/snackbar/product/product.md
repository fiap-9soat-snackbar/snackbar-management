## 📍Products Endpoints

✅ All endpoints below have been implemented with standardized responses in `/api/product`:

| route               | description                                          | status
|----------------------|-----------------------------------------------------|--------
| <kbd>GET /api/product</kbd>     | See [request details](#get-products) | ✅ Done
| <kbd>GET /api/product/id/{id}</kbd>     |  See [request details](#get-products-id) | ✅ Done
| <kbd>GET /api/product/category/{category}</kbd>     |See [request details](#get-products-category) | ✅ Done
| <kbd>POST /api/product</kbd>     | See [request details](#post-products) | ✅ Done
| <kbd>PUT /api/product/id/{id}</kbd>     | See [request details](#put-products) | ✅ Done
| <kbd>DELETE /api/product/id/{id}</kbd>     | See [request details](#delete-products) | ✅ Done


<h3 id="get-products">GET /api/product ✅</h3>

**RESPONSE**  
```json
{
    "success": true,
    "message": "Products retrieved successfully",
    "data": [
        {
            "id": "671bb29c52801c1c1efe6911",
            "category": "Lanche",
            "description": "Hambúrguer artesanal 160g, servido com pão de brioche, alface e tomate.",
            "name": "Hambúrguer",
            "price": 22,
            "cookingTime": 10
        }
        /* All other products */
    ]
}
```

<h3 id="get-products-id">GET /api/product/id/{id} ✅</h3>

**RESPONSE**
```json
{
    "success": true,
    "message": "Product retrieved successfully",
    "data": {
        "id": "671d1ab834d76230acfe6911",
        "category": "Lanche",
        "description": "Hambúrguer artesanal 160g, servido com pão de brioche, alface e tomate.",
        "name": "Hambúrguer",
        "price": 22,
        "cookingTime": 10
    }
}
```

<h3 id="get-products-category">GET /api/product/category/{category} ✅</h3>

**RESPONSE**
```json
{
    "success": true,
    "message": "Products retrieved successfully",
    "data": [
        {
            "id": "671d1ab834d76230acfe6911",
            "category": "Lanche",
            "description": "Hambúrguer artesanal 160g, servido com pão de brioche, alface e tomate.",
            "name": "Hambúrguer",
            "price": 22,
            "cookingTime": 10
        },
        {
            "id": "67266201b5ad4f0589fe6912",
            "category": "Lanche",
            "description": "Hambúrguer artesanal 160g, servido com pão de brioche e queijo prato.",
            "name": "Cheesebúrguer",
            "price": 25,
            "cookingTime": 10
        }
        /* All other products in the same category */
    ]
}
```
<h3 id="post-products">POST /api/product ✅</h3>

**REQUEST**  
```json
{
    "name": "Hambúrguer",
    "category": "Lanche",
    "description": "Hambúrguer artesanal 160g, servido com pão de brioche, alface e tomate.",
    "price": 22,
    "cookingTime": 10
}
```
**RESPONSE**
```json
{
    "success": true,
    "message": "Product created successfully",
    "data": {
        "id": "671d1c91f7689b2849534586",
        "category": "Lanche",
        "description": "Hambúrguer artesanal 160g, servido com pão de brioche, alface e tomate.",
        "name": "Hambúrguer",
        "price": 22,
        "cookingTime": 10
    }
}
```

<h3 id="put-products">PUT /api/product/id/{id} ✅</h3>

**REQUEST**  
```json
{
    "id": "67266201b5ad4f0589fe6917",
    "category": "Acompanhamento",
    "description": "Porção grande de batatas fritas crocantes.",
    "name": "Batata frita Grande",
    "price": 15,
    "cookingTime": 12
}
```

**RESPONSE**  
```json
{
    "success": true,
    "message": "Product updated successfully",
    "data": {
        "id": "67266201b5ad4f0589fe6917",
        "category": "Acompanhamento",
        "description": "Porção grande de batatas fritas crocantes.",
        "name": "Batata frita Grande",
        "price": 15,
        "cookingTime": 12
    }
}
```
<h3 id="delete-products">DELETE /api/product/id/{id} ✅</h3>

**RESPONSE**  
```json
{
    "success": true,
    "message": "Product deleted successfully",
    "data": null
}
```

## 🔄 Next Steps

4. Implement validation for product fields:
   - name: required, min length 3
   - category: required, must be one of: ["Lanche", "Acompanhamento", "Bebida", "Sobremesa"]
   - description: required, min length 10
   - price: required, must be greater than 0
   - cookingTime: required, must be greater than or equal to 0
