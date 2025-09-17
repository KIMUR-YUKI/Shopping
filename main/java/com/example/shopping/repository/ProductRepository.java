package com.example.shopping.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.shopping.model.Product;

@Repository
public interface  ProductRepository extends JpaRepository<Product, Long>{
    //商品名にキーワードを含み、カテゴリーが一致する商品を取得
    List<Product> findByNameContainingAndCategoryContaining(String name, String category);
}
