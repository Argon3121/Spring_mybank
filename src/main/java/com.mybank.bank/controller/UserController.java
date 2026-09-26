package com.mybank.bank.controller;

import com.mybank.bank.model.User;
import com.mybank.bank.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public List<User> getAll() {
        return userService.getAll();
    }

    @GetMapping("/{id}")
    public User getById(@PathVariable Long id) {
        return userService.getById(id);
    }

    @PostMapping("/register")
    public User register(@RequestBody Map<String, String> body) {

        User user = new User();

        user.setName(body.get("name"));
        user.setEmail(body.get("email"));
        user.setPassword(body.get("password"));

        return userService.create(user);
    }

    @PostMapping("/login")
    public User login(@RequestBody Map<String, String> body) {

        String email = body.get("email");
        String password = body.get("password");

        return userService.login(email, password);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        userService.delete(id);
    }
}