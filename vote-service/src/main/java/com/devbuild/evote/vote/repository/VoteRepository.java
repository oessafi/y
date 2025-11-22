package com.devbuild.evote.vote.repository;

import com.devbuild.evote.vote.model.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface VoteRepository extends JpaRepository<Vote, UUID> {

    List<Vote> findByCandidate(String candidate);
    List<Vote> findByCin(String cin);

    @Query("SELECT v.candidate, COUNT(v) FROM Vote v GROUP BY v.candidate")
    List<Object[]> countVotesByCandidate();
}