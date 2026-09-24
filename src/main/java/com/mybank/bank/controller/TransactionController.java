package com.mybank.bank.controller;

import com.mybank.bank.model.Transaction;
import com.mybank.bank.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;


    @PostMapping("/{cardId}/deposit")
    public Transaction deposit(
            @PathVariable Long cardId,
            @RequestBody Map<String, String> body) {

        BigDecimal amount =
                new BigDecimal(body.get("amount"));

        return transactionService.deposit(
                cardId,
                amount
        );
    }


    @PostMapping("/{cardId}/withdraw")
    public Transaction withdraw(
            @PathVariable Long cardId,
            @RequestBody Map<String, String> body) {

        BigDecimal amount =
                new BigDecimal(body.get("amount"));

        return transactionService.withdraw(
                cardId,
                amount
        );
    }

    @GetMapping("/{cardId}/history")
    public List<Transaction> getHistory(
            @PathVariable Long cardId) {

        return transactionService.getHistory(cardId);
    }
    @PostMapping("/transfer")
    public Transaction transfer(
            @RequestBody Map<String, String> body) {

        String fromNumber = body.get("fromNumber");
        String toNumber = body.get("toNumber");

        BigDecimal amount =
                new BigDecimal(body.get("amount"));

        return transactionService.transfer(
                fromNumber,
                toNumber,
                amount
        );
    }
}