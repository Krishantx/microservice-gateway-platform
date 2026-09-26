package com.krishantx.github.Sample_Microservice_2.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.krishantx.github.Sample_Microservice_2.entity.ProductEntity;

@RestController
public class Product {
  @GetMapping("/product")
  public ResponseEntity<?> getProfile() {
    ProductEntity product = new ProductEntity();
    product.setCategory("Clothing");
    product.setPrice((float) 200.00);
    product.setProductId(1);
    product.setStock(200);
    product.setProductName("Black Tshirt");
    return ResponseEntity.status(200).body(product);
  }
}
