package com.example.shopping.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.example.shopping.service.CustomUserDetailsService;

@Configuration

public class SecurityConfig {
    
    private final CustomUserDetailsService userDetailsService;

    public SecurityConfig(CustomUserDetailsService userDetailsService){
        this.userDetailsService = userDetailsService;
    }
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        // http.csrf(csrf -> csrf.disable());
        http
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/login","/register" , "/css/**", "/js/**" , "/images/**").permitAll()
            .requestMatchers("/admin/**").hasRole("ADMIN")//管理者だけ
            .anyRequest().authenticated()//それ以外はログイン必須
            )
            .formLogin(form -> form
                .loginPage("/login")//ログインページを表示するURL(GET)
                .defaultSuccessUrl("/products", true)//ログイン成功時に遷移するURL
                .permitAll()//これを付けたページはログイン無しでもアクセス出来る
            )
            .logout(logout -> logout
            .logoutSuccessUrl("/login?logout")
            .permitAll()
            );
            
        return http.build();
    }
        
    //これを書いておくとパスワードがハッシュ化されます
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception{
        return authConfig.getAuthenticationManager();
    }
}
