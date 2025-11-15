package com.devbuild.evote.vote.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "votes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vote {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false)
    private String cin;

    @Column(nullable = false)
    private String candidate;

    @Column(nullable = false)
    private Instant timestamp;

    public Vote(java.util.UUID id, String cin, String candidate, Instant timestamp) {
        this.id = id;
        this.cin = cin;
        this.candidate = candidate;
        this.timestamp = timestamp;
    }
}
