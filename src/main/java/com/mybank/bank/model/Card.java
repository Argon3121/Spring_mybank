package com.mybank.bank.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "cards")
@NoArgsConstructor
@Getter
@Setter
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 16)
    private String number;

    @Column(nullable = false, length = 3)
    private String cvv;

    @Column(nullable = false)
    private BigDecimal balance;

    @Column(nullable = false)
    private String status;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private User owner;

    public Card(String number, User owner, String cvv) {
        this.number = number;
        this.cvv = cvv;
        this.owner = owner;
    }

    @PrePersist
    void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.balance = BigDecimal.ZERO;
        this.status = "ACTIVE";
    }
}