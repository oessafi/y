package com.devbuild.evote.voter.controller;

import com.devbuild.evote.voter.model.Voter;
import com.devbuild.evote.voter.repository.VoterRepository;
import org.springframework.dao.DataIntegrityViolationException; // Import nécessaire
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/voters")
public class VoterController {

    private final VoterRepository voterRepository;

    public VoterController(VoterRepository voterRepository) {
        this.voterRepository = voterRepository;
    }

    // 1. Lister tous les électeurs
    @GetMapping
    public List<Voter> getAllVoters() {
        return voterRepository.findAll();
    }

    // 2. Chercher par CIN (Utilisé par vote-service)
    @GetMapping("/search/findByCin")
    public ResponseEntity<Voter> findByCin(@RequestParam("cin") String cin) {
        return voterRepository.findByCin(cin)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 3. Créer un électeur (Avec gestion d'erreur CIN dupliqué)
    @PostMapping
    public ResponseEntity<?> createVoter(@RequestBody Voter voter) {
        try {
            Voter saved = voterRepository.save(voter);
            return ResponseEntity.ok(saved);
        } catch (DataIntegrityViolationException ex) {
            // Retourne un conflit (409) si le CIN existe déjà
            return ResponseEntity.status(409)
                    .body(Collections.singletonMap("error", "CIN already exists"));
        }
    }

    // 4. Mettre à jour (PATCH) - CORRIGÉ
    // IMPORTANT : On garde ("id") ici pour éviter le bug IllegalArgumentException
    @PatchMapping("/{id}")
    public ResponseEntity<?> updateVoter(@PathVariable("id") UUID id, @RequestBody Map<String, Object> updates) {
        try {
            return voterRepository.findById(id).map(voter -> {
                if (updates.containsKey("hasVoted")) {
                    Object val = updates.get("hasVoted");
                    if (val instanceof Boolean) {
                        voter.setHasVoted((Boolean) val);
                    } else if (val instanceof String) {
                        voter.setHasVoted(Boolean.parseBoolean((String) val));
                    }
                }
                voterRepository.save(voter);
                return ResponseEntity.ok().build();
            }).orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Collections.singletonMap("error", e.getMessage()));
        }
    }
}