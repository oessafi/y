package com.devbuild.evote.voter.repository;

import com.devbuild.evote.voter.model.Voter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import java.util.Optional;
import java.util.UUID;

@RepositoryRestResource(collectionResourceRel = "voters", path = "voters")
public interface VoterRepository extends JpaRepository<Voter, UUID> {
    Optional<Voter> findByCin(String cin);
}
