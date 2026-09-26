package com.github.krishantx.RLaaS.Repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.github.krishantx.RLaaS.Model.DatabaseModels.ClientEntity;

@Repository
public interface ClientRepo extends JpaRepository<ClientEntity, Integer> {
    
    public Optional<ClientEntity> findByClientName(String clientName);
    public Optional<ClientEntity> findByApiKey(String apiKey);
}
