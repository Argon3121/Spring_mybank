package com.mybank.bank.repository;

import com.mybank.bank.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository
        extends JpaRepository<Transaction, Long> {

    List<Transaction> findByCardIdOrFromCardIdOrToCardIdOrderByCreatedAtDesc(
            Long cardId,
            Long fromCardId,
            Long toCardId
    );
}