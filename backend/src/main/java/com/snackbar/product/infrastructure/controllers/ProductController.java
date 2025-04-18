package com.snackbar.product.infrastructure.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.snackbar.product.application.usecases.*;
import com.snackbar.product.domain.entity.Product;
import com.snackbar.product.infrastructure.controllers.dto.ResponseDTO;

import java.util.List;

@RestController
@RequestMapping("/api/product")
public class ProductController {
  
    private final CreateProductUseCase createProductUseCase;
    private final GetProductByIdUseCase getProductByIdUseCase;
    private final ListProductUseCase listProductUseCase;
    private final GetProductByCategoryUseCase getProductByCategoryUseCase;
    private final GetProductByNameUseCase getProductByNameUseCase;
    private final UpdateProductByIdUseCase updateProductByIdUseCase;
    private final DeleteProductByIdUseCase deleteProductByIdUseCase;
    private final ProductDTOMapper productDTOMapper;

    @Autowired
    public ProductController(
            CreateProductUseCase createProductUseCase,
            GetProductByIdUseCase getProductByIdUseCase,
            ListProductUseCase listProductUseCase,
            GetProductByCategoryUseCase getProductByCategoryUseCase,
            GetProductByNameUseCase getProductByNameUseCase,
            UpdateProductByIdUseCase updateProductByIdUseCase,
            DeleteProductByIdUseCase deleteProductByIdUseCase,
            ProductDTOMapper productDTOMapper) {
        this.createProductUseCase = createProductUseCase;
        this.getProductByIdUseCase = getProductByIdUseCase;
        this.listProductUseCase = listProductUseCase;
        this.getProductByCategoryUseCase = getProductByCategoryUseCase;
        this.getProductByNameUseCase = getProductByNameUseCase;
        this.updateProductByIdUseCase = updateProductByIdUseCase;
        this.deleteProductByIdUseCase = deleteProductByIdUseCase;
        this.productDTOMapper = productDTOMapper;
    }

    @PostMapping
    public ResponseEntity<ResponseDTO> createProduct(@RequestBody CreateProductRequest request) {
        Product product = productDTOMapper.createRequestToDomain(request);
        Product createdProduct = createProductUseCase.createProduct(product);
        CreateProductResponse response = productDTOMapper.createToResponse(createdProduct);
        return ResponseEntity.ok(new ResponseDTO(true, "Product created successfully", response));
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<ResponseDTO> getProductById(@PathVariable("id") String id) {
        Product retrievedProduct = getProductByIdUseCase.getProductById(id);
        GetProductResponse response = productDTOMapper.getToResponse(retrievedProduct);
        return ResponseEntity.ok(new ResponseDTO(true, "Product retrieved successfully", response));
    }

    @GetMapping
    public ResponseEntity<ResponseDTO> listProduct() {
        List<Product> retrievedProductList = listProductUseCase.listProduct();
        List<GetProductResponse> response = productDTOMapper.listToResponse(retrievedProductList);
        return ResponseEntity.ok(new ResponseDTO(true, "Products retrieved successfully", response));
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<ResponseDTO> getProductByCategory(@PathVariable("category") String category) {
        List<Product> retrievedProductList = getProductByCategoryUseCase.getProductByCategory(category);
        List<GetProductResponse> response = productDTOMapper.listToResponse(retrievedProductList);
        return ResponseEntity.ok(new ResponseDTO(true, "Products retrieved successfully", response));
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<ResponseDTO> getProductByName(@PathVariable("name") String name) {
        Product retrievedProduct = getProductByNameUseCase.getProductByName(name);
        GetProductResponse response = productDTOMapper.getToResponse(retrievedProduct);
        return ResponseEntity.ok(new ResponseDTO(true, "Product retrieved successfully", response));
    }

    @PutMapping("/id/{id}")
    public ResponseEntity<ResponseDTO> updateProductById(@PathVariable("id") String id, @RequestBody CreateProductRequest request) {
        Product product = productDTOMapper.createRequestToDomain(request);
        Product updatedProduct = updateProductByIdUseCase.updateProductById(id, product);
        CreateProductResponse response = productDTOMapper.createToResponse(updatedProduct);
        return ResponseEntity.ok(new ResponseDTO(true, "Product updated successfully", response));
    }

    @DeleteMapping("/id/{id}")
    public ResponseEntity<ResponseDTO> deleteProduct(@PathVariable String id) {
        deleteProductByIdUseCase.deleteProductById(id);
        return ResponseEntity.ok(new ResponseDTO(true, "Product deleted successfully", null));
    }
}
