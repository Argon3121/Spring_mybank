package com.mybank.bank.repository;

import com.mybank.bank.model.Card;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CardRepository extends JpaRepository<Card, Long> {

    boolean existsByNumber(String number);
}