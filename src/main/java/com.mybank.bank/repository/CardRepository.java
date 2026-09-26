package com.mybank.bank.repository;

import com.mybank.bank.model.Card;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CardRepository extends JpaRepository<Card, Long> {

    Optional<Card> findByNumber(String number);

    boolean existsByNumber(String number);

    List<Card> findByOwnerId(Long ownerId);
}