package com.mybank.bank.service;

import com.mybank.bank.model.Card;
import com.mybank.bank.repository.CardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CardService {

    @Autowired
    private CardRepository cardRepository;

    public List<Card> getAll() {
        return cardRepository.findAll();
    }

    public Card getById(Long id) {
        return cardRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Карта не найдена"));
    }

    public Card create(Card card) {

        if (cardRepository.existsByNumber(card.getNumber())) {
            throw new RuntimeException(
                    "Карта с таким номером уже существует");
        }

        return cardRepository.save(card);
    }

    public void delete(Long id) {

        if (!cardRepository.existsById(id)) {
            throw new RuntimeException(
                    "Карта не найдена");
        }

        cardRepository.deleteById(id);
    }
}