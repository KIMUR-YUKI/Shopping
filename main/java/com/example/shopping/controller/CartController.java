package com.example.shopping.controller;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.shopping.cart.CartItem;
import com.example.shopping.model.Order;
import com.example.shopping.model.OrderItem;
import com.example.shopping.model.Product;
import com.example.shopping.model.User;
import com.example.shopping.repository.OrderRepository;
import com.example.shopping.repository.ProductRepository;
import com.example.shopping.repository.UserRepository;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/cart")
// @SessionAttributes("order")
public class CartController {
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;




    //商品をカートに追加
    @PostMapping("/add/{productId}")
    public String addToCart(@PathVariable Long productId , HttpSession session){
        //1.商品IDから商品を取得
        Product product = productRepository.findById(productId).orElse(null);
        if(product == null)return "redirect:/products";

        //カート取得orなければ新規作成
        Map<Long, CartItem> cart = (Map<Long, CartItem>)session.getAttribute("cart");
        if(cart == null) cart = new HashMap<>();
        
        //3.既に入っていれば数量＋１、新規なら新しく追加
        CartItem item = cart.get(productId);
        if(item != null){
            item.increaseQuantity(1);//すでに入っていた→数量+1
        }else{
            cart.put(productId, new CartItem(product, 1L));//初めて→新規追加
        }

        //4.セッションに保存し直す
        session.setAttribute("cart", cart);
        
        

        //5.カート画面へリダイレクト
        return "redirect:/products";
    }

    @PostMapping("/increase/{productId}")
    public String increaseQuantity(@PathVariable Long productId, HttpSession session){
        Map<Long, CartItem> cart = (Map<Long, CartItem>) session.getAttribute("cart");
        if(cart != null && cart.containsKey(productId)){
            cart.get(productId).increaseQuantity(1);
            session.setAttribute("cart", cart);
        }
        return "redirect:/products";
        
    }

    @PostMapping("/decrease/{productId}")
    public String decreaseQuantity(@PathVariable Long productId, HttpSession session){
        Map<Long, CartItem> cart = (Map<Long, CartItem>) session.getAttribute("cart");
        if(cart != null && cart.containsKey(productId)){
            CartItem item = cart.get(productId);
            if(item.getQuantity() > 1){
                item.increaseQuantity(-1);
            }else{
                cart.remove(productId);//数量が1→0で削除
            }
            session.setAttribute("cart", cart);
        }
        return "redirect:/products";
        
    }

    

    //カート画面の表示
    @GetMapping("/view")
    public String viewCart(HttpSession session, Model model, Principal principal){
        //セッションからカートを取得（なければ空のMap）
        Map<Long, CartItem> cart = (Map<Long, CartItem>) session.getAttribute("cart");
        if(cart == null) cart = new HashMap<>();

        //ThymeLeafに渡すため、cartの中身（value)だけ渡す
        model.addAttribute("cartItems", cart.values());

        //ログインユーザーの表示（権限付き）
        if(principal != null){
            String userName = principal.getName();
            User user = userRepository.findByUserId(userName)
                        .orElseThrow(() -> new RuntimeException("ユーザーが見つかりません：" + userName));
            model.addAttribute("loginUser", user);
        }

        //合計金額を計算
        Long total = cart.values().stream()
                    .mapToLong(CartItem::getSubtotal).sum();//合計を計算
        model.addAttribute("totalPrice", total);
        return "cart";  //cart.htmlへ
    }

    //カート画面の数量選択（増加）
    @PostMapping("/view/increase/{productId}")
    public String viewIncreaseQuantity(@PathVariable Long productId, HttpSession session){
        Map<Long, CartItem> cart = (Map<Long, CartItem>) session.getAttribute("cart");
        if(cart != null && cart.containsKey(productId)){
            cart.get(productId).increaseQuantity(1);
            session.setAttribute("cart", cart);
        }
        return "redirect:/cart/view";
        
    }

    

    //カート画面の数量選択（減少）
    @PostMapping("/view/decrease/{productId}")
    public String viewDecreaseQuantity(@PathVariable Long productId, HttpSession session){
        Map<Long, CartItem> cart = (Map<Long, CartItem>) session.getAttribute("cart");
        if(cart != null && cart.containsKey(productId)){
            CartItem item = cart.get(productId);
            if(item.getQuantity() > 1){
                item.increaseQuantity(-1);
            }else{
                cart.remove(productId);//数量が1→0で削除
            }
            session.setAttribute("cart", cart);
        }
        return "redirect:/cart/view";
        
    }

    //商品をカートから削除
    @GetMapping("/remove/{productId}")
    public String removeFromCart(@PathVariable Long productId, HttpSession session){
        //カートをセッションから取得
        Map<Long, CartItem> cart = (Map<Long, CartItem>) session.getAttribute("cart");
        if(cart != null){
            cart.remove(productId);//商品IDをキーに削除
            session.setAttribute("cart", cart);//セッション更新
        }

        return "redirect:/cart/view";//カート画面へ戻る
    }

    //注文の手続きメソッド
    @GetMapping("/form")
    public String showOrderForm(@ModelAttribute("order") Order order,HttpSession session,Model model){
        //カート確認（空なら戻す）
        Map<Long, CartItem> cart = (Map<Long, CartItem>) session.getAttribute("cart");
        if(cart == null || cart.isEmpty()){
            model.addAttribute("errorMessage" , "カートに商品がありません。");
            return "redirect:/cart/view";
        }


        //model.addAttribute("order",order);
        //ThymeLeafに渡すため、cartの中身（value)だけ渡す
        model.addAttribute("cartItems", cart.values());
        //合計金額を計算
        Long total = cart.values().stream()
                    .mapToLong(CartItem::getSubtotal).sum();//合計を計算
        
        model.addAttribute("totalPrice", total);
        return "order-form"; //order-form.htmlへ
    }

    // @ModelAttribute("order")
    // public Order setupOrder() {
    //     return new Order(); // 最初の一度だけ呼ばれる。セッションに自動で保存される
    // }


    //注文手続きでのカート画面の数量選択（増加）
    @PostMapping("/form/increase/{productId}")
    public String showOrderIncreaseQuantity(@PathVariable Long productId, 
                                            @ModelAttribute("order")Order order, HttpSession session){
        // 🧪 セッションから復元されたorderの中身をログ出力！
        System.out.println("【数量＋1】orderの名前: " + order.getName());
        System.out.println("【数量＋1】支払方法: " + order.getPaymentMethod());
        
        Map<Long, CartItem> cart = (Map<Long, CartItem>) session.getAttribute("cart");
        if(cart != null && cart.containsKey(productId)){
            CartItem item = cart.get(productId);
            item.increaseQuantity(1);
            //cart.get(productId).increaseQuantity(1);
            session.setAttribute("cart", cart);
        }
        
        //session.setAttribute("order", order);

        return "redirect:/cart/form";
        
    }

//注文手続きでのカート画面の数量選択（減少）
    @PostMapping("/form/decrease/{productId}")
    public String showOrderDecreaseQuantity(@PathVariable Long productId, 
                                            @ModelAttribute("order")Order order, HttpSession session){
         // 🧪 セッションから復元されたorderの中身をログ出力！
        System.out.println("【数量－1】orderの名前: " + order.getName());
        System.out.println("【数量－1】支払方法: " + order.getPaymentMethod());

        Map<Long, CartItem> cart = (Map<Long, CartItem>) session.getAttribute("cart");
        if(cart != null && cart.containsKey(productId)){//カートの中身がある場合
            CartItem item = cart.get(productId);//itemにカートIDを格納
            if(item.getQuantity() > 1){//カートが1より大きい場合
                item.increaseQuantity(-1);//1減らす
            }else{
                cart.remove(productId);//数量が1→0で削除
            }
            session.setAttribute("cart", cart);
        }

        //session.setAttribute("order", order);

        return "redirect:/cart/form";
        
    }

    // 注文手続き画面でのカート商品削除
    // @GetMapping("form/remove/{productId}")
    // public String formRemoveFromCart(@PathVariable Long productId, HttpSession session){
    //     //カートをセッションから取得
    //     Map<Long, CartItem> cart = (Map<Long, CartItem>) session.getAttribute("cart");
    //     if(cart != null){
    //         cart.remove(productId);//商品IDをキーに削除
    //         session.setAttribute("cart", cart);//セッション更新
    //     }

    //     return "redirect:/cart/form";
    // }

    //注文完了画面表示のメソッド
    @GetMapping("/complete")
    public String purchaseComplete(@ModelAttribute("orderId") Long orderId, Model model){
        //ビューへorderIdが表示されるようにする
        
        model.addAttribute("orderId", orderId);
        return "complete";//complete.htmlを表示
    }
    
    //注文確定ボタンを押した後に処理されるメソッド
    @PostMapping("/checkout")
    public String checkout(HttpSession session, RedirectAttributes redirectAttributes){
        
        Order order = new Order();
        
        //カートをセッションから取得
        Map<Long, CartItem> cart = (Map<Long, CartItem>) session.getAttribute("cart");
        
        //カートに商品がない or 空の場合
        if(cart == null || cart.isEmpty()){
            redirectAttributes.addFlashAttribute("errorMessage", "カートに商品がありません。");
            return "redirect:/cart/view";//カート一覧に戻る
        }

        //ログイン中のユーザーを取得
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String loginId = auth.getName();//Spring Securityが保持している"userId"
        User user = userRepository.findByUserId(loginId)
                    .orElseThrow(() -> new RuntimeException("ユーザーが見つかりません"));
        
        order.setId(null);
        order.setUser(user);//Orderに紐づけ
        order.setOrderDate(LocalDateTime.now());//現在の日時をセット
        

        List<OrderItem> orderItems = new ArrayList<>();//購入商品のリスト
        Long total = 0L;//合計金額の初期値

        //カートの中身を1つずつ注文項目（OrderItem）として交換
        for(CartItem cartItem : cart.values()){
            OrderItem item = new OrderItem();
            item.setProductName(cartItem.getProduct().getName());//商品名をセット
            item.setQuantity((int)cartItem.getQuantity().longValue());//数量
            item.setPrice(cartItem.getSubtotal());//小計（単価×数量）
            item.setProduct(cartItem.getProduct());//永続化済みのProductをセット
            
            item.setOrder(order);//このアイテムが属する注文を設定（紐づけ）
            orderItems.add(item);//リストに追加
            
            total += item.getPrice();//合計に加算
        }

        order.setItems(orderItems);//商品リストをセット
        order.setTotalPrice(order.calculateTotalPrice());//合計金額をセット
        orderRepository.save(order);//データベースに保存（OrderとOrderItem）
        
        session.removeAttribute("cart");//購入後はカートを空にする
        
        //完了画面に注文番号を渡す
        redirectAttributes.addFlashAttribute("orderId", order.getId());

        // 完了ページへ遷移
        return "redirect:/cart/complete";

    }

    //商品詳細ページから「カートに追加」ボタンが押されたときに実行されるメソッド
    @PostMapping("/add/detail")
    public String productDetailAddToCart(@RequestParam("productId") Long productId, HttpSession session){
        
        //指定されたproductIdに対応する商品をデータベースから取得
        Product product = productRepository.findById(productId).orElse(null);
        if(product == null) return "redirect:/products";//商品が空なら一覧へ戻る

        
        //セッションから現在のカート情報を取得
        //なければnullが返ってくる
        Map<Long, CartItem> cart = (Map<Long, CartItem>)session.getAttribute("cart");//Map型で扱う
        
        //カートが存在しない場合（＝はじめて追加する時）は新しく作成
        if(cart == null){
            cart = new HashMap<>();
        }
        CartItem item = cart.get(productId);//カートにすでに同じ商品が入っていればその商品を取得
        if(item != null){//カートに商品がある場合
            item.increaseQuantity(1);//数量を＋１にする
        }else{
            //初めて追加される商品なら数量１で新しくCartItemを作って追加
            cart.put(productId,new CartItem(product,1L));
        }
        //カートの状態をセッションに保存（次の画面でも使えるように）
        session.setAttribute("cart", cart);
        //カート表示画面へリダイレクト
        return "redirect:/cart/view";
    }
}
