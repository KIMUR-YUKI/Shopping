package com.example.shopping.cart;

import java.util.ArrayList;
import java.util.List;

public class Cart {
    private List<CartItem> items = new ArrayList<>();

    public void addItem(CartItem item){
        //すでに商品があるかチェックして増やす処理など（必要に応じて）
        items.add(item);
    }

    public List<CartItem> getItems(){
        return items;
    }
}
