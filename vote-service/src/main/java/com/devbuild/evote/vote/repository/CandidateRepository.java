package com.devbuild.evote.vote.repository;

import com.devbuild.evote.vote.model.Candidate;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface CandidateRepository extends JpaRepository<Candidate, UUID> {
    boolean existsByName(String name);
}