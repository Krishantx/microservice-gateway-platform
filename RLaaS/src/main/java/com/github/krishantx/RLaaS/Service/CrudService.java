package com.github.krishantx.RLaaS.Service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.github.krishantx.RLaaS.Model.DatabaseModels.ClientEntity;
import com.github.krishantx.RLaaS.Model.DatabaseModels.EndpointEntity;
import com.github.krishantx.RLaaS.Repo.ClientRepo;
import com.github.krishantx.RLaaS.Repo.PostgresRepo;

@Service
public class CrudService {
    @Autowired ClientRepo clientRepo;
    @Autowired PostgresRepo postgresRepo;
    @Autowired ApiKeyService apiKeyService;

    public ResponseEntity<?> addEndpoint(String endpoint, int rateLimit, String apiKey) {
        EndpointEntity endpointEntity = new EndpointEntity(endpoint, rateLimit);
        Optional<ClientEntity> client = clientRepo.findByApiKey(apiKey);

        if (client.isEmpty()) 
            return ResponseEntity.status(404).body("Incorrect API Key");

        Optional<EndpointEntity> fetchFromDB = postgresRepo.findByEndpointAndClient(endpoint, client.get());
        
        if (fetchFromDB.isEmpty()) {
            endpointEntity.setClient(client.get());
            
            postgresRepo.save(endpointEntity);
            return ResponseEntity.status(200).build();
        }
        else
            return ResponseEntity.status(409).build();
    }
    public ClientEntity createClient(ClientEntity clientEntity) {
        if (clientRepo.findByClientName(clientEntity.getClientName()).isEmpty()) {
            String apiKey = apiKeyService.generateApiKey();
            clientEntity.setApiKey(apiKey);
            ClientEntity newClientEntity = clientRepo.save(clientEntity);
            return newClientEntity;
        }
        else {
            return null;
        }
    }
    
}
