package com.github.krishantx.RLaaS.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.github.krishantx.RLaaS.Model.AddEndpointDTO;
import com.github.krishantx.RLaaS.Model.CheckDTO;
import com.github.krishantx.RLaaS.Model.DatabaseModels.ClientEntity;
import com.github.krishantx.RLaaS.Service.CrudService;
import com.github.krishantx.RLaaS.Service.Service;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class Endpoints {
  @Autowired
  Service mainService;
  @Autowired
  CrudService crudService;

  @PostMapping("/check")
  public ResponseEntity<?> check(HttpServletRequest request, @RequestBody CheckDTO requestDTO) {
    boolean isExhausted = mainService.check(requestDTO, request.getHeader("x-api-key"));
    if (isExhausted)
      return ResponseEntity
          .status(429)
          .contentLength(0)
          .build();
    else
      return ResponseEntity
          .status(200)
          .contentLength(0)
          .build();
  }

  @PostMapping("/createClient")
  public ResponseEntity<?> createClient(HttpServletRequest request, @RequestBody ClientEntity clientEntity) {
    ClientEntity newClientEntity = crudService.createClient(clientEntity);
    if (newClientEntity == null)
      return ResponseEntity.status(429).build();
    return ResponseEntity.status(200).body(newClientEntity);
  }

  @PostMapping("/addEndpoint")
  public ResponseEntity<?> addEndpoint(HttpServletRequest request, @RequestBody AddEndpointDTO addEndpoint) {

    return crudService.addEndpoint(
        addEndpoint.getEndpoint(),
        addEndpoint.getRateLimit(),
        request.getHeader("x-api-key"));
  }
}
