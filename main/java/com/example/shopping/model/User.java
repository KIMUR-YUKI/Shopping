package com.example.shopping.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message="ログインIDは必須です。")
    @Size(min = 4, max = 15, message="ログインIDは4から15文字までにしてください。")
    @Column(nullable = false, unique = true)
    private String userId; //ログインID
    
    @NotBlank(message="ユーザー名は必須です。")
    @Column(nullable = false)
    private String userName;//表示名
    
    @NotBlank(message="メールアドレスは必須です。")
    @Email(message="メールアドレス形式ではありません。")
    @Column(nullable = false, unique = true)
    private String email; //メールアドレス

    @NotBlank(message="パスワードは必須です。")
    @Size(min = 4, max = 15, message="パスワードは4から15文字までにしてください。")
    @Column(nullable = false)
    private String password;//パスワードハッシュ化して保存

    @Column(nullable = false)
    private String role;// "ROLE_USER" or "ROLE_ADMIN"

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Order> orders = new ArrayList<>();

    public User(){

    }
    
    public User(Long id, String userId, String userName, String email, String password,  String role, List<Order> orders) {
        this.id = id;
        this.userId = userId;
        this.userName = userName;
        this.email = email;
        this.password = password;
        this.role = role;
        this.orders = orders;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }

    
}
