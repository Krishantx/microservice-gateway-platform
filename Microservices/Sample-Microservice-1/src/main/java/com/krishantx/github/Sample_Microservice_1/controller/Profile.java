package com.krishantx.github.Sample_Microservice_1.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.krishantx.github.Sample_Microservice_1.entity.AuthenticationDTO;
import com.krishantx.github.Sample_Microservice_1.entity.DummyResponseBody;
import com.krishantx.github.Sample_Microservice_1.entity.UserEntity;
import com.krishantx.github.Sample_Microservice_1.utils.JwtUtils;

@RestController
public class Profile {
  @GetMapping("/profile")
  public ResponseEntity<?> getHome() {
    DummyResponseBody responseBody = new DummyResponseBody();
    responseBody.setUserId(1);
    responseBody.setUsername("kristovic");
    responseBody.setEmail("kb@gmail.com");
    responseBody.setStatus("ONLINE");

    return ResponseEntity.status(200).body(responseBody);
  }

  @PostMapping("/authenticate")
  public ResponseEntity<?> getJwtToken(@RequestBody UserEntity user) {
    System.out.println("Username : " + user.getUsername());
    if (user.getUsername().equals("krishantx") && user.getPassword().equals("password")) {
      String token = JwtUtils.generateToken(user.getUsername());
      AuthenticationDTO response = new AuthenticationDTO();
      response.setToken(token);
      return ResponseEntity.status(200).contentType(MediaType.APPLICATION_JSON)
          .body(response);
    } else {
      return ResponseEntity.status(400).build();
    }

  }
}
