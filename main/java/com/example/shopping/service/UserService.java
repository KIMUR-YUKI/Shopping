package com.example.shopping.service;


import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.shopping.model.User;
import com.example.shopping.repository.UserRepository;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }


    public void register(User user){
        //パスワード暗号化
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        //権限はデフォルトで一般ユーザー
        user.setRole("ROLE_USER");


        userRepository.save(user);
    }
}
