package com.devbuild.evote.vote.controller;

import com.devbuild.evote.vote.feign.VoterClient;
import com.devbuild.evote.vote.model.Vote;
import com.devbuild.evote.vote.repository.VoteRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Collections; // Import ajouté
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/votes")
public class VoteController {

    private final VoteRepository voteRepository;
    private final VoterClient voterClient;

    public VoteController(VoteRepository voteRepository, VoterClient voterClient) {
        this.voteRepository = voteRepository;
        this.voterClient = voterClient;
    }

    @PostMapping
    public ResponseEntity<?> submitVote(@RequestBody Map<String, String> body) {
        String cin = body.get("cin");
        String candidate = body.get("candidate");
        if (cin == null || candidate == null) {
            // Remplacement de Map.of par Collections.singletonMap
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", "cin and candidate required"));
        }

        Map<String, Object> found = voterClient.findByCin(cin);
        if (found == null || found.isEmpty()) {
            return ResponseEntity.status(404).body(Collections.singletonMap("error", "voter not found"));
        }

        // Spring Data REST search returns either a single resource or embedded; try to extract id and hasVoted
        Object idObj = found.get("id");
        Object hasVotedObj = found.get("hasVoted");

        String voterId = null;
        boolean hasVoted = false;

        if (idObj != null) {
            voterId = idObj.toString();
        } else if (found.get("_embedded") != null) {
            // handle embedded results
            Map embedded = (Map) found.get("_embedded");
            Object voters = embedded.get("voters");
            if (voters instanceof java.util.List && !((List) voters).isEmpty()) {
                Map first = (Map) ((List) voters).get(0);
                voterId = first.get("id").toString();
                hasVoted = Boolean.parseBoolean(first.getOrDefault("hasVoted", "false").toString());
            }
        } else {
            hasVoted = Boolean.parseBoolean(String.valueOf(hasVotedObj));
        }

        if (voterId == null) {
            return ResponseEntity.status(500).body(Collections.singletonMap("error", "couldn't resolve voter id"));
        }

        if (Boolean.TRUE.equals(hasVoted)) {
            return ResponseEntity.status(409).body(Collections.singletonMap("error", "voter already voted"));
        }

        Vote vote = new Vote(null, cin, candidate, Instant.now());

        voteRepository.save(vote);

        // Patch voter hasVoted = true
        Map<String, Object> patch = new HashMap<>();
        patch.put("hasVoted", true);
        voterClient.patchVoter(voterId, patch);

        return ResponseEntity.ok(Collections.singletonMap("status", "vote recorded"));
    }

    @GetMapping
    public List<Vote> listVotes() {
        return voteRepository.findAll();
    }
}