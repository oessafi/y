package com.devbuild.evote.vote.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@FeignClient(name = "voter-service")
public interface VoterClient {

    @GetMapping("/api/voters/search/findByCin")
    Map<String, Object> findByCin(@RequestParam("cin") String cin);

    @PatchMapping("/api/voters/{id}")
    void patchVoter(@PathVariable("id") String id, @RequestBody Map<String, Object> patch);
}
