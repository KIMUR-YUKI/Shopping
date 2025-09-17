package com.example.shopping.service;

import java.util.Collections;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.shopping.model.User;
import com.example.shopping.repository.UserRepository;

//Spring SecurityがDBからユーザーを取得できるようにするクラス
@Service
public class CustomUserDetailsService implements UserDetailsService{
    private final UserRepository userRepository;//ユーザDBの情報をこのクラスで紐づけ

    public CustomUserDetailsService(UserRepository userRepository){
        this.userRepository = userRepository;
    }
    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException{
        User user = userRepository.findByUserId(userId)
            .orElseThrow(() -> new UsernameNotFoundException("ユーザーが見つかりません: " + userId));

        return new org.springframework.security.core.userdetails.User(
            user.getUserId(),  // ← userId を username として扱う
            user.getPassword(),
            Collections.singletonList(new SimpleGrantedAuthority(user.getRole()))
        );
    }
}
