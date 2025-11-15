package com.devbuild.evote.result.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Map;

@FeignClient(name = "vote-service")
public interface VoteClient {
    @GetMapping("/votes")
    List<Map<String, Object>> getAllVotes();
}
