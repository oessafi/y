package com.devbuild.evote.result.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.List;
import java.util.Map;

// AJOUT DE L'URL ICI :
@FeignClient(name = "vote-service", url = "http://localhost:9102")
public interface VoteClient {
    @GetMapping("/votes")
    List<Map<String, Object>> getAllVotes();

    @GetMapping("/votes/counts")
    Map<String, Long> getVoteCounts();
}