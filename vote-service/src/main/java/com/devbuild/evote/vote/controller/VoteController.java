package com.devbuild.evote.vote.controller;

import com.devbuild.evote.vote.feign.VoterClient;
import com.devbuild.evote.vote.model.Vote;
import com.devbuild.evote.vote.repository.CandidateRepository;
import com.devbuild.evote.vote.repository.VoteRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.*;

@RestController
@RequestMapping("/votes")
public class VoteController {

    private final VoteRepository voteRepository;
    private final VoterClient voterClient;
    private final CandidateRepository candidateRepository;

    public VoteController(VoteRepository voteRepository, VoterClient voterClient, CandidateRepository candidateRepository) {
        this.voteRepository = voteRepository;
        this.voterClient = voterClient;
        this.candidateRepository = candidateRepository;
    }

    @GetMapping("/counts")
    public Map<String, Long> getVoteCounts() {
        List<Object[]> results = voteRepository.countVotesByCandidate();
        Map<String, Long> map = new HashMap<>();
        for (Object[] row : results) {
            map.put((String) row[0], (Long) row[1]);
        }
        return map;
    }

    @PostMapping
    public ResponseEntity<?> submitVote(@RequestBody Map<String, String> body) {
        String cin = body.get("cin");
        String candidate = body.get("candidate");

        if (cin == null || candidate == null) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", "cin and candidate required"));
        }

        if (!candidateRepository.existsByName(candidate)) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", "Invalid candidate. Must be one of the official candidates."));
        }

        Map<String, Object> found = voterClient.findByCin(cin);
        if (found == null || found.isEmpty()) {
            return ResponseEntity.status(404).body(Collections.singletonMap("error", "voter not found"));
        }

        String voterId = extractId(found);
        boolean hasVoted = extractHasVoted(found);

        if (voterId == null) {
            return ResponseEntity.status(500).body(Collections.singletonMap("error", "couldn't resolve voter id"));
        }

        if (hasVoted) {
            return ResponseEntity.status(409).body(Collections.singletonMap("error", "voter already voted"));
        }

        Vote vote = new Vote(null, cin, candidate, Instant.now());
        voteRepository.save(vote);

        try {
            Map<String, Object> patch = new HashMap<>();
            patch.put("hasVoted", true);
            voterClient.patchVoter(voterId, patch);
        } catch (Exception e) {
            e.printStackTrace();
            voteRepository.delete(vote);
            return ResponseEntity.status(500).body(Collections.singletonMap("error", "Echec PATCH: " + e.getMessage()));
        }

        return ResponseEntity.ok(Collections.singletonMap("status", "vote recorded"));
    }

    @GetMapping
    public List<Vote> listVotes() {
        return voteRepository.findAll();
    }

    private String extractId(Map<String, Object> found) {
        if (found.get("id") != null) return found.get("id").toString();

        if (found.get("_embedded") != null) {
            Map embedded = (Map) found.get("_embedded");
            Object voters = embedded.get("voters");
            if (voters instanceof List && !((List) voters).isEmpty()) {
                Map first = (Map) ((List) voters).get(0);
                return first.get("id").toString();
            }
        }
        return null;
    }

    private boolean extractHasVoted(Map<String, Object> found) {
        Object obj = found.get("hasVoted");
        if (obj != null) return Boolean.parseBoolean(obj.toString());

        if (found.get("_embedded") != null) {
            Map embedded = (Map) found.get("_embedded");
            Object voters = embedded.get("voters");
            if (voters instanceof List && !((List) voters).isEmpty()) {
                Map first = (Map) ((List) voters).get(0);
                return Boolean.parseBoolean(first.getOrDefault("hasVoted", "false").toString());
            }
        }
        return false;
    }
}