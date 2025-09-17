package com.example.shopping.controller;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.shopping.model.Order;
import com.example.shopping.model.User;
import com.example.shopping.repository.OrderRepository;
import com.example.shopping.repository.UserRepository;

@Controller
@RequestMapping("/orders")
public class OrderController {
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private UserRepository userRepository;

    //注文履歴を一覧表示する処理
    @GetMapping
    public String orderList(Model model, Principal principal){
        //現在ログイン中のユーザー名を取得
        String loginId = principal.getName();

        //DBからUserエンティティを取得
        User user = userRepository.findByUserId(loginId).orElseThrow(() -> new RuntimeException("ユーザーが見つかりません"));
        
        //DBからログインユーザーの注文履歴を取得（新しい順に並べる）
        List<Order> orders = orderRepository.findByUserOrderByOrderDateDesc(user);
        
        //日付ごとに注文をまとめる（新しい日付が先になるようにソート）
        Map<LocalDate, List<Order>> ordersByDate = orders.stream()
                .collect(Collectors.groupingBy(o -> o.getOrderDate().toLocalDate(),
                Collectors.toList()
                ))
                .entrySet().stream()
                .sorted((e1, e2) -> e2.getKey().compareTo(e1.getKey()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        java.util.LinkedHashMap::new
                ));

        //日ごとの合計を作る(Map<LocalDate, Long>)
        Map<LocalDate, Long> dailyTotals = orders.stream()
            .collect(Collectors.groupingBy(
                o -> o.getOrderDate().toLocalDate(),
                Collectors.summingLong(Order::getTotalPrice)
            ));
        
        //View（HTML）に渡す
        // model.addAttribute("orders", orders);
        model.addAttribute("ordersByDate", ordersByDate);
        model.addAttribute("dailyTotals", dailyTotals);
        return "order-list";//表示するテンプレート(order-list.html)
    }
}
