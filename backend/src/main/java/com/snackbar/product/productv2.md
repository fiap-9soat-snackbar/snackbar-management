 
## 📍Products Endpoints

| route               | description                                          
|----------------------|-----------------------------------------------------
| <kbd>GET /api/productsv2</kbd>     | See [request details](#get-products)
| <kbd>GET /api/productsv2/id/{id}</kbd>     |  See [request details](#get-products-id)
| <kbd>GET /api/productsv2/category/{category}</kbd>     |See [request details](#get-products-category)
| <kbd>POST /api/productsv2</kbd>     | See [request details](#post-products)
| <kbd>PUT /api/productsv2/id/{id}</kbd>     | See [request details](#put-products)
| <kbd>DELETE /api/productsv2/id/{id}</kbd>     | See [request details](#delete-products) 


<h3 id="get-products">GET /api/productsv2</h3>

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

<h3 id="get-products-id">GET /api/productsv2/id/{id}</h3>

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

<h3 id="get-products-category">GET /api/productsv2/category/{category}</h3>

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
<h3 id="post-products">POST /api/productsv2</h3>

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

<h3 id="put-products">PUT /api/productsv2/id/{id}</h3>

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
<h3 id="delete-products">DELETE /api/productsv2/id/{id}</h3>

**RESPONSE**  
```json
{
    "success": true,
    "message": "Product deleted successfully",
    "data": null
}

