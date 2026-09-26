package com.mybank.bank.service;

import com.mybank.bank.model.User;
import com.mybank.bank.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public List<User> getAll() {
        return userRepository.findAll();
    }

    public User getById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Пользователь не найден"));
    }

    public User create(User user) {

        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException(
                    "Пользователь уже существует");
        }

        return userRepository.save(user);
    }

    public User login(String email, String password) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Пользователь не найден"));

        if (!user.getPassword().equals(password)) {
            throw new RuntimeException(
                    "Неверный пароль");
        }

        return user;
    }

    public void delete(Long id) {

        if (!userRepository.existsById(id)) {
            throw new RuntimeException(
                    "Пользователь не найден");
        }

        userRepository.deleteById(id);
    }
}