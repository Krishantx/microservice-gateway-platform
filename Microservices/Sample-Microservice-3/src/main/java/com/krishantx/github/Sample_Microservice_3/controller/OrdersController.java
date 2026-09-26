package com.krishantx.github.Sample_Microservice_3.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.krishantx.github.Sample_Microservice_3.entity.OrderEntity;

@RestController
public class OrdersController {
  @GetMapping("/order")
  public ResponseEntity<?> getOrder() {
    OrderEntity order = new OrderEntity();
    order.setOrderId(1);
    order.setOrderStatus("Delivered");
    order.setProductId(1);
    order.setUserId(1);
    order.setTotalAmount(200);
    order.setQuantity(1);

    return ResponseEntity.status(200).body(order);
  }
}
