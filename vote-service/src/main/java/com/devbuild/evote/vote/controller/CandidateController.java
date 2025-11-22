
package com.devbuild.evote.vote.controller;

import com.devbuild.evote.vote.model.Candidate;
import com.devbuild.evote.vote.repository.CandidateRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/candidates")
public class CandidateController {

	private final CandidateRepository candidateRepository;

	public CandidateController(CandidateRepository candidateRepository) {
		this.candidateRepository = candidateRepository;
	}

	@GetMapping
	public List<Candidate> getAllCandidates() {
		return candidateRepository.findAll();
	}

	@PostMapping
	public ResponseEntity<?> createCandidate(@RequestBody Candidate candidate) {
		if (candidateRepository.existsByName(candidate.getName())) {
			return ResponseEntity.badRequest().body(Collections.singletonMap("error", "Candidate already exists"));
		}
		return ResponseEntity.ok(candidateRepository.save(candidate));
	}
}
