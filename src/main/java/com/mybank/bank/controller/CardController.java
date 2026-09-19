package com.mybank.bank.controller;

import com.mybank.bank.model.Card;
import com.mybank.bank.service.CardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cards")
public class CardController {

    @Autowired
    private CardService cardService;

    @GetMapping
    public List<Card> getAll() {
        return cardService.getAll();
    }

    @GetMapping("/{id}")
    public Card getById(@PathVariable Long id) {
        return cardService.getById(id);
    }

    @PostMapping
    public Card create(@RequestBody Card card) {
        return cardService.create(card);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        cardService.delete(id);
    }
}