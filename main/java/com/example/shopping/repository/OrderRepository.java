package com.example.shopping.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.shopping.model.Order;
import com.example.shopping.model.User;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long>{
    List<Order> findByUserOrderByOrderDateDesc(User user);
}
