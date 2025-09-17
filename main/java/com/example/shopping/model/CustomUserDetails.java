// package com.example.shopping.model;

// import java.util.Collection;
// import java.util.stream.Collectors;

// import org.springframework.security.core.GrantedAuthority;
// import org.springframework.security.core.authority.SimpleGrantedAuthority;
// import org.springframework.security.core.userdetails.UserDetails;

// // UserDetailsを継承しているので
// // Spring Securityの認証クラスとして認識される
// public class CustomUserDetails implements UserDetails {
//     // 自作のUserクラスを持つように拡張する
//     private final User user;
		
//     public CustomUserDetails(User user) {
//         this.user = user;
//     }

//     @Override
//     public Collection<? extends GrantedAuthority> getAuthorities() {//ユーザーが持つ権限のリストを返す
//         //user.getAuthoritiesでDBから取得したユーザーの権限情報を取得
//         return user.getAuthorities().stream()
//             .map(authority -> new SimpleGrantedAuthority(authority.getAuthority()))
//             .collect(Collectors.toList());
//     }

//     @Override
//     public String getPassword() {
//         return user.getPassword(); // カスタム User エンティティのパスワードを返す
//     }

//     @Override
//     public String getUsername() {
//         return user.getUserId(); // カスタム User エンティティのユーザー名を返す
//     }

//     @Override
//     public boolean isAccountNonExpired() {
//         return true; // ユーザーアカウントが期限切れでないかを示す
//                      //trueを返して期限切れでないことをSpring Securityに伝える
//     }

//     @Override
//     public boolean isAccountNonLocked() {
//         return true; // ユーザーアカウントがロックされていないかを示す
//     }

//     @Override
//     public boolean isCredentialsNonExpired() {
//         return true; // ユーザーの資格情報（パスワード）が期限切れでないかを示す
//     }

//     @Override
//     public boolean isEnabled() {
//         return user.isEnabled(); // カスタム User エンティティでユーザーが有効かどうかを返す
//     }

//     // カスタム User エンティティにアクセスするための追加メソッド
//     public User getUser() {
//         return user;
//     }
// }
