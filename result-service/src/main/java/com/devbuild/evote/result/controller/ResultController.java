package com.devbuild.evote.result.controller;

import com.devbuild.evote.result.feign.VoteClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

@RestController
public class ResultController {

    private final VoteClient voteClient;

    public ResultController(VoteClient voteClient) {
        this.voteClient = voteClient;
    }

    @GetMapping("/results")
    public List<Map<String, Object>> results() {
        List<Map<String, Object>> votes = voteClient.getAllVotes();
        if (votes == null) votes = Collections.emptyList();

        Map<String, Long> counts = new HashMap<>();
        for (Map<String, Object> v : votes) {
            String candidate = String.valueOf(v.get("candidate"));
            counts.put(candidate, counts.getOrDefault(candidate, 0L) + 1L);
        }

        List<Map<String, Object>> result = new ArrayList<>();
        for (Map.Entry<String, Long> e : counts.entrySet()) {
            result.add(Map.of("candidate", e.getKey(), "totalVotes", e.getValue()));
        }

        return result;
    }
}
