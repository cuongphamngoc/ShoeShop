package com.cuongpn.shoeshop.controller;

import com.cuongpn.shoeshop.dto.AddCartRequest;
import com.cuongpn.shoeshop.dto.CartDTO;
import com.cuongpn.shoeshop.dto.CartItemDTO;
import com.cuongpn.shoeshop.dto.UpdateQuantityRequest;
import com.cuongpn.shoeshop.entity.Cart;
import com.cuongpn.shoeshop.entity.CartItem;
import com.cuongpn.shoeshop.entity.User;
import com.cuongpn.shoeshop.mapper.CartItemMapper;
import com.cuongpn.shoeshop.service.CartItemService;
import com.cuongpn.shoeshop.service.CartService;
import com.cuongpn.shoeshop.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Controller
@AllArgsConstructor
public class ShoppingCartController {
    private final CartService cartService;
    private final UserService userService;
    private final CartItemService cartItemService;

    private final CartItemMapper cartItemMapper;
    @GetMapping("/shopping-cart")
    public String getShoppingCart(Model model, Principal principal){
        User user = userService.findByUserName(principal.getName());
        model.addAttribute("shoppingCart",cartService.getCartDTOByUser(user));
        return "shopping-cart";
    }
    @PostMapping("/shopping-cart/add")
    public @ResponseBody ResponseEntity<?> addToCart(@RequestBody AddCartRequest addCartRequest, Principal principal){
        System.out.println(addCartRequest);
        User user = userService.findByUserName(principal.getName());
        return ResponseEntity.ok(cartItemService.addItemToCart(addCartRequest,user));
    }
    @PostMapping("/shopping-cart/delete")
    public @ResponseBody ResponseEntity<?> deleteCartItem(@RequestBody List<Long> cartIds,Principal principal){
        System.out.println(cartIds);
        User user = userService.findByUserName(principal.getName());
        return ResponseEntity.ok(cartService.deleteCartItem(cartIds,user));

    }
    @PostMapping("/shopping-cart/update-quantity")
    public @ResponseBody ResponseEntity<?> updateQuantity(@RequestBody UpdateQuantityRequest updateQuantityRequest, Principal principal ){
        User user = userService.findByUserName(principal.getName());
        return cartService.updateCartItem(user,updateQuantityRequest);

    }
}
