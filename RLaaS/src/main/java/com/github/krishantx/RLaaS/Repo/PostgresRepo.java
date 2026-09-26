package com.github.krishantx.RLaaS.Repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.github.krishantx.RLaaS.Model.DatabaseModels.ClientEntity;
import com.github.krishantx.RLaaS.Model.DatabaseModels.EndpointEntity;

@Repository
public interface PostgresRepo extends JpaRepository<EndpointEntity, Integer>{
    public Optional<EndpointEntity> findByEndpoint(String endpoint);

    public Optional<EndpointEntity> findByEndpointAndClient(String endpoint, ClientEntity client);
    
}
