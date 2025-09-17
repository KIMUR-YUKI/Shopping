package com.example.shopping.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.example.shopping.cart.CartItem;

import jakarta.servlet.http.HttpSession;

@RestController//REST APIを返すコントローラーとして定義（JSONを返す）
@RequestMapping("/cart/api")//このクラスのすべてのURLは/cart/apiで始まる
public class CartRestController {

    @PostMapping("/increase/{productId}")//数量を増やすAPI（POSTリクエスト）
    public Map<String, Object> increase(@PathVariable Long productId, HttpSession session) {
        return updateQuantity(productId, 1, session);//数量を1増やす処理を呼び出す
    }

    @PostMapping("/decrease/{productId}")//数量を減らすAPI（POSTリクエスト）
    public Map<String, Object> decrease(@PathVariable Long productId, HttpSession session) {
        return updateQuantity(productId, -1, session);//数量を1減らす処理を呼び出す
    }

    //共通化した処理。amountが1なら増加、‐1なら減少
    private Map<String, Object> updateQuantity(Long productId, int amount, HttpSession session) {
        //セッションからカート情報を取得(Map<商品ID、CartItem>)
        Map<Long, CartItem> cart = (Map<Long, CartItem>) session.getAttribute("cart");
        if (cart == null || !cart.containsKey(productId)) {//カートがない、商品がカートに存在しない場合
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "商品がカートにありません");
        }

        CartItem item = cart.get(productId);
        item.increaseQuantity(amount);//+1または-1
        
        if (item.getQuantity() <= 0) {//数量が0以下なら
            cart.remove(productId);//カートから削除
        }

        session.setAttribute("cart", cart);//変更後のカートをセッションに保存

        long totalPrice = cart.values().stream()
            .mapToLong(CartItem::getSubtotal)
            .sum();//カート全体の合計金額を計算

        Map<String, Object> result = new HashMap<>();
        result.put("quantity", item.getQuantity());//商品の数量
        result.put("subtotal", item.getSubtotal());//商品の小計
        result.put("totalPrice", totalPrice);//カート商品全体の合計
        return result;//これをJSON形式で返す
    }

    @DeleteMapping("/remove/{productId}")
    public Map<String, Object> removeItem(@PathVariable Long productId, HttpSession session){
        Map<Long, CartItem> cart = (Map<Long, CartItem>) session.getAttribute("cart");

        if(cart != null){
            cart.remove(productId); //該当商品を削除
            session.setAttribute("cart", cart);//カートを更新
        }

        //合計金額を再計算
        long totalPrice = cart.values().stream()
            .mapToLong(CartItem::getSubtotal)
            .sum();
        Map<String, Object> result = new HashMap<>();
        result.put("totalPrice", totalPrice); //新しい合計金額だけ返す
        return result;
    }
}
