package com.example.shopping.cart;

import com.example.shopping.model.Product;

//カートに入れる１つの商品を表すクラス（商品情報と個数を保持）
public class CartItem {
    private Product product;//商品の情報（名前、価格など）
    private Long quantity;   //カートに入っている個数
    

    //商品と数量を指定して初期化する
    public CartItem(Product product, Long quantity){
        this.product = product;
        this.quantity = quantity;
    }

    //getter（外から値を取得するためのメソッド）
    public Product getProduct() {
        return product;
    }

    public Long getQuantity() {
        return quantity;
    }
    
    

    // カートの個数を増やすメソッド
    public void increaseQuantity(int amount){
        this.quantity += amount;
    }
    
    public Long getSubtotal(){
        return product.getPrice() * Long.valueOf(quantity);
    }
}
