package com.example.shopping.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;

@Entity
@Table(name = "orders")
public class Order implements Serializable{
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;//主キー

    @Column
    private Long totalPrice;//合計金額

    @Column
    private LocalDateTime orderDate;//注文日時

    @Column
    private String name;//商品名

    @Column
    private String postalCode;//郵便番号

    @Column
    private String address;//住所

    @Column
    private String phoneNumber;//電話番号

    @Column
    private String paymentMethod;//お支払方法

    @Column
    private String note;//その他備考

    //一つの注文（Order）が複数の注文商品（OrderItem）を持つという関係
    @OneToMany(mappedBy = "order" , cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @OrderBy("id DESC")
    private List<OrderItem> items = new ArrayList<>();

    //ユーザーとの紐づけを追加
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    
    public Order(){}

    

    public Order(Long id, Long totalPrice, LocalDateTime orderDate, String name, String postalCode,
            String address, String phoneNumber, String paymentMethod, String note, List<OrderItem> items
            , User user) {
        this.id = id;
        this.totalPrice = totalPrice;
        this.orderDate = orderDate;
        this.name = name;
        this.postalCode = postalCode;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.paymentMethod = paymentMethod;
        this.note = note;
        this.items = items;
        this.user = user;
    }



    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Long totalPrice) {
        this.totalPrice = totalPrice;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }



    public String getName() {
        return name;
    }



    public void setName(String name) {
        this.name = name;
    }



    public String getPostalCode() {
        return postalCode;
    }



    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }



    public String getAddress() {
        return address;
    }



    public void setAddress(String address) {
        this.address = address;
    }



    public String getPhoneNumber() {
        return phoneNumber;
    }



    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }



    public String getPaymentMethod() {
        return paymentMethod;
    }



    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }



    public String getNote() {
        return note;
    }



    public void setNote(String note) {
        this.note = note;
    }



    public User getUser() {
        return user;
    }



    public void setUser(User user) {
        this.user = user;
    }

    public Long calculateTotalPrice(){
        return items.stream()
            .mapToLong(item -> item.getProduct().getPrice() * item.getQuantity())
            .sum();
    }
}
