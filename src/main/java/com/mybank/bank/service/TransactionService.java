package com.mybank.bank.service;

import com.mybank.bank.model.Card;
import com.mybank.bank.model.Transaction;
import com.mybank.bank.repository.CardRepository;
import com.mybank.bank.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class TransactionService {
    @Autowired
    private CardService cardService;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private CardRepository cardRepository;


    public Transaction deposit(Long cardId, BigDecimal amount) {

        Card card = cardRepository.findById(cardId)
                .orElseThrow(() ->
                        new RuntimeException("Карта не найдена"));

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Сумма должна быть больше 0");
        }

        card.setBalance(card.getBalance().add(amount));

        cardRepository.save(card);

        Transaction transaction = new Transaction(
                amount,
                "DEPOSIT",
                card
        );

        return transactionRepository.save(transaction);
    }


    public Transaction withdraw(Long cardId, BigDecimal amount) {

        Card card = cardRepository.findById(cardId)
                .orElseThrow(() ->
                        new RuntimeException("Карта не найдена"));

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Сумма должна быть больше 0");
        }

        if (card.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Недостаточно средств");
        }

        card.setBalance(card.getBalance().subtract(amount));

        cardRepository.save(card);

        Transaction transaction = new Transaction(
                amount,
                "WITHDRAW",
                card
        );

        return transactionRepository.save(transaction);
    }


    public List<Transaction> getHistory(Long cardId) {

        if (!cardRepository.existsById(cardId)) {
            throw new RuntimeException("Карта не найдена");
        }

        return transactionRepository.findByCardId(cardId);
    }

    public Transaction transfer(String fromNumber, String toNumber, BigDecimal amount) {

        Card fromCard = cardService.findCardByNumber(fromNumber);
        Card toCard = cardService.findCardByNumber(toNumber);

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Сумма должна быть больше 0");
        }

        if (fromCard.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Недостаточно средств");
        }

        fromCard.setBalance(
                fromCard.getBalance().subtract(amount)
        );

        toCard.setBalance(
                toCard.getBalance().add(amount)
        );

        cardRepository.save(fromCard);
        cardRepository.save(toCard);

        Transaction transaction = new Transaction(
                amount,
                "TRANSFER",
                fromCard,
                toCard,
                fromCard.getBalance(),
                toCard.getBalance()
        );

        return transactionRepository.save(transaction);
    }
}