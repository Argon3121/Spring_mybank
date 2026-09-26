package com.mybank.bank.controller;

import com.mybank.bank.model.Card;
import com.mybank.bank.model.User;
import com.mybank.bank.service.CardService;
import com.mybank.bank.service.TransactionService;
import com.mybank.bank.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;

@Controller
public class PageController {

    @Autowired
    private UserService userService;

    @Autowired
    private CardService cardService;

    @Autowired
    private TransactionService transactionService;

    @GetMapping("/")
    public String index(HttpSession session) {
        if (session.getAttribute("userId") == null) {
            return "redirect:/login";
        }
        return "redirect:/cards-page";
    }

    @GetMapping("/login")
    public String loginPage(HttpSession session) {
        if (session.getAttribute("userId") != null) {
            return "redirect:/cards-page";
        }
        return "login";
    }

    @PostMapping("/login")
    public String login(
            @RequestParam String email,
            @RequestParam String password,
            HttpSession session,
            RedirectAttributes attributes) {

        try {
            User user = userService.login(email, password);
            session.setAttribute("userId", user.getId());
            session.setAttribute("userName", user.getName());
            return "redirect:/cards-page";
        } catch (RuntimeException e) {
            attributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/login";
        }
    }

    @GetMapping("/register")
    public String registerPage(HttpSession session) {
        if (session.getAttribute("userId") != null) {
            return "redirect:/cards-page";
        }
        return "register";
    }

    @PostMapping("/register")
    public String register(
            @RequestParam String name,
            @RequestParam String email,
            @RequestParam String password,
            RedirectAttributes attributes) {

        try {
            User user = new User();
            user.setName(name);
            user.setEmail(email);
            user.setPassword(password);
            userService.create(user);

            attributes.addFlashAttribute(
                    "success",
                    "Аккаунт создан. Теперь войдите в систему."
            );

            return "redirect:/login";
        } catch (RuntimeException e) {
            attributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/register";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }

    @GetMapping("/cards-page")
    public String cardsPage(HttpSession session, Model model) {
        Long userId = getUserId(session);
        if (userId == null) {
            return "redirect:/login";
        }

        User user = userService.getById(userId);
        List<Card> cards = cardService.getByUserId(userId);

        model.addAttribute("user", user);
        model.addAttribute("cards", cards);
        model.addAttribute("totalBalance", getTotalBalance(cards));

        return "cards";
    }

    @PostMapping("/cards-page/create")
    public String createCard(
            HttpSession session,
            RedirectAttributes attributes) {

        Long userId = getUserId(session);
        if (userId == null) {
            return "redirect:/login";
        }

        try {
            cardService.createCard(userId);
            attributes.addFlashAttribute("success", "Новая карта создана");
        } catch (RuntimeException e) {
            attributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/cards-page";
    }

    @PostMapping("/cards-page/delete")
    public String deleteCard(
            @RequestParam Long cardId,
            HttpSession session,
            RedirectAttributes attributes) {

        Long userId = getUserId(session);
        if (userId == null) {
            return "redirect:/login";
        }

        try {
            List<Card> cards = cardService.getByUserId(userId);
            boolean ownCard = cards.stream()
                    .anyMatch(card -> card.getId().equals(cardId));

            if (!ownCard) {
                throw new RuntimeException("Это не ваша карта");
            }

            cardService.delete(cardId);
            attributes.addFlashAttribute("success", "Карта удалена");
        } catch (RuntimeException e) {
            attributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/cards-page";
    }

    @PostMapping("/cards-page/deposit")
    public String deposit(
            @RequestParam Long cardId,
            @RequestParam BigDecimal amount,
            HttpSession session,
            RedirectAttributes attributes) {

        if (getUserId(session) == null) {
            return "redirect:/login";
        }

        try {
            transactionService.deposit(cardId, amount);
            attributes.addFlashAttribute("success", "Карта пополнена");
        } catch (RuntimeException e) {
            attributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/cards-page";
    }

    @PostMapping("/cards-page/withdraw")
    public String withdraw(
            @RequestParam Long cardId,
            @RequestParam BigDecimal amount,
            HttpSession session,
            RedirectAttributes attributes) {

        if (getUserId(session) == null) {
            return "redirect:/login";
        }

        try {
            transactionService.withdraw(cardId, amount);
            attributes.addFlashAttribute("success", "Деньги сняты с карты");
        } catch (RuntimeException e) {
            attributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/cards-page";
    }

    @GetMapping("/transfer")
    public String transferPage(HttpSession session, Model model) {
        Long userId = getUserId(session);
        if (userId == null) {
            return "redirect:/login";
        }

        model.addAttribute("cards", cardService.getByUserId(userId));
        return "transfer";
    }

    @PostMapping("/transfer")
    public String transfer(
            @RequestParam String fromNumber,
            @RequestParam String toNumber,
            @RequestParam BigDecimal amount,
            HttpSession session,
            RedirectAttributes attributes) {

        if (getUserId(session) == null) {
            return "redirect:/login";
        }

        try {
            transactionService.transfer(fromNumber, toNumber, amount);
            attributes.addFlashAttribute("success", "Перевод выполнен");
        } catch (RuntimeException e) {
            attributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/transfer";
    }

    @GetMapping("/history")
    public String historyPage(
            @RequestParam(required = false) Long cardId,
            HttpSession session,
            Model model) {

        Long userId = getUserId(session);
        if (userId == null) {
            return "redirect:/login";
        }

        List<Card> cards = cardService.getByUserId(userId);
        model.addAttribute("cards", cards);

        if (cardId == null && !cards.isEmpty()) {
            cardId = cards.get(0).getId();
        }

        if (cardId != null) {
            Long finalCardId = cardId;
            boolean ownCard = cards.stream()
                    .anyMatch(card -> card.getId().equals(finalCardId));

            if (!ownCard) {
                return "redirect:/history";
            }

            Card selectedCard = cardService.getById(cardId);
            model.addAttribute("selectedCard", selectedCard);
            model.addAttribute("transactions", transactionService.getHistory(cardId));
        }

        return "history";
    }

    private Long getUserId(HttpSession session) {
        return (Long) session.getAttribute("userId");
    }

    private BigDecimal getTotalBalance(List<Card> cards) {
        BigDecimal total = BigDecimal.ZERO;

        for (Card card : cards) {
            if (card.getBalance() != null) {
                total = total.add(card.getBalance());
            }
        }

        return total;
    }
}
