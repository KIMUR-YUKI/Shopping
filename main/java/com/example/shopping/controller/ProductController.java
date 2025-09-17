package com.example.shopping.controller;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.example.shopping.cart.CartItem;
import com.example.shopping.model.Product;
import com.example.shopping.model.User;
import com.example.shopping.repository.ProductRepository;
import com.example.shopping.repository.UserRepository;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/products")
public class ProductController {
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/new")
    public String showCreateForm(Model model, Principal principal){
        model.addAttribute("product",new Product());
        //ログインユーザーの表示（権限付き）
        if(principal != null){
            String userName = principal.getName();
            User user = userRepository.findByUserId(userName)
                        .orElseThrow(() -> new RuntimeException("ユーザーが見つかりません：" + userName));
            model.addAttribute("loginUser", user);
        }
        return "product-form";
    }

    @PostMapping("/new")
    public String createProduct(@ModelAttribute Product product, @RequestParam(name = "imageFiles") MultipartFile[] imageFiles){
        List<String> imagePaths = new ArrayList<>();
        
        for(MultipartFile imageFile : imageFiles){
            if(!imageFile.isEmpty()){
                try {
                    String fileName = UUID.randomUUID() + "_" + imageFile.getOriginalFilename();
                    Path path = Paths.get("uploads/" + fileName);
                    Files.copy(imageFile.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
                    imagePaths.add(fileName);//DB用にファイル名だけ保存
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        product.setImagePaths(imagePaths);//リストで保存
        productRepository.save(product);
        return "redirect:/products";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model){
        Product product = productRepository.findById(id).orElseThrow(()-> new IllegalArgumentException("Invalid ID:" + id));
        model.addAttribute("product", product);
        return "product-edit";
    }
    
    @PostMapping("/edit/{id}")
    public String updateProduct(@PathVariable Long id, @ModelAttribute Product product){
        product.setId(id);
        // product.setCategory(product.getCategory());
        productRepository.save(product);
        return "redirect:/products";
    }
    
    @GetMapping("/delete")
    public String deleteProduct(@RequestParam Long id){
        productRepository.deleteById(id);
        return "redirect:/products";
    }

    @GetMapping
    public String listProducts(@RequestParam(name = "keyword", required = false, defaultValue = "")String keyword,
                                @RequestParam(name = "category", required = false, defaultValue = "")String category,
                                Model model, HttpSession session, Principal principal){
        //検索処理：商品名とカテゴリで部分一致検索
        List<Product> products = productRepository.findByNameContainingAndCategoryContaining(keyword, category);

        model.addAttribute("products", products);//商品一覧をviewに渡す
        model.addAttribute("keyword", keyword);//検索フォームに保存させる用
        model.addAttribute("category", category);//同上

        if(principal != null){
            String userName = principal.getName();
            User user = userRepository.findByUserId(userName)
                        .orElseThrow(() -> new RuntimeException("ユーザーが見つかりません：" + userName));
            model.addAttribute("loginUser", user);
        }
        //カート情報を渡す
        Map<Long, CartItem> cart = (Map<Long, CartItem>) session.getAttribute("cart");
        if(cart == null) cart = new HashMap<>();
        model.addAttribute("cart", cart);
        return "product-list";
    }

    //商品の詳細表示
    @GetMapping("/{id}")
    public String showDetail(@PathVariable Long id, Model model, Principal principal){
        // Productの中からIDを取りだす
        Product product = productRepository.findById(id).orElse(null);
        if(product == null)//productが空だったら
            return "redirect:/products";//商品一覧へリダイレクト
        model.addAttribute("product", product);//商品一覧をビューへ渡す

        //ログインユーザーの表示（権限付き）
        if(principal != null){
            String userName = principal.getName();
            User user = userRepository.findByUserId(userName)
                        .orElseThrow(() -> new RuntimeException("ユーザーが見つかりません：" + userName));
            model.addAttribute("loginUser", user);
        }
        return "product-detail";
    }
}
