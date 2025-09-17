package com.example.shopping.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.shopping.model.User;
import com.example.shopping.service.UserService;

import jakarta.validation.Valid;

@Controller
public class RegistrationController {
    @Autowired
    private UserService userService;

    @GetMapping("/register")
    public String showRegisterForm(Model model){
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String register(@Valid User user, BindingResult result, RedirectAttributes redirectAttributes, Model model){
        if(result.hasErrors()){
        
            return "register";
        }
        //UserServiceクラスのメソッドを呼び出し
        userService.register(user);
        redirectAttributes.addFlashAttribute
        ("message", "会員登録が完了しました！ログインしてください。");
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String showLoginForm(){
        return "login";//login.htmlを返す
    }
}
