package com.devbuild.evote.vote.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

// Explicit URL to avoid local DNS issues on Windows
@FeignClient(name = "voter-service", url = "http://localhost:9101")
public interface VoterClient {

    @GetMapping("/api/voters/search/findByCin")
    Map<String, Object> findByCin(@RequestParam("cin") String cin);

    @PatchMapping("/api/voters/{id}")
    void patchVoter(@PathVariable("id") String id, @RequestBody Map<String, Object> patch);
}