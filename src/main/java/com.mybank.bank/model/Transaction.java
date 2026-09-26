package com.mybank.bank.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private BigDecimal amount;

    private String type;

    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "card_id")
    private Card card;

    @ManyToOne
    @JoinColumn(name = "from_card_id")
    private Card fromCard;

    @ManyToOne
    @JoinColumn(name = "to_card_id")
    private Card toCard;

    private BigDecimal fromBalance;

    private BigDecimal toBalance;

    public Transaction(
            BigDecimal amount,
            String type,
            Card card) {

        this.amount = amount;
        this.type = type;
        this.card = card;
        this.createdAt = LocalDateTime.now();
    }

    public Transaction(
            BigDecimal amount,
            String type,
            Card fromCard,
            Card toCard,
            BigDecimal fromBalance,
            BigDecimal toBalance) {

        this.amount = amount;
        this.type = type;

        this.fromCard = fromCard;
        this.toCard = toCard;

        this.fromBalance = fromBalance;
        this.toBalance = toBalance;

        this.createdAt = LocalDateTime.now();
    }
}